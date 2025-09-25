package com.djcode.fitness.fitnessapp.service;

import com.djcode.fitness.fitnessapp.dto.AuthRequest;
import com.djcode.fitness.fitnessapp.dto.AuthResponse;
import com.djcode.fitness.fitnessapp.dto.RegisterRequest;
import com.djcode.fitness.fitnessapp.entity.User;
import com.djcode.fitness.fitnessapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthResponse register(RegisterRequest request) {
        // Duplicate email safeguard
        userRepository.findByEmail(request.email()).ifPresent(u -> {
            throw new RuntimeException("Email already registered");
        });

        var user = User.builder()
                .username(request.username())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .roles(List.of("ROLE_USER"))
                .build();

        userRepository.save(user);

        var now = Instant.now();
        var claims = new HashMap<String,Object>();
        claims.put("uid", user.getId());
        claims.put("roles", user.getRoles());
        var jwtToken = jwtService.generateToken(claims, user);
        var expiresAt = now.plusMillis(jwtService.getJwtExpiration());

        return new AuthResponse(
                jwtToken,
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRoles(),
                now,
                expiresAt
        );
    }

    public AuthResponse login(AuthRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password()
                )
        );

        var user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new RuntimeException("User not found"));

        var now = Instant.now();
        var claims = new HashMap<String,Object>();
        claims.put("uid", user.getId());
        claims.put("roles", user.getRoles());
        var jwtToken = jwtService.generateToken(claims, user);
        var expiresAt = now.plusMillis(jwtService.getJwtExpiration());

        return new AuthResponse(
                jwtToken,
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRoles(),
                now,
                expiresAt
        );
    }
}
