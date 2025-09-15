package com.TripFinder.dto;

import com.TripFinder.enums.Role;

import java.time.LocalDateTime;

/**
 * DTO for admin user information.
 * Contains user details relevant for admin operations.
 */
public record AdminUserDto(
    int id,
    String fullName,
    String email,
    String phone,
    Role role,
    long totalItineraries,
    long totalBookings,
    LocalDateTime lastActivity,
    boolean isActive
) {
    
    /**
     * Create AdminUserDto from User entity with statistics
     */
    public static AdminUserDto fromUserWithStats(
            com.TripFinder.entity.User user,
            long totalItineraries,
            long totalBookings,
            LocalDateTime lastActivity
    ) {
        return new AdminUserDto(
            user.getId(),
            user.getFullName(),
            user.getEmail(),
            user.getPhone(),
            user.getRole(),
            totalItineraries,
            totalBookings,
            lastActivity,
            user.isEnabled()
        );
    }
}