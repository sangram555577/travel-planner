package com.TripFinder.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler to catch and format exceptions thrown by controllers.
 * Ensures consistent, structured JSON error responses across the API.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles validation errors (e.g., @NotBlank, @Email).
     *
     * @param ex The exception thrown when validation fails.
     * @return A ResponseEntity with a map of validation errors.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage()));
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles general runtime exceptions, such as "User not found" or "Invalid credentials".
     *
     * @param ex The runtime exception.
     * @return A ResponseEntity with a formatted error response.
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException ex) {
        HttpStatus status = HttpStatus.CONFLICT; // Default to Conflict for business logic errors
        if (ex.getMessage().toLowerCase().contains("credentials")) {
            status = HttpStatus.UNAUTHORIZED;
        }
        ErrorResponse errorResponse = new ErrorResponse(status.value(), ex.getMessage(), Instant.now().toEpochMilli());
        return new ResponseEntity<>(errorResponse, status);
    }

    /**
     * A catch-all handler for any other unhandled exceptions.
     *
     * @param ex The generic exception.
     * @return A ResponseEntity with a generic internal server error message.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "An unexpected error occurred", Instant.now().toEpochMilli());
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

// Helper Record for structured error responses
record ErrorResponse(int status, String message, long timestamp) {}