package com.bookmanagement.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewMinimalDTO {
    private Long id;
    
    private Integer rating;
    
    private String comment;
   
    private UserSummaryDTO user;
    
    private LocalDateTime createdAt;
}