package com.bookmanagement.controller;

import com.bookmanagement.annotation.UserOrAdmin;
import com.bookmanagement.dto.NewReviewDTO;
import com.bookmanagement.dto.ReviewDTO;
import com.bookmanagement.dto.UpdateReviewDTO;
import com.bookmanagement.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.hibernate.sql.Update;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Reviews", description = "Review management endpoints")
@SecurityRequirement(name = "Bearer Authentication")
public class ReviewController {
    
    private final ReviewService reviewService;
    
    @PostMapping("/books/{bookId}/reviews")
    @UserOrAdmin
    @Operation(summary = "Create a review for a book")
    public ResponseEntity<ReviewDTO> createReview(
            @PathVariable Long bookId,
            @Valid @RequestBody NewReviewDTO reviewDTO) {
        ReviewDTO createdReview = reviewService.createReview(bookId, reviewDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdReview);
    }
    
    @GetMapping("/books/{bookId}/reviews")
    @UserOrAdmin
    @Operation(summary = "Get all reviews for a book")
    public ResponseEntity<List<ReviewDTO>> getReviewsByBookId(@PathVariable Long bookId) {
        List<ReviewDTO> reviews = reviewService.getReviewsByBookId(bookId);
        return ResponseEntity.ok(reviews);
    }
    
    @PutMapping("/reviews/{id}")
    @UserOrAdmin
    @Operation(summary = "Update a review")
    public ResponseEntity<ReviewDTO> updateReview(
            @PathVariable Long id,
            @Valid @RequestBody UpdateReviewDTO reviewDTO) {
        ReviewDTO updatedReview = reviewService.updateReview(id, reviewDTO);
        return ResponseEntity.ok(updatedReview);
    }
    
    @DeleteMapping("/reviews/{id}")
    @UserOrAdmin
    @Operation(summary = "Delete a review")
    public ResponseEntity<Void> deleteReview(@PathVariable Long id) {
        reviewService.deleteReview(id);
        return ResponseEntity.noContent().build();
    }
}