package com.bookmanagement.service.impl;

import com.bookmanagement.constant.RoleConstants;
import com.bookmanagement.dto.*;
import com.bookmanagement.entity.Role;
import com.bookmanagement.entity.User;
//import com.bookmanagement.enums.Role;
import com.bookmanagement.exception.*;
import com.bookmanagement.mapper.UserMapper;
import com.bookmanagement.repository.RoleRepository;
import com.bookmanagement.repository.UserRepository;
import com.bookmanagement.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;


    @Override
    public UserResponseDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        return userMapper.toResponseDTO(user);
    }



@Override
@Transactional
public UserResponseDTO createUser(NewUserDTO userDTO) {
    log.info(">>>> Creating user with username: {}", userDTO.getUsername());

    validateAdminRoleAssignment(userDTO);
    validateUniqueConstraints(userDTO);

    User user = buildUserEntity(userDTO);
    assignRolesToUser(user, userDTO.getRole());

    User savedUser = userRepository.save(user);
    log.info(">>>> User saved successfully with {} role(s)", savedUser.getRoles().size());

    return userMapper.toResponseDTO(savedUser);
}

/**
 * Validates that user is not trying to self-assign ADMIN role
 */
private void validateAdminRoleAssignment(NewUserDTO userDTO) {
    if (userDTO.getRole() != null && !userDTO.getRole().isBlank() 
            && userDTO.getRole().toUpperCase().contains(RoleConstants.ADMIN)) {
        log.warn(">>>> Attempt to self-assign ADMIN role blocked for username: {}", userDTO.getUsername());
        throw new IllegalArgumentException("You cannot assign ADMIN role to yourself. Please contact an administrator.");
    }
}

/**
 * Validates username and email uniqueness
 */
private void validateUniqueConstraints(NewUserDTO userDTO) {
    if (userRepository.findByUsername(userDTO.getUsername()).isPresent()) {
        log.info(">>>> Username {} already exists", userDTO.getUsername());
        throw new DuplicateResourceException("Username already exists");
    }

    log.info(">>>> Creating user with email: {}", userDTO.getEmail());
    if (userRepository.findByEmail(userDTO.getEmail()).isPresent()) {
        log.info(">>>> Email {} already exists", userDTO.getEmail());
        throw new DuplicateResourceException("Email already exists");
    }
}

/**
 * Builds user entity from DTO and encodes password
 */
private User buildUserEntity(NewUserDTO userDTO) {
    User user = userMapper.toEntity(userDTO);
    user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
    return user;
}

/**
 * Assigns roles to user based on role string (comma-separated or single)
 */
private void assignRolesToUser(User user, String roleString) {
    log.info(">>>> Checking if user set a role: {}", roleString);

    if (roleString == null || roleString.isBlank()) {
        assignDefaultRole(user);
        return;
    }

    String[] roleNames = roleString.split(",");
    boolean anyRoleAssigned = processRoleAssignments(user, roleNames);

    if (!anyRoleAssigned) {
        assignDefaultRole(user);
        log.warn(">>>> No valid roles found. Assigned default USER role");
    }
}

/**
 * Processes multiple role assignments and returns whether any were successful
 */
private boolean processRoleAssignments(User user, String[] roleNames) {
    boolean anyRoleAssigned = false;

    for (String roleName : roleNames) {
        String trimmedRole = roleName.trim().toUpperCase();
        Role role = roleRepository.findByName(trimmedRole).orElse(null);

        if (role != null) {
            user.addRole(role);
            anyRoleAssigned = true;
            log.info(">>>> Assigned role: {}", trimmedRole);
        } else {
            log.warn(">>>> Role '{}' not found. Skipping.", trimmedRole);
        }
    }

    return anyRoleAssigned;
}

/**
 * Assigns default USER role to the user
 */
private void assignDefaultRole(User user) {
    Role defaultUserRole = roleRepository.findByName(RoleConstants.USER)
            .orElseThrow(() -> new RuntimeException("Default USER role not found. System misconfigured."));
    
    user.addRole(defaultUserRole);
    log.info(">>>> No role specified. Assigned default USER role");
}

    @Override
    @Transactional
    public UserResponseDTO updateUser(Long id, UpdateUserDTO userDTO) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        
        if (userDTO.getUsername() != null && !userDTO.getUsername().equals(user.getUsername())) {
            if (userRepository.findByUsername(userDTO.getUsername()).isPresent()) {
                throw new DuplicateResourceException("Username already exists");
            }
            user.setUsername(userDTO.getUsername());
        }
        
        if (userDTO.getEmail() != null && !userDTO.getEmail().equals(user.getEmail())) {
            if (userRepository.findByEmail(userDTO.getEmail()).isPresent()) {
                throw new DuplicateResourceException("Email already exists");
            }
            user.setEmail(userDTO.getEmail());
        }
        
        if (userDTO.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        }
        
        User updatedUser = userRepository.save(user);
        return userMapper.toResponseDTO(updatedUser);
    }

    @Override
     @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }
    
}
