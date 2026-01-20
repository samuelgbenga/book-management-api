package com.bookmanagement.service;

import org.springframework.data.domain.Page;

import com.bookmanagement.dto.BookDTO;
import com.bookmanagement.dto.BookDetailDTO;

import java.time.LocalDate;

public interface BookService {

    Page<BookDTO> getAllBooks(
            Integer page,
            Integer size,
            Long authorId,
            Long categoryId,
            Double ratingMin,
            Double ratingMax,
            LocalDate publishedStart,
            LocalDate publishedEnd,
            String sortBy
    );

    BookDetailDTO getBookById(Long id);

    BookDTO createBook(BookDTO bookDTO);

    BookDTO updateBook(Long id, BookDTO bookDTO);

    void deleteBook(Long id);
}
