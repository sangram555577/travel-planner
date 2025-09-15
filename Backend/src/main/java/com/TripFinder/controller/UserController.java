package com.TripFinder.controller;

import com.TripFinder.dto.AuthResponse;
import com.TripFinder.entity.User;
import com.TripFinder.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * REST controller for managing user-related operations.
 * All endpoints in this controller are protected and require authentication.
 */
@RestController
@RequestMapping("/api/v1/users")
@CrossOrigin(origins = "http://localhost:5173")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * Retrieves a list of all users.
     *
     * @return A list of User DTOs.
     */
    @GetMapping
    public ResponseEntity<List<AuthResponse.UserDto>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        List<AuthResponse.UserDto> userDtos = users.stream()
                .map(AuthResponse.UserDto::fromUser)
                .collect(Collectors.toList());
        return ResponseEntity.ok(userDtos);
    }

    /**
     * Retrieves a single user by their ID.
     *
     * @param id The ID of the user to retrieve.
     * @return A ResponseEntity containing the user DTO or a 404 Not Found status.
     */
    @GetMapping("/{id}")
    public ResponseEntity<AuthResponse.UserDto> getUserById(@PathVariable int id) {
        return userService.getUserById(id)
                .map(user -> ResponseEntity.ok(AuthResponse.UserDto.fromUser(user)))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Deletes a user by their ID.
     *
     * @param id The ID of the user to delete.
     * @return A ResponseEntity with No Content status.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable int id) {
        // First check if user exists to provide a 404 if not found
        if (userService.getUserById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        userService.deleteUserById(id);
        return ResponseEntity.noContent().build();
    }
}