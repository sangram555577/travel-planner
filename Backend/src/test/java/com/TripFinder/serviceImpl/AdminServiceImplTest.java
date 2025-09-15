package com.TripFinder.serviceImpl;

import com.TripFinder.dto.AdminUserDto;
import com.TripFinder.entity.Booking;
import com.TripFinder.entity.User;
import com.TripFinder.enums.Role;
import com.TripFinder.repository.BookingRepo;
import com.TripFinder.repository.ItineraryRepo;
import com.TripFinder.repository.UserRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminServiceImplTest {

    @Mock
    private UserRepo userRepo;

    @Mock
    private BookingRepo bookingRepo;

    @Mock
    private ItineraryRepo itineraryRepo;

    @InjectMocks
    private AdminServiceImpl adminService;

    private User testUser;
    private User testAdmin;
    private Booking testBooking;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1);
        testUser.setFullName("Test User");
        testUser.setEmail("test@example.com");
        testUser.setPhone("123-456-7890");
        testUser.setRole(Role.USER);

        testAdmin = new User();
        testAdmin.setId(2);
        testAdmin.setFullName("Test Admin");
        testAdmin.setEmail("admin@example.com");
        testAdmin.setPhone("098-765-4321");
        testAdmin.setRole(Role.ADMIN);

        testBooking = new Booking();
        testBooking.setId(1L);
        testBooking.setBookingReference("TEST-BOOKING-123");
        testBooking.setStatus(Booking.BookingStatus.PENDING);
    }

    @Test
    void getAllUsers_ShouldReturnUserList() {
        // Given
        List<User> users = Arrays.asList(testUser, testAdmin);
        when(userRepo.findAll()).thenReturn(users);
        when(itineraryRepo.countByUserId(anyInt())).thenReturn(2L);
        when(bookingRepo.countByUserId(anyInt())).thenReturn(1L);

        // When
        List<AdminUserDto> result = adminService.getAllUsers();

        // Then
        assertEquals(2, result.size());
        assertEquals("Test User", result.get(0).fullName());
        assertEquals("Test Admin", result.get(1).fullName());
        verify(userRepo).findAll();
    }

    @Test
    void updateUserRole_ShouldUpdateRole_WhenValidUser() {
        // Given
        when(userRepo.findById(1)).thenReturn(Optional.of(testUser));
        when(userRepo.countByRole(Role.ADMIN)).thenReturn(2L);
        when(userRepo.save(any(User.class))).thenReturn(testUser);

        // When
        User result = adminService.updateUserRole(1, Role.ADMIN);

        // Then
        assertEquals(Role.ADMIN, result.getRole());
        verify(userRepo).save(testUser);
    }

    @Test
    void updateUserRole_ShouldThrowException_WhenLastAdmin() {
        // Given
        testUser.setRole(Role.ADMIN);
        when(userRepo.findById(1)).thenReturn(Optional.of(testUser));
        when(userRepo.countByRole(Role.ADMIN)).thenReturn(1L);

        // When & Then
        assertThrows(RuntimeException.class, () -> {
            adminService.updateUserRole(1, Role.USER);
        });
        
        verify(userRepo, never()).save(any());
    }

    @Test
    void deleteBooking_ShouldDeleteBooking_WhenNotConfirmed() {
        // Given
        when(bookingRepo.findById(1L)).thenReturn(Optional.of(testBooking));
        doNothing().when(bookingRepo).delete(testBooking);

        // When
        adminService.deleteBooking(1L);

        // Then
        verify(bookingRepo).delete(testBooking);
    }

    @Test
    void deleteBooking_ShouldThrowException_WhenConfirmed() {
        // Given
        testBooking.setStatus(Booking.BookingStatus.CONFIRMED);
        when(bookingRepo.findById(1L)).thenReturn(Optional.of(testBooking));

        // When & Then
        assertThrows(RuntimeException.class, () -> {
            adminService.deleteBooking(1L);
        });
        
        verify(bookingRepo, never()).delete(any());
    }

    @Test
    void getSystemStatistics_ShouldReturnStats() {
        // Given
        when(userRepo.count()).thenReturn(10L);
        when(userRepo.countByRole(Role.ADMIN)).thenReturn(2L);
        when(userRepo.countByRole(Role.USER)).thenReturn(8L);
        when(bookingRepo.count()).thenReturn(25L);
        when(itineraryRepo.count()).thenReturn(15L);
        when(bookingRepo.getBookingStatsByStatus()).thenReturn(Arrays.asList(
            new Object[]{"PENDING", 5L},
            new Object[]{"CONFIRMED", 20L}
        ));

        // When
        Map<String, Object> stats = adminService.getSystemStatistics();

        // Then
        assertEquals(10L, stats.get("totalUsers"));
        assertEquals(2L, stats.get("adminUsers"));
        assertEquals(8L, stats.get("regularUsers"));
        assertEquals(25L, stats.get("totalBookings"));
        assertEquals(15L, stats.get("totalItineraries"));
        
        @SuppressWarnings("unchecked")
        Map<String, Long> bookingsByStatus = (Map<String, Long>) stats.get("bookingsByStatus");
        assertEquals(5L, bookingsByStatus.get("PENDING"));
        assertEquals(20L, bookingsByStatus.get("CONFIRMED"));
    }

    @Test
    void validateAdminPermission_ShouldPass_WhenUserIsAdmin() {
        // Given
        when(userRepo.findById(2)).thenReturn(Optional.of(testAdmin));

        // When & Then
        assertDoesNotThrow(() -> {
            adminService.validateAdminPermission(2, null);
        });
    }

    @Test
    void validateAdminPermission_ShouldThrowException_WhenUserIsNotAdmin() {
        // Given
        when(userRepo.findById(1)).thenReturn(Optional.of(testUser));

        // When & Then
        assertThrows(RuntimeException.class, () -> {
            adminService.validateAdminPermission(1, null);
        });
    }

    @Test
    void validateAdminPermission_ShouldThrowException_WhenUserNotFound() {
        // Given
        when(userRepo.findById(999)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class, () -> {
            adminService.validateAdminPermission(999, null);
        });
    }
}