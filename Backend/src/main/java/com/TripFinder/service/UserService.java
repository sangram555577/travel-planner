package com.TripFinder.service;

import com.TripFinder.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    List<User> getAllUsers();
    Optional<User> getUserById(int id);
    Optional<User> findByEmail(String email);
    User saveUser(User user);
    void deleteUserById(int id);
}
