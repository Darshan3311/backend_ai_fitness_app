package com.djcode.fitness.fitnessapp.dto;

/**
 * DTO representing a meal in a diet plan
 */
public record Meal(
        String name,
        String ingredients,
        String calories,
        String description
) {}
