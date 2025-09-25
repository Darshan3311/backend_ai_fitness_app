package com.djcode.fitness.fitnessapp.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenAPIConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("FitAI - AI-Powered Fitness & Nutrition Coach API")
                        .description("""
                                FitAI is an intelligent web application that serves as a virtual fitness and nutrition coach. 
                                Using a robust Java-based backend, the application connects to powerful AI (Google Gemini) to offer personalized guidance.
                                
                                ## Features
                                - 🔐 **User Authentication**: Secure JWT-based authentication
                                - 💪 **AI Workout Generator**: Generate personalized workout routines based on target muscles, duration, and fitness level
                                - 🥗 **AI Diet Planner**: Create customized meal plans based on dietary preferences and fitness goals
                                - 📊 **Progress Tracking**: Monitor your fitness journey with saved plans
                                
                                ## Getting Started
                                1. Register a new account using the `/api/auth/register` endpoint
                                2. Login to get your JWT token using the `/api/auth/login` endpoint
                                3. Use the token in the Authorization header for protected endpoints
                                4. Generate workout plans and diet plans using AI-powered endpoints
                                """)
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("FitAI Development Team")
                                .email("support@fitai.com")
                                .url("https://fitai.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8081")
                                .description("Development Server"),
                        new Server()
                                .url("https://api.fitai.com")
                                .description("Production Server")))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("JWT token for authentication. Get it from /api/auth/login")))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
    }
}
