package com.bookmanagement.controller;

import com.bookmanagement.annotation.AdminOnly;
import com.bookmanagement.annotation.UserOrAdmin;
import com.bookmanagement.dto.*;
import com.bookmanagement.service.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
@Tag(name = "Books", description = "Book management endpoints")
@SecurityRequirement(name = "Bearer Authentication")
public class BookController {
    
    private final BookService bookService;
    
    @GetMapping
    @UserOrAdmin
    @Operation(summary = "Get all books with filtering and pagination")
    public ResponseEntity<Page<BookDTO>> getAllBooks(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) Long authorId,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Double ratingMin,
            @RequestParam(required = false) Double ratingMax,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate publishedStart,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate publishedEnd,
            @RequestParam(required = false) String sortBy) {
        
        Page<BookDTO> books = bookService.getAllBooks(
                page, size, authorId, categoryId, ratingMin, ratingMax,
                publishedStart, publishedEnd, sortBy
        );
        return ResponseEntity.ok(books);
    }
    
    @GetMapping("/{id}")
    @UserOrAdmin
    @Operation(summary = "Get book by ID with full details")
    public ResponseEntity<BookDetailDTO> getBookById(@PathVariable Long id) {
        BookDetailDTO book = bookService.getBookById(id);
        return ResponseEntity.ok(book);
    }
    
    @PostMapping
    @AdminOnly
    @Operation(summary = "Create a new book (Admin only)")
    public ResponseEntity<BookDTO> createBook(@Valid @RequestBody BookDTO bookDTO) {
        BookDTO createdBook = bookService.createBook(bookDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdBook);
    }
    
    @PutMapping("/{id}")
    @AdminOnly
    @Operation(summary = "Update an existing book (Admin only)")
    public ResponseEntity<BookDTO> updateBook(
            @PathVariable Long id,
            @Valid @RequestBody BookDTO bookDTO) {
        BookDTO updatedBook = bookService.updateBook(id, bookDTO);
        return ResponseEntity.ok(updatedBook);
    }
    
    @DeleteMapping("/{id}")
    @AdminOnly
    @Operation(summary = "Delete a book (Admin only)")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
        return ResponseEntity.noContent().build();
    }
}