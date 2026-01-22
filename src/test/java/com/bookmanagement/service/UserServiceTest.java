package com.bookmanagement.service;

import com.bookmanagement.dto.*;
import com.bookmanagement.entity.Role;
import com.bookmanagement.entity.User;
import com.bookmanagement.exception.*;
import com.bookmanagement.mapper.UserMapper;
import com.bookmanagement.repository.RoleRepository;
import com.bookmanagement.repository.UserRepository;
import com.bookmanagement.service.impl.UserServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService Tests")
class UserServiceTest {
    
    @Mock
    private UserRepository userRepository;
    
    @Mock
    private RoleRepository roleRepository;
    
    @Mock
    private UserMapper userMapper;
    
    @Mock
    private PasswordEncoder passwordEncoder;
    
    @InjectMocks
    private UserServiceImpl userService;
    
    private User user;
    private UserDTO userDTO;
    private UserResponseDTO userResponseDTO;
    private Role userRole;
    private NewUserDTO newUserDTO;
    private UpdateUserDTO updateUserDTO;
    
    @BeforeEach
    void setUp() {
        userRole = Role.builder()
                .id(1L)
                .name("USER")
                .build();
        
        user = User.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .password("encodedPassword")
                .roles(new HashSet<>(Set.of(userRole)))
                .build();
        
        userDTO = UserDTO.builder()
                .username("testuser")
                .email("test@example.com")
                .password("password123")
                .role("USER")
                .build();
        
         newUserDTO = NewUserDTO.builder()
                .username("testuser")
                .email("test@example.com")
                .password("password123")
                .role("USER")
                .build();

        updateUserDTO = updateUserDTO.builder()
                .username("testuser")
                .email("test@example.com")
                .password("password123")
                .build();
        
        userResponseDTO = UserResponseDTO.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .role("USER")
                .build();
    }
    
    @Test
    @DisplayName("Should get user by ID successfully")
    void testGetUserById_Success() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userMapper.toResponseDTO(user)).thenReturn(userResponseDTO);
        
        // Act
        UserResponseDTO result = userService.getUserById(1L);
        
        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo("testuser");
    }
    
    @Test
    @DisplayName("Should throw ResourceNotFoundException when user not found")
    void testGetUserById_NotFound() {
        // Arrange
        when(userRepository.findById(999L)).thenReturn(Optional.empty());
        
        // Act & Assert
        assertThatThrownBy(() -> userService.getUserById(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("User not found with id: 999");
    }
    
    @Test
    @DisplayName("Should create user successfully")
    void testCreateUser_Success() {
        // Arrange
        when(userRepository.findByUsername(userDTO.getUsername())).thenReturn(Optional.empty());
        when(userRepository.findByEmail(userDTO.getEmail())).thenReturn(Optional.empty());
        when(roleRepository.findByName("USER")).thenReturn(Optional.of(userRole));
        when(userMapper.toEntity(any(NewUserDTO.class))).thenReturn(user);
        when(passwordEncoder.encode(userDTO.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userMapper.toResponseDTO(user)).thenReturn(userResponseDTO);
        
        // Act
        UserResponseDTO result = userService.createUser(newUserDTO);
        
        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo("testuser");
        verify(passwordEncoder, times(1)).encode(userDTO.getPassword());
    }
    
    @Test
    @DisplayName("Should throw DuplicateResourceException when username exists")
    void testCreateUser_DuplicateUsername() {
        // Arrange
        when(userRepository.findByUsername(userDTO.getUsername())).thenReturn(Optional.of(user));
        
        // Act & Assert
        assertThatThrownBy(() -> userService.createUser(newUserDTO))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("Username already exists");
    }
    
    @Test
    @DisplayName("Should throw DuplicateResourceException when email exists")
    void testCreateUser_DuplicateEmail() {
        // Arrange
        when(userRepository.findByUsername(userDTO.getUsername())).thenReturn(Optional.empty());
        when(userRepository.findByEmail(userDTO.getEmail())).thenReturn(Optional.of(user));
        
        // Act & Assert
        assertThatThrownBy(() -> userService.createUser(newUserDTO))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("Email already exists");
    }
    
    @Test
    @DisplayName("Should update user successfully")
    void testUpdateUser_Success() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userMapper.toResponseDTO(user)).thenReturn(userResponseDTO);
        
        UpdateUserDTO updateUserDTO = UpdateUserDTO.builder().username("newusername").build();
        
        // Act
        UserResponseDTO result = userService.updateUser(1L, updateUserDTO);
        
        // Assert
        assertThat(result).isNotNull();
    }
    
    @Test
    @DisplayName("Should delete user successfully")
    void testDeleteUser_Success() {
        // Arrange
        when(userRepository.existsById(1L)).thenReturn(true);
        
        // Act
        userService.deleteUser(1L);
        
        // Assert
        verify(userRepository, times(1)).deleteById(1L);
    }
}
