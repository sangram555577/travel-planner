package com.TripFinder.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

/**
 * DTO for creating a new expense.
 */
public record ExpenseDto(
    @NotNull(message = "Amount cannot be null")
    @Positive(message = "Amount must be positive")
    Double amount,

    @NotBlank(message = "Category cannot be blank")
    @Size(max = 100)
    String category,

    @Size(max = 255)
    String description,

    @NotNull(message = "User ID cannot be null")
    Integer userId
) {}