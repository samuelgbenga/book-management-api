package com.bookmanagement.dto;

import lombok.*;
import java.time.LocalDate;
import java.util.Set;

// Book DTOs
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookPaginationDTO {
    private Long id;
  
    private String title;

    private String isbn;
    
    private LocalDate publishedDate;
    
    private AuthorSummaryDTO author;
    private Set<CategoryDTO> categories;
    private Double rating;
}
