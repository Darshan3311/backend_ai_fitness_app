package com.djcode.fitness.fitnessapp.controller;

import com.djcode.fitness.fitnessapp.dto.AuthRequest;
import com.djcode.fitness.fitnessapp.dto.AuthResponse;
import com.djcode.fitness.fitnessapp.dto.RegisterRequest;
import com.djcode.fitness.fitnessapp.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Authentication", description = "User authentication and registration endpoints")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @Operation(
            summary = "Register a new user",
            description = "Create a new user account with username, email, and password. Returns JWT + metadata upon successful registration."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "User registered successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AuthResponse.class),
                            examples = @ExampleObject(
                                    name = "Registration Success",
                                    value = "{" +
                                            "\"token\":\"eyJhbGciOi...\"," +
                                            "\"userId\":1," +
                                            "\"username\":\"john_doe\"," +
                                            "\"email\":\"john@example.com\"," +
                                            "\"roles\":[\"ROLE_USER\"]," +
                                            "\"issuedAt\":\"2025-09-25T07:00:00Z\"," +
                                            "\"expiresAt\":\"2025-09-26T07:00:00Z\"}" )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid registration data or user already exists",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Registration Error",
                                    value = "{\"error\":\"Email already registered\"}"
                            )
                    )
            )
    })
    public ResponseEntity<AuthResponse> register(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "User registration details",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = RegisterRequest.class),
                            examples = @ExampleObject(
                                    name = "Registration Request",
                                    value = "{\"username\": \"john_doe\", \"email\": \"john@example.com\", \"password\": \"securePassword123\"}"
                            )
                    )
            )
            @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    @Operation(
            summary = "User login",
            description = "Authenticate user with email and password. Returns JWT + metadata."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Login successful",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AuthResponse.class),
                            examples = @ExampleObject(
                                    name = "Login Success",
                                    value = "{" +
                                            "\"token\":\"eyJhbGciOi...\"," +
                                            "\"userId\":1," +
                                            "\"username\":\"john_doe\"," +
                                            "\"email\":\"john@example.com\"," +
                                            "\"roles\":[\"ROLE_USER\"]," +
                                            "\"issuedAt\":\"2025-09-25T07:10:00Z\"," +
                                            "\"expiresAt\":\"2025-09-26T07:10:00Z\"}" )
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Invalid credentials",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Login Error",
                                    value = "{\"error\":\"Invalid email or password\"}"
                            )
                    )
            )
    })
    public ResponseEntity<AuthResponse> login(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "User login credentials",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = AuthRequest.class),
                            examples = @ExampleObject(
                                    name = "Login Request",
                                    value = "{\"email\": \"john@example.com\", \"password\": \"securePassword123\"}"
                            )
                    )
            )
            @RequestBody AuthRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}
