package com.djcode.fitness.fitnessapp.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Yoga plan generation request")
public record YogaPlanRequest(
        @Schema(description = "Primary goal of the session", example = "Stress Relief") String goal,
        @Schema(description = "Session duration in minutes", example = "20") int durationInMinutes
) {}

