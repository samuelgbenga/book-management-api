package com.bookmanagement.controller;

import com.bookmanagement.annotation.AdminOnly;
import com.bookmanagement.annotation.UserOrAdmin;
import com.bookmanagement.dto.AuthorDTO;
import com.bookmanagement.service.AuthorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/authors")
@RequiredArgsConstructor
@Tag(name = "Authors", description = "Author management endpoints")
@SecurityRequirement(name = "Bearer Authentication")
public class AuthorController {
    
    private final AuthorService authorService;
    
    @GetMapping
    @UserOrAdmin
    @Operation(summary = "Get all authors")
    public ResponseEntity<List<AuthorDTO>> getAllAuthors() {
        List<AuthorDTO> authors = authorService.getAllAuthors();
        return ResponseEntity.ok(authors);
    }
    
    @GetMapping("/{id}")
    @UserOrAdmin
    @Operation(summary = "Get author by ID with books")
    public ResponseEntity<AuthorDTO> getAuthorById(@PathVariable Long id) {
        AuthorDTO author = authorService.getAuthorById(id);
        return ResponseEntity.ok(author);
    }
    
    @PostMapping
    @AdminOnly
    @Operation(summary = "Create a new author (Admin only)")
    public ResponseEntity<AuthorDTO> createAuthor(@Valid @RequestBody AuthorDTO authorDTO) {
        AuthorDTO createdAuthor = authorService.createAuthor(authorDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdAuthor);
    }
    
    @PutMapping("/{id}")
    @AdminOnly
    @Operation(summary = "Update an existing author (Admin only)")
    public ResponseEntity<AuthorDTO> updateAuthor(
            @PathVariable Long id,
            @Valid @RequestBody AuthorDTO authorDTO) {
        AuthorDTO updatedAuthor = authorService.updateAuthor(id, authorDTO);
        return ResponseEntity.ok(updatedAuthor);
    }
    
    @DeleteMapping("/{id}")
    @AdminOnly
    @Operation(summary = "Delete an author (Admin only)")
    public ResponseEntity<Void> deleteAuthor(@PathVariable Long id) {
        authorService.deleteAuthor(id);
        return ResponseEntity.noContent().build();
    }
}