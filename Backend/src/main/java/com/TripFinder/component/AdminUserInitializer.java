package com.TripFinder.component;

import com.TripFinder.entity.User;
import com.TripFinder.enums.Role;
import com.TripFinder.repository.UserRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Component to automatically set admin369@gmail.com as ADMIN on startup
 */
@Component
@Slf4j
public class AdminUserInitializer implements CommandLineRunner {

    @Autowired
    private UserRepo userRepo;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        String adminEmail = "admin369@gmail.com";
        
        Optional<User> userOpt = userRepo.findByEmail(adminEmail);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (user.getRole() != Role.ADMIN) {
                user.setRole(Role.ADMIN);
                userRepo.save(user);
                log.info("✅ Successfully promoted {} to ADMIN role", adminEmail);
            } else {
                log.info("✅ User {} is already ADMIN", adminEmail);
            }
        } else {
            // Create admin user if not exists
            User adminUser = new User();
            adminUser.setFullName("Admin User");
            adminUser.setEmail(adminEmail);
            adminUser.setPhone("9999999999");
            adminUser.setPassword(passwordEncoder.encode("12345678"));
            adminUser.setRole(Role.ADMIN);
            userRepo.save(adminUser);
            log.info("✅ Created new admin user: {}", adminEmail);
        }
        
        // Also check for testadmin@gmail.com, myadmin123@gmail.com, and workingadmin@gmail.com
        String[] otherAdminEmails = {"testadmin@gmail.com", "myadmin123@gmail.com", "workingadmin@gmail.com"};
        for (String otherAdminEmail : otherAdminEmails) {
            Optional<User> otherAdminUserOpt = userRepo.findByEmail(otherAdminEmail);
            if (otherAdminUserOpt.isPresent()) {
                User otherAdminUser = otherAdminUserOpt.get();
                if (otherAdminUser.getRole() != Role.ADMIN) {
                    otherAdminUser.setRole(Role.ADMIN);
                    userRepo.save(otherAdminUser);
                    log.info("✅ Successfully promoted {} to ADMIN role", otherAdminEmail);
                } else {
                    log.info("✅ User {} is already ADMIN", otherAdminEmail);
                }
            }
        }
        
        // Also fix regular users' passwords if they exist
        String[] regularUsers = {"john@example.com", "jane@example.com"};
        for (String userEmail : regularUsers) {
            Optional<User> regularUserOpt = userRepo.findByEmail(userEmail);
            if (regularUserOpt.isPresent()) {
                User regularUser = regularUserOpt.get();
                // Re-encode password to ensure it's correct
                regularUser.setPassword(passwordEncoder.encode("12345678"));
                userRepo.save(regularUser);
                log.info("✅ Updated password for regular user: {}", userEmail);
            }
        }
    }
}