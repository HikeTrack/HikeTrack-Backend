package com.hiketrackbackend.hiketrackbackend.service.impl;

import com.hiketrackbackend.hiketrackbackend.dto.reviews.ReviewRequestDto;
import com.hiketrackbackend.hiketrackbackend.dto.reviews.ReviewsRespondDto;
import com.hiketrackbackend.hiketrackbackend.exception.EntityNotFoundException;
import com.hiketrackbackend.hiketrackbackend.exception.InvalidIdException;
import com.hiketrackbackend.hiketrackbackend.mapper.ReviewMapper;
import com.hiketrackbackend.hiketrackbackend.model.tour.Review;
import com.hiketrackbackend.hiketrackbackend.model.tour.Tour;
import com.hiketrackbackend.hiketrackbackend.model.user.User;
import com.hiketrackbackend.hiketrackbackend.repository.ReviewRepository;
import com.hiketrackbackend.hiketrackbackend.repository.tour.TourRepository;
import com.hiketrackbackend.hiketrackbackend.service.ReviewService;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {
    private final ReviewRepository reviewRepository;
    private final ReviewMapper reviewMapper;
    private final TourRepository tourRepository;

    @Override
    public ReviewsRespondDto createReview(ReviewRequestDto requestDto, User user, Long tourId) {
        Tour tour = getTourById(tourId);
        Review review = reviewMapper.toEntity(requestDto);
        review.setDateCreated(LocalDateTime.now());
        review.setTour(tour);
        review.setUser(user);
        return reviewMapper.toDto(reviewRepository.save(review));
    }

    @Override
    @Transactional
    public ReviewsRespondDto updateReview(
            ReviewRequestDto requestDto,
            Long reviewId,
            Long tourId,
            Long userId) {
        isExistReviewByIdAndTourIdAndUserId(tourId, reviewId, userId);
        Review review = getReviewById(reviewId);
        reviewMapper.updateEntityFromDto(review, requestDto);
        return reviewMapper.toDto(reviewRepository.save(review));
    }

    @Override
    public List<ReviewsRespondDto> getAllByUserId(Long userId, Pageable pageable) {
        if (userId == null) {
            throw new InvalidIdException("User id cannot be null");
        }
        return reviewRepository.findReviewsByUserId(userId, pageable)
                .stream()
                .map(reviewMapper::toDto)
                .toList();
    }

    @Override
    public List<ReviewsRespondDto> getAllByTourId(Long tourId, Pageable pageable) {
        if (tourId == null) {
            throw new InvalidIdException("Tour id cannot be null");
        }
        return reviewRepository.findByTourId(tourId, pageable)
                .stream()
                .map(reviewMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public void deleteById(Long reviewId, Long userId, Long tourId) {
        isExistReviewByIdAndTourIdAndUserId(tourId, reviewId, userId);
        reviewRepository.deleteById(reviewId);
    }

    private void isExistReviewByIdAndTourIdAndUserId(Long tourId, Long reviewId, Long userId) {
        if (tourId == null || reviewId == null || userId == null) {
            throw new InvalidIdException("Tour id " + tourId + " or user id "
                    + userId + " or review id " + reviewId + " cannot be null");
        }
        boolean isExist = reviewRepository.existsByIdAndTourIdAndUserId(reviewId, tourId, userId);
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
