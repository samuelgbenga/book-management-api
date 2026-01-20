package com.bookmanagement.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthorDetailDTO {
    private Long id;
    private String name;
    private String email;
    private String bio;
}