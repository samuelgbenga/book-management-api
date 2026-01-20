package com.bookmanagement.mapper;

import com.bookmanagement.dto.*;
import com.bookmanagement.entity.*;
import org.mapstruct.*;


@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {
    
    @Mapping(target = "role", source = "role")
    UserResponseDTO toResponseDTO(User user);
    
    UserSummaryDTO toSummaryDTO(User user);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "role", expression = "java(mapRole(dto.getRole()))")
    @Mapping(target = "reviews", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    User toEntity(UserDTO dto);
    
    default Role mapRole(String role) {
        return Role.valueOf(role.toUpperCase());
    }
}