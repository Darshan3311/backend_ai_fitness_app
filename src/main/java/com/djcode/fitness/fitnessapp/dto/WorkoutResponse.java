package com.djcode.fitness.fitnessapp.dto;

import java.util.List;

/**
 * DTO for workout generation responses containing a list of exercises
 */
public record WorkoutResponse(
        List<Exercise> exercises
) {}
