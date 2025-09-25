package com.djcode.fitness.fitnessapp.config;

import com.djcode.fitness.fitnessapp.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private ResponseEntity<ErrorResponse> build(HttpStatus status, String err, String msg, HttpServletRequest req){
        return ResponseEntity.status(status).body(new ErrorResponse(
                err,
                msg,
                status.value(),
                Instant.now(),
                req.getRequestURI()
        ));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> badCredentials(BadCredentialsException ex, HttpServletRequest req){
        return build(HttpStatus.UNAUTHORIZED, "BAD_CREDENTIALS", "Invalid email or password", req);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> validation(MethodArgumentNotValidException ex, HttpServletRequest req){
        String msg = ex.getBindingResult().getAllErrors().stream().findFirst().map(e -> e.getDefaultMessage()).orElse("Validation error");
        return build(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", msg, req);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> runtime(RuntimeException ex, HttpServletRequest req){
        // Specific duplicate email hint
        if(ex.getMessage()!=null && ex.getMessage().toLowerCase().contains("email already")){
            return build(HttpStatus.BAD_REQUEST, "EMAIL_EXISTS", ex.getMessage(), req);
        }
        return build(HttpStatus.BAD_REQUEST, "RUNTIME_ERROR", ex.getMessage()==null?"Unexpected error":ex.getMessage(), req);
    }
}

