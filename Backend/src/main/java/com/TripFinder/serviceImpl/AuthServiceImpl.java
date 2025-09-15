package com.TripFinder.serviceImpl;

import com.TripFinder.dto.AuthResponse;
import com.TripFinder.dto.LoginRequest;
import com.TripFinder.dto.SignUpRequest;
import com.TripFinder.entity.User;
import com.TripFinder.repository.UserRepo;
import com.TripFinder.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;

/**
 * Implementation of the AuthService for handling user authentication.
 */
@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtEncoder jwtEncoder;

    @Override
    public AuthResponse signUp(SignUpRequest signUpRequest) {
        // Check if user with the same email already exists
        Optional<User> existingUser = userRepo.findByEmail(signUpRequest.email());
        if (existingUser.isPresent()) {
            throw new RuntimeException("User with this email already exists");
        }

        // Create and save the new user
        User newUser = new User();
        newUser.setFullName(signUpRequest.fullName());
        newUser.setEmail(signUpRequest.email());
        newUser.setPhone(signUpRequest.phone());
        newUser.setPassword(passwordEncoder.encode(signUpRequest.password()));

        User savedUser = userRepo.save(newUser);

        // Generate token and create response
        String token = generateJwtToken(savedUser);
        return AuthResponse.fromUserAndToken(savedUser, token);
    }

    @Override
    public AuthResponse login(LoginRequest loginRequest) {
        // Find user by email
        User user = userRepo.findByEmail(loginRequest.email())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        // Check if password matches
        if (!passwordEncoder.matches(loginRequest.password(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        // Generate token and create response
        String token = generateJwtToken(user);
        return AuthResponse.fromUserAndToken(user, token);
    }

    /**
     * Generates a JWT for the given user.
     *
     * @param user The user for whom to generate the token.
     * @return The JWT as a String.
     */
    private String generateJwtToken(User user) {
        Instant now = Instant.now();
        long expiry = 36000L; // 10 hours in seconds

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("TripFinderApp")
                .issuedAt(now)
                .expiresAt(now.plusSeconds(expiry))
                .subject(user.getEmail())
                .claim("userId", user.getId())
                .build();

        return this.jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }
}