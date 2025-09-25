package com.djcode.fitness.fitnessapp.controller;

import com.djcode.fitness.fitnessapp.dto.DietRequest;
import com.djcode.fitness.fitnessapp.dto.DietResponse;
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
@RequestMapping("/api/diet")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "AI Diet Planner", description = "Generate personalized diet plans using AI")
public class DietController {

    private final AIService aiService;

    @PostMapping("/generate")
    @Operation(
            summary = "Generate AI-powered diet plan",
            description = """
                    Generate a personalized daily meal plan using Google's Gemini AI based on:
                    - Dietary preference (e.g., vegetarian, vegan, keto, paleo, omnivore)
                    - Fitness goal (e.g., weight loss, muscle gain, maintenance)
                    - Daily calorie target
                    - Food allergies or restrictions
                    
                    The AI will create a complete meal plan with breakfast, lunch, dinner, and snacks including ingredients, calories, and preparation descriptions.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Diet plan generated successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = DietResponse.class),
                            examples = @ExampleObject(
                                    name = "Diet Plan Response",
                                    value = """
                                            {
                                                "meals": [
                                                    {
                                                        "name": "Protein Oatmeal Bowl",
                                                        "ingredients": "Rolled oats, protein powder, banana, almond butter, berries",
                                                        "calories": "350",
                                                        "description": "High-protein breakfast with complex carbs and antioxidants"
                                                    },
                                                    {
                                                        "name": "Grilled Chicken Quinoa Salad",
                                                        "ingredients": "Grilled chicken breast, quinoa, mixed greens, cherry tomatoes, olive oil",
                                                        "calories": "450",
                                                        "description": "Balanced lunch with lean protein and complete grains"
                                                    },
                                                    {
                                                        "name": "Salmon with Roasted Vegetables",
                                                        "ingredients": "Atlantic salmon, broccoli, sweet potato, asparagus, herbs",
                                                        "calories": "500",
                                                        "description": "Omega-3 rich dinner with nutrient-dense vegetables"
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
                    description = "AI service error - fallback diet plan provided",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "AI Service Error",
                                    value = "{\"error\": \"AI service temporarily unavailable, fallback diet plan provided\"}"
                            )
                    )
            )
    })
    public ResponseEntity<DietResponse> generateDiet(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Diet plan generation parameters",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = DietRequest.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Weight Loss - Vegetarian",
                                            value = "{\"dietaryPreference\": \"vegetarian\", \"fitnessGoal\": \"weight loss\", \"dailyCalories\": 1600, \"allergies\": \"nuts\"}"
                                    ),
                                    @ExampleObject(
                                            name = "Muscle Gain - High Protein",
                                            value = "{\"dietaryPreference\": \"high protein\", \"fitnessGoal\": \"muscle gain\", \"dailyCalories\": 2200, \"allergies\": \"none\"}"
                                    ),
                                    @ExampleObject(
                                            name = "Keto Diet - Maintenance",
                                            value = "{\"dietaryPreference\": \"keto\", \"fitnessGoal\": \"maintenance\", \"dailyCalories\": 1800, \"allergies\": \"dairy\"}"
                                    )
                            }
                    )
            )
            @RequestBody DietRequest request) {
        DietResponse response = aiService.generateDiet(request);
        return ResponseEntity.ok(response);
    }
}
