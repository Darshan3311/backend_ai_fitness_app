package com.djcode.fitness.fitnessapp.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Single running session entry")
public record RunningSession(
        @Schema(example = "Mon") String day,
        @Schema(example = "Easy Run") String type,
        @Schema(example = "3 km") String distance,
        @Schema(example = "Comfortable conversational pace") String notes
) {}

