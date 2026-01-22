package com.bookmanagement.service;

import com.bookmanagement.dto.NewReviewDTO;
import com.bookmanagement.dto.ReviewDTO;
import com.bookmanagement.dto.UpdateReviewDTO;
import com.bookmanagement.entity.*;
import com.bookmanagement.exception.ResourceNotFoundException;
import com.bookmanagement.mapper.ReviewMapper;
import com.bookmanagement.repository.*;
import com.bookmanagement.service.impl.ReviewServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ReviewService Tests")
class ReviewServiceTest {
    
    @Mock
    private ReviewRepository reviewRepository;
    
    @Mock
    private BookRepository bookRepository;
    
    @Mock
    private UserRepository userRepository;
    
    @Mock
    private ReviewMapper reviewMapper;
    
    @InjectMocks
    private ReviewServiceImpl reviewService;
    
    private Book book;
    private User user;
    private Review review;
    private ReviewDTO reviewDTO;
    private NewReviewDTO newReviewDTO;
    private UpdateReviewDTO updateReviewDTO;
    
    @BeforeEach
    void setUp() {
        book = Book.builder()
                .id(1L)
                .title("Clean Code")
                .rating(0.0)
                .reviews(new HashSet<>())
                .build();
        
        user = User.builder()
                .id(1L)
                .username("testuser")
                .build();
        
        review = Review.builder()
                .id(1L)
                .book(book)
                .user(user)
                .rating(5)
                .comment("Great book!")
                .build();

        newReviewDTO = NewReviewDTO.builder()
                .userId(1L)
                .rating(5)
                .comment("Great book!")
                .build();

        updateReviewDTO = UpdateReviewDTO.builder()
                .rating(5)
                .comment("Great book!")
                .build();
        
        reviewDTO = ReviewDTO.builder()
                .id(1L)
                .rating(5)
                .comment("Great book!")
                .build();
    }
    
    @Test
    @DisplayName("Should get reviews by book ID successfully")
    void testGetReviewsByBookId_Success() {
        // Arrange
        when(reviewRepository.findByBookId(1L)).thenReturn(List.of(review));
        when(reviewMapper.toDTO(review)).thenReturn(reviewDTO);
        
        // Act
        List<ReviewDTO> result = reviewService.getReviewsByBookId(1L);
        
        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getRating()).isEqualTo(5);
    }
    
    @Test
    @DisplayName("Should create review successfully")
    void testCreateReview_Success() {
        // Arrange
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(reviewMapper.toEntity(newReviewDTO)).thenReturn(review);
        when(reviewRepository.save(any(Review.class))).thenReturn(review);
        when(bookRepository.save(any(Book.class))).thenReturn(book);
        when(reviewMapper.toDTO(review)).thenReturn(reviewDTO);
        
        // Act
        ReviewDTO result = reviewService.createReview(1L, newReviewDTO);
        
        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getRating()).isEqualTo(5);
        verify(bookRepository, times(1)).save(any(Book.class));
    }
    
    @Test
    @DisplayName("Should throw ResourceNotFoundException when book not found for review")
    void testCreateReview_BookNotFound() {
        // Arrange
        when(bookRepository.findById(999L)).thenReturn(Optional.empty());
        
        // Act & Assert
        assertThatThrownBy(() -> reviewService.createReview(999L, newReviewDTO))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Book not found");
    }
    
    @Test
    @DisplayName("Should update review successfully")
    void testUpdateReview_Success() {
        // Arrange
        when(reviewRepository.findById(1L)).thenReturn(Optional.of(review));
        when(reviewRepository.save(any(Review.class))).thenReturn(review);
        when(bookRepository.save(any(Book.class))).thenReturn(book);
        when(reviewMapper.toDTO(review)).thenReturn(reviewDTO);
        
        UpdateReviewDTO updateReviewDTO = UpdateReviewDTO.builder().rating(4).comment("Updated").build();
        
        // Act
        ReviewDTO result = reviewService.updateReview(1L, updateReviewDTO);
        
        // Assert
        assertThat(result).isNotNull();
    }
    
    @Test
    @DisplayName("Should delete review successfully")
    void testDeleteReview_Success() {
        // Arrange
        when(reviewRepository.findById(1L)).thenReturn(Optional.of(review));
        when(bookRepository.save(any(Book.class))).thenReturn(book);
        
        // Act
        reviewService.deleteReview(1L);
        
        // Assert
        verify(reviewRepository, times(1)).deleteById(1L);
        verify(bookRepository, times(1)).save(any(Book.class));
    }
}