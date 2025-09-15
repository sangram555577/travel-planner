package com.TripFinder.service;

import com.TripFinder.dto.AdminUserDto;
import com.TripFinder.entity.Booking;
import com.TripFinder.entity.User;
import com.TripFinder.enums.Role;

import java.util.List;
import java.util.Map;

/**
 * Service interface for admin operations.
 * Handles user management, booking management, and system statistics.
 */
public interface AdminService {

    /**
     * Get all users in the system
     * @return list of users with admin-relevant information
     */
    List<AdminUserDto> getAllUsers();

    /**
     * Get all bookings in the system
     * @return list of bookings with user and itinerary information
     */
    List<Booking> getAllBookings();

    /**
     * Delete a booking by ID
     * @param bookingId the booking ID to delete
     * @throws RuntimeException if booking not found or cannot be deleted
     */
    void deleteBooking(Long bookingId);

    /**
     * Update user role
     * @param userId the user ID
     * @param newRole the new role to assign
     * @return updated user
     * @throws RuntimeException if user not found
     */
    User updateUserRole(int userId, Role newRole);

    /**
     * Get system statistics for admin dashboard
     * @return map with various statistics
     */
    Map<String, Object> getSystemStatistics();

    /**
     * Get user statistics
     * @param userId the user ID (optional, if null returns stats for all users)
     * @return user-specific statistics
     */
    Map<String, Object> getUserStatistics(Integer userId);

    /**
     * Validate admin operation permissions
     * @param currentUserId the current user performing the operation
     * @param targetUserId the target user being modified (optional)
     * @throws RuntimeException if operation not permitted
     */
    void validateAdminPermission(int currentUserId, Integer targetUserId);
}