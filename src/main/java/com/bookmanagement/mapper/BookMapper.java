package com.bookmanagement.mapper;

import com.bookmanagement.dto.*;
import com.bookmanagement.entity.*;
import org.mapstruct.*;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BookMapper {
    
    @Mapping(target = "authorId", source = "author.id")
    @Mapping(target = "categoryIds", expression = "java(mapCategoryIds(book.getCategories()))")
    BookDTO toDTO(Book book);

    BookMinimalDTO toBookMinimalDTO(Book book);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "author", ignore = true)
    @Mapping(target = "categories", ignore = true)
    @Mapping(target = "reviews", ignore = true)
    @Mapping(target = "rating", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Book toEntity(BookDTO dto);
    
    BookDetailDTO toDetailDTO(Book book);
    BookSummaryDTO toSummaryDTO(Book book);
    
    default Set<Long> mapCategoryIds(Set<Category> categories) {
        return categories.stream()
                .map(Category::getId)
                .collect(Collectors.toSet());
    }
}