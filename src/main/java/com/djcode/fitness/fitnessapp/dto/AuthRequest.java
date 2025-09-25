package com.djcode.fitness.fitnessapp.dto;

/**
 * DTO for authentication requests
 */
public record AuthRequest(
        String email,
        String password
) {}
