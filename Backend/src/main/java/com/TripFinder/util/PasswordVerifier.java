package com.TripFinder.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordVerifier {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String password = "12345678";
        
        // Hash from create_admin.sql
        String hash1 = "$2a$10$eImiTXuWVxfM37uY4JANjO8QgCfPo8HKBsj9EcC7yR1O5VzGkVDAK";
        
        // Hash from data.sql (old)
        String hash2 = "$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy";
        
        System.out.println("Testing password: " + password);
        System.out.println("Hash1 (create_admin.sql) matches: " + encoder.matches(password, hash1));
        System.out.println("Hash2 (data.sql old) matches: " + encoder.matches(password, hash2));
        
        // Generate a new hash
        String newHash = encoder.encode(password);
        System.out.println("New hash: " + newHash);
        System.out.println("New hash matches: " + encoder.matches(password, newHash));
    }
}