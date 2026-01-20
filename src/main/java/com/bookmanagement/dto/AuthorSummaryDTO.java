package com.bookmanagement.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthorSummaryDTO {
    private Long id;
    private String name;
}