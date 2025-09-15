package com.TripFinder.controller;

import com.TripFinder.dto.AdminUserDto;
import com.TripFinder.entity.Booking;
import com.TripFinder.entity.User;
import com.TripFinder.enums.Role;
import com.TripFinder.service.AdminService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST Controller for admin operations.
 * Provides endpoints for user management, booking management, and system statistics.
 * All endpoints require admin role authorization.
 */
@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
@PreAuthorize("hasRole('ADMIN')")
@Slf4j
public class AdminController {

    @Autowired
    private AdminService adminService;

    /**
     * Get all users in the system with statistics
     * GET /api/admin/users
     */
    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers() {
        try {
            log.info("Admin request: Get all users");
            List<AdminUserDto> users = adminService.getAllUsers();
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            log.error("Error fetching all users: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch users: " + e.getMessage()));
        }
    }

    /**
     * Get all bookings in the system
     * GET /api/admin/bookings
     */
    @GetMapping("/bookings")
    public ResponseEntity<?> getAllBookings() {
        try {
            log.info("Admin request: Get all bookings");
            List<Booking> bookings = adminService.getAllBookings();
            return ResponseEntity.ok(bookings);
        } catch (Exception e) {
            log.error("Error fetching all bookings: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch bookings: " + e.getMessage()));
        }
    }

    /**
     * Delete a booking by ID
     * DELETE /api/admin/bookings/{id}
     */
    @DeleteMapping("/bookings/{id}")
    public ResponseEntity<?> deleteBooking(@PathVariable Long id) {
        try {
            log.info("Admin request: Delete booking with ID {}", id);
            adminService.deleteBooking(id);
            return ResponseEntity.ok(Map.of("message", "Booking deleted successfully", "bookingId", id));
        } catch (RuntimeException e) {
            log.error("Error deleting booking {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Unexpected error deleting booking {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to delete booking: " + e.getMessage()));
        }
    }

    /**
     * Update user role
     * PUT /api/admin/users/{id}/role
     * Body: { "role": "ADMIN" | "USER" }
     */
    @PutMapping("/users/{id}/role")
    public ResponseEntity<?> updateUserRole(@PathVariable int id, @RequestBody Map<String, String> request) {
        try {
            String roleStr = request.get("role");
            if (roleStr == null || roleStr.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Role is required"));
            }

            Role newRole;
            try {
                newRole = Role.valueOf(roleStr.toUpperCase());
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Invalid role. Must be USER or ADMIN"));
            }

            log.info("Admin request: Update user {} role to {}", id, newRole);

            // Validate admin permission
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            User currentUser = (User) auth.getPrincipal();
            adminService.validateAdminPermission(currentUser.getId(), id);

            User updatedUser = adminService.updateUserRole(id, newRole);
            
            return ResponseEntity.ok(Map.of(
                "message", "User role updated successfully",
                "userId", updatedUser.getId(),
                "newRole", updatedUser.getRole(),
                "userEmail", updatedUser.getEmail()
            ));
        } catch (RuntimeException e) {
            log.error("Error updating user role for user {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Unexpected error updating user role for user {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to update user role: " + e.getMessage()));
        }
    }

    /**
     * Get system statistics for admin dashboard
     * GET /api/admin/statistics
     */
    @GetMapping("/statistics")
    public ResponseEntity<?> getSystemStatistics() {
        try {
            log.info("Admin request: Get system statistics");
            Map<String, Object> stats = adminService.getSystemStatistics();
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            log.error("Error fetching system statistics: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch statistics: " + e.getMessage()));
        }
    }

    /**
     * Get user-specific statistics
     * GET /api/admin/users/{id}/statistics
     */
    @GetMapping("/users/{id}/statistics")
    public ResponseEntity<?> getUserStatistics(@PathVariable Integer id) {
        try {
            log.info("Admin request: Get statistics for user {}", id);
            Map<String, Object> stats = adminService.getUserStatistics(id);
            return ResponseEntity.ok(stats);
        } catch (RuntimeException e) {
            log.error("Error fetching user statistics for user {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Unexpected error fetching user statistics for user {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch user statistics: " + e.getMessage()));
        }
    }

    /**
     * Get current admin user info
     * GET /api/admin/profile
     */
    @GetMapping("/profile")
    public ResponseEntity<?> getAdminProfile() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            User currentUser = (User) auth.getPrincipal();
            
            Map<String, Object> profile = Map.of(
                "id", currentUser.getId(),
                "fullName", currentUser.getFullName(),
                "email", currentUser.getEmail(),
                "role", currentUser.getRole(),
                "authorities", currentUser.getAuthorities()
            );
            
            return ResponseEntity.ok(profile);
        } catch (Exception e) {
            log.error("Error fetching admin profile: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch profile: " + e.getMessage()));
        }
    }
}