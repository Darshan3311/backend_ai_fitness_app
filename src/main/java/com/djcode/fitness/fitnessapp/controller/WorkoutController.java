package com.djcode.fitness.fitnessapp.controller;

import com.djcode.fitness.fitnessapp.dto.WorkoutRequest;
import com.djcode.fitness.fitnessapp.dto.WorkoutResponse;
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
@RequestMapping("/api/workout")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "AI Workout Generator", description = "Generate personalized workout routines using AI")
public class WorkoutController {

    private final AIService aiService;

    @PostMapping("/generate")
    @Operation(
            summary = "Generate AI-powered workout routine",
            description = """
                    Generate a personalized workout routine using Google's Gemini AI based on:
                    - Target muscle group (e.g., chest, back, legs, arms, full body)
                    - Workout duration in minutes
                    - Fitness level (beginner, intermediate, advanced)
                    
                    The AI will create a structured workout plan with exercises, sets, reps, and descriptions.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Workout generated successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = WorkoutResponse.class),
                            examples = @ExampleObject(
                                    name = "Workout Response",
                                    value = """
                                            {
                                                "exercises": [
                                                    {
                                                        "name": "Push-ups",
                                                        "sets": "3",
                                                        "reps": "10-12",
                                                        "description": "Classic bodyweight exercise targeting chest, shoulders, and triceps"
                                                    },
                                                    {
                                                        "name": "Incline Dumbbell Press",
                                                        "sets": "3",
                                                        "reps": "8-10",
                                                        "description": "Upper chest focused exercise with adjustable dumbbells"
                                                    }
                                                ]
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - JWT token required",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Unauthorized Error",
                                    value = "{\"error\": \"JWT token is missing or invalid\"}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "AI service error - fallback workout provided",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "AI Service Error",
                                    value = "{\"error\": \"AI service temporarily unavailable, fallback workout provided\"}"
                            )
                    )
            )
    })
    public ResponseEntity<WorkoutResponse> generateWorkout(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Workout generation parameters",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = WorkoutRequest.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Chest Workout - Beginner",
                                            value = "{\"targetMuscle\": \"chest\", \"durationInMinutes\": 30, \"fitnessLevel\": \"beginner\"}"
                                    ),
                                    @ExampleObject(
                                            name = "Back Workout - Intermediate",
                                            value = "{\"targetMuscle\": \"back\", \"durationInMinutes\": 45, \"fitnessLevel\": \"intermediate\"}"
                                    ),
                                    @ExampleObject(
                                            name = "Full Body - Advanced",
                                            value = "{\"targetMuscle\": \"full body\", \"durationInMinutes\": 60, \"fitnessLevel\": \"advanced\"}"
                                    )
                            }
                    )
            )
            @RequestBody WorkoutRequest request) {
        WorkoutResponse response = aiService.generateWorkout(request);
        return ResponseEntity.ok(response);
    }
}
