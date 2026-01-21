package com.bookmanagement.mapper;

import com.bookmanagement.dto.*;
import com.bookmanagement.entity.*;
import org.mapstruct.*;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {
    
    // Map User entity to UserResponseDTO
    @Mapping(target = "role", expression = "java(mapRolesToString(user.getRoles()))")
    UserResponseDTO toResponseDTO(User user);
    
    // Map User entity to UserSummaryDTO
    // @Mapping(target = "role", expression = "java(getPrimaryRole(user.getRoles()))")
    // UserSummaryDTO toSummaryDTO(User user);
    
    // Map UserDTO to User entity (roles will be set manually in service)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "reviews", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    User toEntity(UserDTO dto);
    
    // Map NewUserDTO to User entity (roles will be set manually in service)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "roles", ignore = true)  // Ignore roles - service handles this
    @Mapping(target = "reviews", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    User toEntity(NewUserDTO dto);
    
    // Convert Set<Role> entities to comma-separated string for display
    default String mapRolesToString(Set<Role> roles) {
        if (roles == null || roles.isEmpty()) {
            return null;
        }
        return roles.stream()
                .map(Role::getName)
                .sorted()  // Sort alphabetically for consistency
                .collect(Collectors.joining(", "));
    }
    
    // Get the primary/first role (for summary views or backwards compatibility)
    default String getPrimaryRole(Set<Role> roles) {
        if (roles == null || roles.isEmpty()) {
            return null;
        }
        // Return first role alphabetically
        return roles.stream()
                .map(Role::getName)
                .sorted()
                .findFirst()
                .orElse(null);
    }
}