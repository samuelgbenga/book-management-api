package com.bookmanagement.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserSummaryDTO {
    private Long id;
    private String username;
}