package com.hiketrackbackend.hiketrackbackend.service;

import com.hiketrackbackend.hiketrackbackend.dto.reviews.ReviewRequestDto;
import com.hiketrackbackend.hiketrackbackend.dto.reviews.ReviewsRespondDto;
import com.hiketrackbackend.hiketrackbackend.model.User;

public interface ReviewService {
    ReviewsRespondDto createReview(ReviewRequestDto requestDto, User user, Long tourId);

    void deleteById(Long reviewId, Long tourId);

    ReviewsRespondDto updateReview(ReviewRequestDto requestDto, Long tourId, Long reviewId);
}