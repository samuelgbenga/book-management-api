package com.bookmanagement.service.impl;

import com.bookmanagement.dto.NewReviewDTO;
import com.bookmanagement.dto.ReviewDTO;
import com.bookmanagement.dto.UpdateReviewDTO;
import com.bookmanagement.entity.*;
import com.bookmanagement.exception.ResourceNotFoundException;
import com.bookmanagement.mapper.ReviewMapper;
import com.bookmanagement.repository.*;
import com.bookmanagement.service.ReviewService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewServiceImpl implements ReviewService {

     private final ReviewRepository reviewRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final ReviewMapper reviewMapper;

    @Override
      public List<ReviewDTO> getReviewsByBookId(Long bookId) {
        return reviewRepository.findByBookId(bookId).stream()
                .map(reviewMapper::toDTO)
                .toList();
    }
    @Override
    @Transactional
    public ReviewDTO createReview(Long bookId, NewReviewDTO reviewDTO) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + bookId));
        
        User user = userRepository.findById(reviewDTO.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + reviewDTO.getUserId()));
        
        Review review = reviewMapper.toEntity(reviewDTO);
        review.setBook(book);
        review.setUser(user);
        
        Review savedReview = reviewRepository.save(review);
        
        book.calculateRating();
        bookRepository.save(book);
        
        return reviewMapper.toDTO(savedReview);
    }


    @Override
     @Transactional
    public ReviewDTO updateReview(Long id, UpdateReviewDTO reviewDTO) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found with id: " + id));
        
        if (reviewDTO.getRating() != null) {
            review.setRating(reviewDTO.getRating());
        }
        
        if (reviewDTO.getComment() != null) {
            review.setComment(reviewDTO.getComment());
        }
        
        Review updatedReview = reviewRepository.save(review);
        
        review.getBook().calculateRating();
        bookRepository.save(review.getBook());
        
        return reviewMapper.toDTO(updatedReview);
    }

    @Override
     @Transactional
    public void deleteReview(Long id) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found with id: " + id));
        
        Book book = review.getBook();
        reviewRepository.deleteById(id);
        
        book.calculateRating();
        bookRepository.save(book);
    }
    
}
