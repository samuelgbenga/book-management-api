package com.bookmanagement.service;

import com.bookmanagement.dto.AuthResponse;
import com.bookmanagement.dto.LoginRequest;

public interface AuthService {

    AuthResponse login(LoginRequest request);
}
