package com.TripFinder.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Data Transfer Object for handling user registration requests.
 * Includes validation rules for the incoming data.
 *
 * @param fullName The user's full name.
 * @param email The user's email address, which will also serve as their username.
 * @param phone The user's phone number.
 * @param password The user's desired password.
 */
public record SignUpRequest(
    @NotBlank(message = "Full name cannot be blank")
    @Size(max = 100, message = "Full name must not exceed 100 characters")
    String fullName,

    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Please provide a valid email address")
    @Size(max = 100, message = "Email must not exceed 100 characters")
    String email,

    @NotBlank(message = "Phone number cannot be blank")
    @Size(max = 20, message = "Phone number must not exceed 20 characters")
    String phone,

    @NotBlank(message = "Password cannot be blank")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    String password
) {}