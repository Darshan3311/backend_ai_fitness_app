package com.djcode.fitness.fitnessapp.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Yoga pose item")
public record YogaPose(
        @Schema(example = "Mountain Pose") String name,
        @Schema(example = "60 sec") String hold,
        @Schema(example = "Stand tall, grounding through feet, lengthen spine and breathe deeply") String description
) {}

