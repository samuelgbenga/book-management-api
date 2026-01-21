package com.bookmanagement.mapper;

import com.bookmanagement.dto.*;
import com.bookmanagement.entity.*;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AuthorMapper {
    AuthorDTO toDTO(Author author);

    AllAuthorDTO toAllDTO(Author author);

    AuthorDetailDTO toDetailDTO(Author author);
    AuthorSummaryDTO toSummaryDTO(Author author);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "books", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Author toEntity(AuthorDTO dto);

    Author toEntity(NewAuthorDTO dto);
}