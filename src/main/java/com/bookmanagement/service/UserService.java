package com.bookmanagement.service;

import com.bookmanagement.dto.UserDTO;
import com.bookmanagement.dto.UserResponseDTO;

public interface UserService {

    UserResponseDTO getUserById(Long id);

    UserResponseDTO createUser(UserDTO userDTO);

    UserResponseDTO updateUser(Long id, UserDTO userDTO);

    void deleteUser(Long id);
}
