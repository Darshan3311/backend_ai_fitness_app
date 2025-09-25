package com.djcode.fitness.fitnessapp.dto;

/**
 * DTO for diet plan generation requests
 */
public record DietRequest(
        String dietaryPreference,
        String fitnessGoal,
        int dailyCalories,
        String allergies
) {}
