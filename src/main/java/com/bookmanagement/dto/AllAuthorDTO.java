package com.bookmanagement.dto;

import java.util.Set;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AllAuthorDTO {
    private Long id;
    
    private String name;
    
    private String email;
    
    private String bio;
    
    private Set<BookMinimalDTO> books;
}