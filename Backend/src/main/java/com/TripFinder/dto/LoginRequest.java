package com.TripFinder.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * Data Transfer Object for handling user login requests.
 * Includes validation rules for the incoming credentials.
 *
 * @param email The user's email address.
 * @param password The user's password.
 */
public record LoginRequest(
    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Please provide a valid email address")
    String email,

    @NotBlank(message = "Password cannot be blank")
    String password
) {}