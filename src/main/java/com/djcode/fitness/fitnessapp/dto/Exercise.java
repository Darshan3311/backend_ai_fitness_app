package com.djcode.fitness.fitnessapp.dto;

/**
 * DTO representing an individual exercise
 */
public record Exercise(
        String name,
        String sets,
        String reps,
        String description
) {}
