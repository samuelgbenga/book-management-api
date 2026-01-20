package com.bookmanagement.dto;

import java.time.LocalDate;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BookSummaryDTO {
    private Long id;
    private String title;
    private String isbn;
    private LocalDate publishedDate;
}