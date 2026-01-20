package com.bookmanagement.dto;

import java.time.LocalDate;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookDetailDTO {
    private Long id;
    private String title;
    private String isbn;
    private LocalDate publishedDate;
    private AuthorDetailDTO author;
    private Set<CategoryDTO> categories;
    private Double rating;
    private Set<ReviewDTO> reviews;
}