package com.djcode.fitness.fitnessapp.dto;

import java.util.List;

/**
 * DTO for diet plan generation responses containing a list of meals
 */
public record DietResponse(
        List<Meal> meals
) {}
