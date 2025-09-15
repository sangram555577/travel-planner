package com.TripFinder.serviceImpl;

import com.TripFinder.dto.AdminUserDto;
import com.TripFinder.entity.Booking;
import com.TripFinder.entity.User;
import com.TripFinder.enums.Role;
import com.TripFinder.repository.BookingRepo;
import com.TripFinder.repository.ItineraryRepo;
import com.TripFinder.repository.UserRepo;
import com.TripFinder.service.AdminService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Implementation of AdminService for admin operations.
 * Handles user management, booking management, and system statistics.
 */
@Service
@Slf4j
@Transactional
public class AdminServiceImpl implements AdminService {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private BookingRepo bookingRepo;

    @Autowired
    private ItineraryRepo itineraryRepo;

    @Override
    @Transactional(readOnly = true)
    public List<AdminUserDto> getAllUsers() {
        log.info("Fetching all users for admin view");
        
        List<User> users = userRepo.findAll();
        return users.stream()
                .map(user -> {
                    long itineraryCount = itineraryRepo.countByUserId(user.getId());
                    long bookingCount = bookingRepo.countByUserId(user.getId());
                    
                    // For now, use creation date as last activity
                    // In a real app, you'd track actual last activity
                    LocalDateTime lastActivity = LocalDateTime.now().minusDays(7); // Mock data
                    
                    return AdminUserDto.fromUserWithStats(
                        user, itineraryCount, bookingCount, lastActivity
                    );
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Booking> getAllBookings() {
        log.info("Fetching all bookings for admin view");
        return bookingRepo.findAllWithUserAndItinerary();
    }

    @Override
    public void deleteBooking(Long bookingId) {
        log.info("Deleting booking with ID: {}", bookingId);
        
        Booking booking = bookingRepo.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found with ID: " + bookingId));

        // Check if booking can be deleted (e.g., not confirmed or completed)
        if (booking.getStatus() == Booking.BookingStatus.CONFIRMED || 
            booking.getStatus() == Booking.BookingStatus.COMPLETED) {
            throw new RuntimeException("Cannot delete confirmed or completed booking. Please cancel first.");
        }

        bookingRepo.delete(booking);
        log.info("Successfully deleted booking with ID: {}", bookingId);
    }

    @Override
    public User updateUserRole(int userId, Role newRole) {
        log.info("Updating role for user ID {} to {}", userId, newRole);
        
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        // Prevent changing role of the last admin
        if (user.getRole() == Role.ADMIN && newRole != Role.ADMIN) {
            long adminCount = userRepo.countByRole(Role.ADMIN);
            if (adminCount <= 1) {
                throw new RuntimeException("Cannot change role of the last admin user");
            }
        }

        user.setRole(newRole);
        User updatedUser = userRepo.save(user);
        
        log.info("Successfully updated role for user ID {} to {}", userId, newRole);
        return updatedUser;
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getSystemStatistics() {
        log.info("Fetching system statistics");
        
        Map<String, Object> stats = new HashMap<>();
        
        // User statistics
        long totalUsers = userRepo.count();
        long adminUsers = userRepo.countByRole(Role.ADMIN);
        long regularUsers = userRepo.countByRole(Role.USER);
        
        // Booking statistics
        long totalBookings = bookingRepo.count();
        List<Object[]> bookingStatsByStatus = bookingRepo.getBookingStatsByStatus();
        Map<String, Long> bookingsByStatus = new HashMap<>();
        for (Object[] stat : bookingStatsByStatus) {
            bookingsByStatus.put(stat[0].toString(), (Long) stat[1]);
        }
        
        // Itinerary statistics
        long totalItineraries = itineraryRepo.count();
        
        stats.put("totalUsers", totalUsers);
        stats.put("adminUsers", adminUsers);
        stats.put("regularUsers", regularUsers);
        stats.put("totalBookings", totalBookings);
        stats.put("bookingsByStatus", bookingsByStatus);
        stats.put("totalItineraries", totalItineraries);
        stats.put("generatedAt", LocalDateTime.now());
        
        return stats;
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getUserStatistics(Integer userId) {
        Map<String, Object> stats = new HashMap<>();
        
        if (userId != null) {
            log.info("Fetching statistics for user ID: {}", userId);
            
            User user = userRepo.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
            
            long userItineraries = itineraryRepo.countByUserId(userId);
            long userBookings = bookingRepo.countByUserId(userId);
            
            stats.put("userId", userId);
            stats.put("userEmail", user.getEmail());
            stats.put("totalItineraries", userItineraries);
            stats.put("totalBookings", userBookings);
            stats.put("userRole", user.getRole());
        } else {
            // Return aggregated stats for all users
            stats.put("totalUsers", userRepo.count());
            stats.put("totalItineraries", itineraryRepo.count());
            stats.put("totalBookings", bookingRepo.count());
        }
        
        stats.put("generatedAt", LocalDateTime.now());
        return stats;
    }

    @Override
    public void validateAdminPermission(int currentUserId, Integer targetUserId) {
        User currentUser = userRepo.findById(currentUserId)
                .orElseThrow(() -> new RuntimeException("Current user not found"));
        
        if (currentUser.getRole() != Role.ADMIN) {
            throw new RuntimeException("Access denied. Admin role required.");
        }
        
        // Additional validation logic can be added here
        // For example, preventing admins from modifying other admins
        if (targetUserId != null && !targetUserId.equals(currentUserId)) {
            User targetUser = userRepo.findById(targetUserId)
                    .orElseThrow(() -> new RuntimeException("Target user not found"));
            
            // Example rule: Regular admins cannot modify other admins
            if (targetUser.getRole() == Role.ADMIN && currentUser.getRole() == Role.ADMIN) {
                // In a more complex system, you might have super-admin roles
                log.warn("Admin {} attempted to modify another admin {}", currentUserId, targetUserId);
            }
        }
    }
}