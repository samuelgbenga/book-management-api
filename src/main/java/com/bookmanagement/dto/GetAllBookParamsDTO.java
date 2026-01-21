package com.bookmanagement.dto;

import java.time.LocalDate;

public record GetAllBookParamsDTO(
        Integer page,
        Integer size,
        Long authorId,
        Long categoryId,
        Double ratingMin,
        Double ratingMax,
        LocalDate publishedStart,
        LocalDate publishedEnd,
        String sortBy
) {}
