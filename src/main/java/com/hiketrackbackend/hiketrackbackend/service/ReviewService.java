package com.hiketrackbackend.hiketrackbackend.service;

import com.hiketrackbackend.hiketrackbackend.dto.reviews.ReviewRequestDto;
import com.hiketrackbackend.hiketrackbackend.dto.reviews.ReviewsRespondDto;
import com.hiketrackbackend.hiketrackbackend.model.user.User;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface ReviewService {
    ReviewsRespondDto createReview(ReviewRequestDto requestDto, User user, Long tourId);

    ReviewsRespondDto updateReview(
            ReviewRequestDto requestDto,
            Long reviewId,
            Long tourId,
            Long userId
    );

    List<ReviewsRespondDto> getAllByUserId(Long userId, Pageable pageable);

    List<ReviewsRespondDto> getAllByTourId(Long tourId, Pageable pageable);

    void deleteById(Long reviewId, Long userId, Long tourId);
}
