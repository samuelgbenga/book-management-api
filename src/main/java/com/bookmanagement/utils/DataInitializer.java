package com.bookmanagement.utils;

import com.bookmanagement.constant.RoleConstants;
import com.bookmanagement.entity.Role;
import com.bookmanagement.entity.User;
import com.bookmanagement.repository.RoleRepository;
import com.bookmanagement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {
    
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Override
    public void run(String... args) {
        initializeRoles();
        initializeAdminUser();
    }
    
    private void initializeRoles() {
        createRoleIfNotExists(RoleConstants.USER, "Standard user role with basic permissions");
        createRoleIfNotExists(RoleConstants.ADMIN, "Administrator role with full permissions");
        
        log.info("Role initialization completed");
    }
    
    @Transactional
    protected void createRoleIfNotExists(String roleName, String description) {
        if (!roleRepository.existsByName(roleName)) {
            Role role = Role.builder()
                    .name(roleName)
                    .description(description)
                    .build();
            roleRepository.save(role);
            log.info("Created role: {}", roleName);
        }
    }
    
    @Transactional
    protected void initializeAdminUser() {
        // Check if admin user already exists
        if (userRepository.findByUsername("admin").isPresent()) {
            log.info("Admin user already exists. Skipping creation.");
            return;
        }
        
        // Fetch ADMIN role
        Role adminRole = roleRepository.findByName(RoleConstants.ADMIN)
                .orElseThrow(() -> new RuntimeException("ADMIN role not found"));
        
        // Create admin user
        User adminUser = User.builder()
                .username("admin")
                .email("admin@bookmanagement.com")
                .password(passwordEncoder.encode("admin123$"))  // Change this password!
                .build();
        
        // Directly add to the roles set instead of using the helper method
        adminUser.getRoles().add(adminRole);
        
        // Save admin user
        userRepository.save(adminUser);
        
        log.info("============================================");
        log.info("Default Admin User Created:");
        log.info("Username: admin");
        log.info("Email: admin@bookmanagement.com");
        log.info("Password: admin123$");
        log.info("⚠️  IMPORTANT: Change this password immediately!");
        log.info("============================================");
    }
}