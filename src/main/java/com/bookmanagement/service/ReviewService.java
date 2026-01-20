package com.bookmanagement.service;

import com.bookmanagement.dto.ReviewDTO;

import java.util.List;

public interface ReviewService {

    List<ReviewDTO> getReviewsByBookId(Long bookId);

    ReviewDTO createReview(Long bookId, ReviewDTO reviewDTO);

    ReviewDTO updateReview(Long id, ReviewDTO reviewDTO);

    void deleteReview(Long id);
}
