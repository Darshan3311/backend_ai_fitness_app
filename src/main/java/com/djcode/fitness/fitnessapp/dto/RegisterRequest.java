package com.djcode.fitness.fitnessapp.dto;

/**
 * DTO for user registration requests
 */
public record RegisterRequest(
        String username,
        String email,
        String password
) {}
