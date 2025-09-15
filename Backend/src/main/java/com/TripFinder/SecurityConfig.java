package com.TripFinder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class SecurityConfig {

    @Autowired
    private CorsConfigurationSource corsConfigurationSource;

    /**
     * Dev-friendly security config:
     * - Enables CORS for local frontend (5173, 5174)
     * - Disables CSRF (dev convenience)
     * - Permits all requests (so frontend can talk to backend while we iterate)
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource))
            .csrf(csrf -> csrf.disable())
            .headers(headers -> headers.frameOptions().disable()) // Allow H2 console frames
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/h2-console/**").permitAll() // Explicitly allow H2 console
                .requestMatchers("/api/v1/auth/**").permitAll() // Allow auth endpoints
                .anyRequest().permitAll()
            );
        return http.build();
    }

    // CORS configuration is now handled by CorsConfig class

    // Password encoder bean used by services when creating users
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
