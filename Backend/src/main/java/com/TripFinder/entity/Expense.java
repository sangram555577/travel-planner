package com.TripFinder.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;

/**
 * Represents a single expense entry associated with a user.
 * This entity maps to the 'expenses' table in the database.
 */
@Entity
@Table(name = "expenses")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Expense {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotNull(message = "Amount cannot be null")
    @Positive(message = "Amount must be positive")
    @Column(nullable = false)
    private double amount;

    @NotBlank(message = "Category cannot be blank")
    @Size(max = 100, message = "Category must not exceed 100 characters")
    @Column(nullable = false, length = 100)
    private String category;

    @Size(max = 255, message = "Description must not exceed 255 characters")
    private String description;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDate date;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @NotNull(message = "Expense must be associated with a user")
    private User user;
}