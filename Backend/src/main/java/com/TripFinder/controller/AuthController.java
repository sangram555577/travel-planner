package com.TripFinder.controller;

import com.TripFinder.dto.AuthResponse;
import com.TripFinder.dto.LoginRequest;
import com.TripFinder.dto.SignUpRequest;
import com.TripFinder.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for handling user authentication endpoints like login and sign-up.
 */
@RestController
@RequestMapping("/api/v1/auth")
@CrossOrigin(origins = "http://localhost:5173")
public class AuthController {

    @Autowired
    private AuthService authService;

    /**
     * Handles user registration.
     *
     * @param signUpRequest The sign-up request DTO containing user details.
     * @return A ResponseEntity with the authentication response (JWT and user data).
     */
    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> signUp(@Valid @RequestBody SignUpRequest signUpRequest) {
        AuthResponse response = authService.signUp(signUpRequest);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Handles user login.
     *
     * @param loginRequest The login request DTO containing user credentials.
     * @return A ResponseEntity with the authentication response (JWT and user data).
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        AuthResponse response = authService.login(loginRequest);
        return ResponseEntity.ok(response);
    }
}
