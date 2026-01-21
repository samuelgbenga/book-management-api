package com.bookmanagement.dto;

import com.bookmanagement.annotation.ValidISBN;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;
import java.util.Set;

// Book DTOs
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewBookDTO {
    
    @NotBlank(message = "Title is required")
    private String title;
    
    @ValidISBN
    @NotBlank(message = "ISBN is required")
    private String isbn;
    
    private LocalDate publishedDate;
    
    @NotNull(message = "Author ID is required")
    private Long authorId;
    
    @NotEmpty(message = "At least one category is required")
    private Set<Long> categoryIds;

}
