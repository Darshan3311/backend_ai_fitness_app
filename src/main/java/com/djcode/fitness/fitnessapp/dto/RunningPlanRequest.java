package com.djcode.fitness.fitnessapp.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Running plan generation request")
public record RunningPlanRequest(
        @Schema(description = "Primary running goal", example = "Run a 5k") String goal,
        @Schema(description = "Timeframe to achieve the goal (human readable)", example = "8 weeks") String timeframe,
        @Schema(description = "Current fitness level", example = "beginner") String fitnessLevel
) {}

