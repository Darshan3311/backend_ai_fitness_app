package com.djcode.fitness.fitnessapp.dto;

import java.time.Instant;

public record ErrorResponse(
        String error,
        String message,
        int status,
        Instant timestamp,
        String path
) {}

