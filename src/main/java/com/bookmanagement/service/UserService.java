package com.bookmanagement.service;

import com.bookmanagement.dto.NewUserDTO;
import com.bookmanagement.dto.UpdateUserDTO;
import com.bookmanagement.dto.UserResponseDTO;

public interface UserService {

    UserResponseDTO getUserById(Long id);

    UserResponseDTO createUser(NewUserDTO userDTO);

    UserResponseDTO updateUser(Long id, UpdateUserDTO userDTO);

    void deleteUser(Long id);
}
