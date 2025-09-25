package com.djcode.fitness.fitnessapp.controller;

import com.djcode.fitness.fitnessapp.dto.YogaPlanRequest;
import com.djcode.fitness.fitnessapp.dto.YogaPlanResponse;
import com.djcode.fitness.fitnessapp.service.AIService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/yoga")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "AI Yoga Plan", description = "Generate guided yoga session flows using AI")
public class YogaController {

    private final AIService aiService;

    @PostMapping("/generate")
    @Operation(
            summary = "Generate AI-powered yoga flow",
            description = """
                    Generate a structured yoga session based on a user's goal (e.g. Stress Relief, Morning Energy) and time availability.
                    The flow returns a sequential list of poses with hold times and concise guidance.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Yoga flow generated successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = YogaPlanResponse.class),
                            examples = @ExampleObject(
                                    name = "Yoga Plan Response",
                                    value = """
                                            {\n  \"poses\": [\n    {\n      \"name\": \"Mountain Pose\",\n      \"hold\": \"60 sec\",\n      \"description\": \"Stand tall, steady breath, lengthen through crown\"\n    },\n    {\n      \"name\": \"Forward Fold\",\n      \"hold\": \"45 sec\",\n      \"description\": \"Hinge from hips, soften knees, release neck\"\n    }\n  ]\n}\n                                            """
                            )
                    )
            )
    })
    public ResponseEntity<YogaPlanResponse> generateYoga(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Yoga plan generation parameters",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = YogaPlanRequest.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Stress Relief 20 min",
                                            value = "{\"goal\":\"Stress Relief\",\"durationInMinutes\":20}"
                                    ),
                                    @ExampleObject(
                                            name = "Morning Energy 15 min",
                                            value = "{\"goal\":\"Morning Energy\",\"durationInMinutes\":15}"
                                    )
                            }
                    )
            )
            @RequestBody YogaPlanRequest request) {
        return ResponseEntity.ok(aiService.generateYogaPlan(request));
    }
}

