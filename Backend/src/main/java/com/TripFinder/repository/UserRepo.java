package com.TripFinder.repository;

import com.TripFinder.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for {@link User} entities.
 * Provides standard CRUD operations and custom queries for user data.
 */
@Repository
public interface UserRepo extends JpaRepository<User, Integer> {

    /**
     * Finds a user by their email address.
     * Since email is a unique constraint, this method will return at most one user.
     *
     * @param email The email address to search for.
     * @return An {@link Optional} containing the found user, or an empty Optional if no user with the given email exists.
     */
    Optional<User> findByEmail(String email);
}