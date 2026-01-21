package com.bookmanagement.service;

import org.springframework.data.domain.Page;

import com.bookmanagement.dto.BookDTO;
import com.bookmanagement.dto.BookDetailDTO;
import com.bookmanagement.dto.BookPaginationDTO;
import com.bookmanagement.dto.GetAllBookParamsDTO;
import com.bookmanagement.dto.NewBookDTO;

import java.time.LocalDate;

public interface BookService {

    Page<BookPaginationDTO> getAllBooks(GetAllBookParamsDTO params);

    BookDetailDTO getBookById(Long id);

    BookDTO createBook(NewBookDTO bookDTO);

    BookDTO updateBook(Long id, NewBookDTO bookDTO);

    void deleteBook(Long id);
}
