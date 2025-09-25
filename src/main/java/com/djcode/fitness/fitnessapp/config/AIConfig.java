package com.djcode.fitness.fitnessapp.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Configuration
public class AIConfig {
    private static final Logger log = LoggerFactory.getLogger(AIConfig.class);

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.api.model}")
    private String model;

    @Bean
    public RestTemplate restTemplate() {
        SimpleClientHttpRequestFactory f = new SimpleClientHttpRequestFactory();
        f.setConnectTimeout(8000);
        f.setReadTimeout(25000);
        return new RestTemplate(f);
    }

    @Bean
    public GeminiClient geminiClient(RestTemplate restTemplate) {
        return new GeminiClient(restTemplate, apiKey, model);
    }

    public record GeminiRequest(Contents[] contents) {
        public static GeminiRequest of(String userText) {
            return new GeminiRequest(new Contents[]{new Contents(new Part[]{new Part(userText)})});
        }
    }

    public record Contents(Part[] parts) {}

    public record Part(String text) {}

    public static class GeminiClient {
        private final RestTemplate restTemplate;
        private final String apiKey;
        private final String model;

        private final String endpointPattern = "https://generativelanguage.googleapis.com/v1beta/models/%s:generateContent?key=%s";

        public GeminiClient(RestTemplate restTemplate, String apiKey, String model) {
            this.restTemplate = restTemplate;
            this.apiKey = apiKey;
            this.model = model;
        }

        /**
         * Returns raw textual aggregation of all parts of first candidate; null if failure.
         */
        public String generate(String prompt) {
            if (apiKey == null || apiKey.isBlank() || apiKey.equals("CHANGE_ME")) {
                log.warn("Gemini API key missing or placeholder");
                return null;
            }
            String url = endpointPattern.formatted(model, apiKey);
            GeminiRequest body = GeminiRequest.of(prompt);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(List.of(MediaType.APPLICATION_JSON));
            HttpEntity<GeminiRequest> entity = new HttpEntity<>(body, headers);
            try {
                ResponseEntity<Map> resp = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);
                if (!resp.getStatusCode().is2xxSuccessful()) {
                    log.error("Gemini non-2xx status {}", resp.getStatusCode());
                    return null;
                }
                Map<?, ?> map = resp.getBody();
                if (map == null) {
                    log.error("Gemini empty body");
                    return null;
                }
                // Check safety / error
                Object error = map.get("error");
                if (error != null) {
                    log.error("Gemini error payload: {}", error);
                    return null;
                }
                Object candidates = map.get("candidates");
                if (!(candidates instanceof List<?> list) || list.isEmpty()) {
                    log.error("Gemini no candidates field");
                    return null;
                }
                StringBuilder sb = new StringBuilder();
                for (Object c : list) {
                    if (c instanceof Map<?, ?> cm) {
                        Object content = cm.get("content");
                        if (content instanceof Map<?, ?> cMap) {
                            Object parts = cMap.get("parts");
                            if (parts instanceof List<?> pList) {
                                for (Object p : pList) {
                                    if (p instanceof Map<?, ?> pm) {
                                        Object t = pm.get("text");
                                        if (t != null) sb.append(t).append('\n');
                                    }
                                }
                            }
                        }
                        // Safety ratings (optional logging)
                        Object safety = cm.get("safetyRatings");
                        if (safety != null) log.debug("Gemini safety ratings: {}", safety);
                    }
                }
                String out = sb.toString().trim();
                if (out.isEmpty())
                    log.warn("Gemini produced empty aggregated text");
                else
                    log.debug("Gemini aggregated text length {}", out.length());
                return out.isBlank() ? null : out;
            } catch (Exception ex) {
                log.error("Gemini request failed: {}: {}", ex.getClass().getSimpleName(), ex.getMessage());
                return null;
            }
        }
    }
}
