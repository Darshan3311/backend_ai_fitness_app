package com.djcode.fitness.fitnessapp.dto;

/**
 * DTO for workout generation requests
 */
public record WorkoutRequest(
        String targetMuscle,
        int durationInMinutes,
        String fitnessLevel
) {}
