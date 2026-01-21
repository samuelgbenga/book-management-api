package com.bookmanagement.service;

import com.bookmanagement.dto.NewReviewDTO;
import com.bookmanagement.dto.ReviewDTO;
import com.bookmanagement.dto.UpdateReviewDTO;

import java.util.List;

public interface ReviewService {

    List<ReviewDTO> getReviewsByBookId(Long bookId);

    ReviewDTO createReview(Long bookId, NewReviewDTO reviewDTO);

    ReviewDTO updateReview(Long id, UpdateReviewDTO reviewDTO);

    void deleteReview(Long id);
}
