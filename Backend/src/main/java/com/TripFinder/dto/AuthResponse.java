package com.TripFinder.dto;

import com.TripFinder.entity.User;

/**
 * Data Transfer Object for sending authentication responses.
 * This record bundles the JWT with essential user details.
 *
 * @param token The JWT token for authenticating subsequent requests.
 * @param user  The details of the authenticated user.
 */
public record AuthResponse(
    String token,
    UserDto user
) {
    /**
     * A nested DTO to represent the user information that is safe to send to the client.
     * Excludes sensitive data like the password hash.
     *
     * @param id The user's unique identifier.
     * @param fullName The user's full name.
     * @param email The user's email address.
     * @param phone The user's phone number.
     */
    public record UserDto(
        int id,
        String fullName,
        String email,
        String phone
    ) {
        /**
         * Factory method to create a UserDto from a User entity.
         *
         * @param user The User entity.
         * @return A new UserDto instance.
         */
        public static UserDto fromUser(User user) {
            return new UserDto(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getPhone()
            );
        }
    }

    /**
     * Factory method to create an AuthResponse from a User entity and a JWT token.
     *
     * @param user The User entity.
     * @param token The JWT token.
     * @return A new AuthResponse instance.
     */
    public static AuthResponse fromUserAndToken(User user, String token) {
        return new AuthResponse(token, UserDto.fromUser(user));
    }
}