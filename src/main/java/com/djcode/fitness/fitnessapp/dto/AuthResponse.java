package com.djcode.fitness.fitnessapp.dto;

import java.time.Instant;
import java.util.List;

/**
 * DTO for authentication responses containing JWT token
 */
public record AuthResponse(
        String token,
        String userId,
        String username,
        String email,
        List<String> roles,
        Instant issuedAt,
        Instant expiresAt
) {}
