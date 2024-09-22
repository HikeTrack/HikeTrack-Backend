package com.hiketrackbackend.hiketrackbackend.service.impl;

import com.hiketrackbackend.hiketrackbackend.dto.reviews.ReviewRequestDto;
import com.hiketrackbackend.hiketrackbackend.dto.reviews.ReviewsRespondDto;
import com.hiketrackbackend.hiketrackbackend.exception.EntityNotFoundException;
import com.hiketrackbackend.hiketrackbackend.mapper.ReviewMapper;
import com.hiketrackbackend.hiketrackbackend.model.Review;
import com.hiketrackbackend.hiketrackbackend.model.User;
import com.hiketrackbackend.hiketrackbackend.model.tour.Tour;
import com.hiketrackbackend.hiketrackbackend.repository.ReviewRepository;
import com.hiketrackbackend.hiketrackbackend.repository.tour.TourRepository;
import com.hiketrackbackend.hiketrackbackend.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {
    private final ReviewRepository reviewRepository;
    private final ReviewMapper reviewMapper;
    private final TourRepository tourRepository;

    @Override
    public ReviewsRespondDto createReview(ReviewRequestDto requestDto, User user, Long tourId) {
        Review review = reviewMapper.toEntity(requestDto);
        review.setDateCreated(LocalDateTime.now());
        review.setTour(getTourById(tourId));
        review.setUser(user);
        return reviewMapper.toDto(reviewRepository.save(review));
    }

    @Override
    @Transactional
    public void deleteById(Long reviewId, Long tourId) {
        isExistReviewByIdAndTourId(tourId, reviewId);
        reviewRepository.deleteById(reviewId);
    }

    @Override
    @Transactional
    public ReviewsRespondDto updateReview(ReviewRequestDto requestDto, Long tourId, Long reviewId) {
        isExistReviewByIdAndTourId(tourId, reviewId);
        Review review = getReviewById(reviewId);
        reviewMapper.updateEntityFromDto(review, requestDto);
        return reviewMapper.toDto(reviewRepository.save(review));
    }

    private void isExistReviewByIdAndTourId(Long tourId, Long reviewId) {
        boolean isExist = reviewRepository.existsByIdAndTourId(reviewId, tourId);
        if (!isExist) {
            throw new EntityNotFoundException("Review not found with id " + reviewId
                    + " and tour id " + tourId);
        }
    }
    private Review getReviewById(Long reviewId) {
        return reviewRepository.findById(reviewId).orElseThrow(
                () -> new EntityNotFoundException("Review not found with id " + reviewId)
        );
    }

    private Tour getTourById(Long tourId) {
        return tourRepository.findById(tourId).orElseThrow(
                () -> new EntityNotFoundException("Tour not found with id " + tourId)
        );
    }
}
