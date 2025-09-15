package com.TripFinder.service;

import com.TripFinder.dto.AuthResponse;
import com.TripFinder.dto.LoginRequest;
import com.TripFinder.dto.SignUpRequest;

/**
 * Service interface for handling user authentication logic,
 * including registration and login.
 */
public interface AuthService {

    /**
     * Registers a new user in the system.
     *
     * @param signUpRequest DTO containing the new user's details.
     * @return An AuthResponse containing the JWT and user details.
     */
    AuthResponse signUp(SignUpRequest signUpRequest);

    /**
     * Authenticates a user and provides a JWT upon successful login.
     *
     * @param loginRequest DTO containing the user's login credentials.
     * @return An AuthResponse containing the JWT and user details.
     */
    AuthResponse login(LoginRequest loginRequest);
}