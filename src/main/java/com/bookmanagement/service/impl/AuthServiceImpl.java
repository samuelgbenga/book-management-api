package com.bookmanagement.service.impl;

import com.bookmanagement.dto.*;
import com.bookmanagement.entity.User;
import com.bookmanagement.mapper.UserMapper;
import com.bookmanagement.repository.UserRepository;
import com.bookmanagement.security.JwtService;
import com.bookmanagement.service.AuthService;

//import com.bookmanagement.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.*;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserMapper userMapper;

    @Override
    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );
        
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        
        var jwtToken = jwtService.generateToken(
                new org.springframework.security.core.userdetails.User(
                        user.getUsername(),
                        user.getPassword(),
                        java.util.Collections.singletonList(
                                new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_" + user.getRole().name())
                        )
                )
        );
        
        return AuthResponse.builder()
                .token(jwtToken)
                .type("Bearer")
                .user(userMapper.toResponseDTO(user))
                .build();
    }
    
}
