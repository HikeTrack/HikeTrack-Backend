package com.hiketrackbackend.hiketrackbackend.service;

import com.hiketrackbackend.hiketrackbackend.dto.reviews.ReviewRequestDto;
import com.hiketrackbackend.hiketrackbackend.dto.reviews.ReviewsRespondDto;
import com.hiketrackbackend.hiketrackbackend.model.User;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface ReviewService {
    ReviewsRespondDto createReview(ReviewRequestDto requestDto, User user, Long tourId);

    ReviewsRespondDto updateReview(ReviewRequestDto requestDto, Long reviewId);

    List<ReviewsRespondDto> getAllByUserId(Long userId, Pageable pageable);

    void deleteById(Long reviewId);
}
