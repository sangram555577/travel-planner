package com.TripFinder.controller;

import com.TripFinder.entity.User;
import com.TripFinder.enums.Role;
import com.TripFinder.repository.UserRepo;
import com.TripFinder.service.AdminService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminController.class)
@ContextConfiguration
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class AdminControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AdminService adminService;

    @MockBean
    private UserRepo userRepo; // Needed for security context

    @Autowired
    private ObjectMapper objectMapper;

    private User adminUser;
    private User regularUser;

    @BeforeEach
    void setUp() {
        adminUser = new User();
        adminUser.setId(1);
        adminUser.setEmail("admin@example.com");
        adminUser.setFullName("Admin User");
        adminUser.setRole(Role.ADMIN);

        regularUser = new User();
        regularUser.setId(2);
        regularUser.setEmail("user@example.com");
        regularUser.setFullName("Regular User");
        regularUser.setRole(Role.USER);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllUsers_ShouldReturnUsers_WhenAdmin() throws Exception {
        // Given
        when(adminService.getAllUsers()).thenReturn(Arrays.asList());

        // When & Then
        mockMvc.perform(get("/api/admin/users")
                .with(user(adminUser)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(adminService).getAllUsers();
    }

    @Test
    @WithMockUser(roles = "USER")
    void getAllUsers_ShouldReturnForbidden_WhenNotAdmin() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/admin/users")
                .with(user(regularUser)))
                .andExpect(status().isForbidden());

        verify(adminService, never()).getAllUsers();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllBookings_ShouldReturnBookings_WhenAdmin() throws Exception {
        // Given
        when(adminService.getAllBookings()).thenReturn(Arrays.asList());

        // When & Then
        mockMvc.perform(get("/api/admin/bookings")
                .with(user(adminUser)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(adminService).getAllBookings();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateUserRole_ShouldUpdateRole_WhenValidRequest() throws Exception {
        // Given
        User updatedUser = new User();
        updatedUser.setId(2);
        updatedUser.setRole(Role.ADMIN);
        
        when(adminService.updateUserRole(2, Role.ADMIN)).thenReturn(updatedUser);
        doNothing().when(adminService).validateAdminPermission(anyInt(), anyInt());

        Map<String, String> request = new HashMap<>();
        request.put("role", "ADMIN");

        // When & Then
        mockMvc.perform(put("/api/admin/users/2/role")
                .with(user(adminUser))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User role updated successfully"))
                .andExpect(jsonPath("$.userId").value(2))
                .andExpect(jsonPath("$.newRole").value("ADMIN"));

        verify(adminService).updateUserRole(2, Role.ADMIN);
        verify(adminService).validateAdminPermission(1, 2);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateUserRole_ShouldReturnBadRequest_WhenInvalidRole() throws Exception {
        // Given
        Map<String, String> request = new HashMap<>();
        request.put("role", "INVALID_ROLE");

        // When & Then
        mockMvc.perform(put("/api/admin/users/2/role")
                .with(user(adminUser))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Invalid role. Must be USER or ADMIN"));

        verify(adminService, never()).updateUserRole(anyInt(), any());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteBooking_ShouldDeleteBooking_WhenValidId() throws Exception {
        // Given
        doNothing().when(adminService).deleteBooking(1L);

        // When & Then
        mockMvc.perform(delete("/api/admin/bookings/1")
                .with(user(adminUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Booking deleted successfully"))
                .andExpect(jsonPath("$.bookingId").value(1));

        verify(adminService).deleteBooking(1L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteBooking_ShouldReturnBadRequest_WhenBookingCannotBeDeleted() throws Exception {
        // Given
        doThrow(new RuntimeException("Cannot delete confirmed booking"))
                .when(adminService).deleteBooking(1L);

        // When & Then
        mockMvc.perform(delete("/api/admin/bookings/1")
                .with(user(adminUser)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Cannot delete confirmed booking"));

        verify(adminService).deleteBooking(1L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getSystemStatistics_ShouldReturnStats_WhenAdmin() throws Exception {
        // Given
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUsers", 10L);
        stats.put("adminUsers", 2L);
        stats.put("totalBookings", 25L);
        
        when(adminService.getSystemStatistics()).thenReturn(stats);

        // When & Then
        mockMvc.perform(get("/api/admin/statistics")
                .with(user(adminUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalUsers").value(10))
                .andExpect(jsonPath("$.adminUsers").value(2))
                .andExpect(jsonPath("$.totalBookings").value(25));

        verify(adminService).getSystemStatistics();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getUserStatistics_ShouldReturnUserStats_WhenValidUserId() throws Exception {
        // Given
        Map<String, Object> userStats = new HashMap<>();
        userStats.put("userId", 2);
        userStats.put("totalItineraries", 5L);
        userStats.put("totalBookings", 3L);
        
        when(adminService.getUserStatistics(2)).thenReturn(userStats);

        // When & Then
        mockMvc.perform(get("/api/admin/users/2/statistics")
                .with(user(adminUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(2))
                .andExpect(jsonPath("$.totalItineraries").value(5))
                .andExpect(jsonPath("$.totalBookings").value(3));

        verify(adminService).getUserStatistics(2);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAdminProfile_ShouldReturnProfile_WhenAdmin() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/admin/profile")
                .with(user(adminUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("admin@example.com"))
                .andExpect(jsonPath("$.role").value("ADMIN"));
    }
}