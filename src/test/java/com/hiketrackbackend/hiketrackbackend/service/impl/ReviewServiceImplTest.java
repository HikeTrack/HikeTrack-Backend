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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReviewServiceImplTest {
    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private ReviewMapper reviewMapper;

    @Mock
    private TourRepository tourRepository;

    @InjectMocks
    private ReviewServiceImpl reviewService;

    @Test
    @DisplayName("Create review with valid data")
    public void testCreateReviewWhenReviewIsSuccessfullyCreatedThenReturnReviewResponseDto() {
        ReviewRequestDto requestDto = new ReviewRequestDto();
        requestDto.setContent("Great tour!");

        User user = new User();
        user.setId(1L);

        Tour tour = new Tour();
        tour.setId(1L);

        Review review = new Review();
        review.setContent(requestDto.getContent());
        review.setDateCreated(LocalDateTime.now());
        review.setTour(tour);
        review.setUser(user);

        ReviewsRespondDto responseDto = new ReviewsRespondDto();
        responseDto.setId(1L);
        responseDto.setContent(requestDto.getContent());
        responseDto.setUserId(user.getId());
        responseDto.setTourId(tour.getId());
        responseDto.setDateCreated(LocalDateTime.now());

        when(reviewMapper.toEntity(requestDto)).thenReturn(review);
        when(tourRepository.findById(tour.getId())).thenReturn(Optional.of(tour));
        when(reviewRepository.save(any(Review.class))).thenReturn(review);
        when(reviewMapper.toDto(any(Review.class))).thenReturn(responseDto);

        ReviewsRespondDto result = reviewService.createReview(requestDto, user, tour.getId());

        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEqualTo(requestDto.getContent());
        assertThat(result.getUserId()).isEqualTo(user.getId());
        assertThat(result.getTourId()).isEqualTo(tour.getId());

        verify(reviewMapper).toEntity(requestDto);
        verify(tourRepository).findById(tour.getId());
        verify(reviewRepository).save(any(Review.class));
        verify(reviewMapper).toDto(any(Review.class));
    }

    @Test
    @DisplayName("Create review with not valid tour id")
    public void testCreateReviewWhenTourIsNotFoundThenThrowEntityNotFoundException() {
        ReviewRequestDto requestDto = new ReviewRequestDto();
        requestDto.setContent("Great tour!");

        User user = new User();
        user.setId(1L);

        Long tourId = 1L;

        when(tourRepository.findById(tourId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reviewService.createReview(requestDto, user, tourId))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Tour not found with id " + tourId);

        verify(reviewMapper, never()).toEntity(any(ReviewRequestDto.class));
        verify(reviewRepository, never()).save(any(Review.class));
    }

    @Test
    @DisplayName("Update review with valid data")
    public void testUpdateReviewWhenReviewIsSuccessfullyUpdatedThenReturnReviewsRespondDto() {
        ReviewRequestDto requestDto = new ReviewRequestDto();
        requestDto.setContent("Updated review content");

        Tour tour = new Tour();
        tour.setId(1L);

        User user = new User();
        user.setId(1L);

        Review review = new Review();
        review.setId(1L);
        review.setContent("Old review content");
        review.setTour(tour);

        ReviewsRespondDto responseDto = new ReviewsRespondDto();
        responseDto.setId(review.getId());
        responseDto.setContent(requestDto.getContent());
        responseDto.setUserId(1L);
        responseDto.setTourId(tour.getId());
        responseDto.setDateCreated(LocalDateTime.now());

        when(reviewRepository.existsByIdAndTourIdAndUserId(review.getId(), tour.getId(), user.getId())).thenReturn(true);
        when(reviewRepository.findById(review.getId())).thenReturn(Optional.of(review));
        doNothing().when(reviewMapper).updateEntityFromDto(review, requestDto);
        when(reviewRepository.save(review)).thenReturn(review);
        when(reviewMapper.toDto(review)).thenReturn(responseDto);

        ReviewsRespondDto result = reviewService.updateReview(requestDto, review.getId(), tour.getId(), user.getId());

        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEqualTo(requestDto.getContent());
        assertThat(result.getTourId()).isEqualTo(tour.getId());

        verify(reviewRepository).existsByIdAndTourIdAndUserId(review.getId(), tour.getId(), user.getId());
        verify(reviewRepository).findById(review.getId());
        verify(reviewMapper).updateEntityFromDto(review, requestDto);
        verify(reviewRepository).save(review);
        verify(reviewMapper).toDto(review);
    }

    @Test
    @DisplayName("Update review with not valid tour id")
    public void testUpdateReviewWhenReviewIsNotFoundThenThrowEntityNotFoundException() {
        ReviewRequestDto requestDto = new ReviewRequestDto();
        requestDto.setContent("Updated review content");

        Long reviewId = 1L;
        Long userId = 1L;
        Long tourId = -1L;

        assertThatThrownBy(() -> reviewService.updateReview(requestDto, reviewId, tourId, userId))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Review not found with id " + reviewId
                        + " and tour id " + tourId);

        verify(reviewRepository).existsByIdAndTourIdAndUserId(reviewId, tourId, userId);
        verify(reviewRepository, never()).findById(anyLong());
        verify(reviewMapper, never()).updateEntityFromDto(any(Review.class), any(ReviewRequestDto.class));
        verify(reviewRepository, never()).save(any(Review.class));
    }

    @Test
    @DisplayName("Update review which not exist for tour id")
    public void testUpdateReviewWhenReviewDoesNotExistForGivenTourThenThrowEntityNotFoundException() {
        ReviewRequestDto requestDto = new ReviewRequestDto();
        requestDto.setContent("Updated review content");

        Tour tour = new Tour();
        tour.setId(1L);

        User user = new User();
        user.setId(1L);

        Long reviewId = 20L;

        when(reviewRepository.existsByIdAndTourIdAndUserId(reviewId, tour.getId(), user.getId())).thenReturn(false);

        assertThatThrownBy(() -> reviewService.updateReview(requestDto, reviewId, tour.getId(), user.getId()))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Review not found with id " + reviewId + " and tour id " + tour.getId());

        verify(reviewRepository).existsByIdAndTourIdAndUserId(reviewId, tour.getId(), user.getId());
        verify(reviewRepository, never()).findById(anyLong());
        verify(reviewMapper, never()).updateEntityFromDto(any(Review.class), any(ReviewRequestDto.class));
        verify(reviewRepository, never()).save(any(Review.class));
    }

    @Test
    @DisplayName("Get all review by user id with all valid data")
    public void testGetAllByUserIdWhenRepositoryReturnsListOfReviewsThenReturnListOfReviewsRespondDto() {
        Long userId = 1L;
        Pageable pageable = Pageable.unpaged();

        Review review1 = new Review();
        review1.setId(1L);
        review1.setContent("Review 1");

        Review review2 = new Review();
        review2.setId(2L);
        review2.setContent("Review 2");

        List<Review> reviews = List.of(review1, review2);

        ReviewsRespondDto responseDto1 = new ReviewsRespondDto();
        responseDto1.setId(review1.getId());
        responseDto1.setContent(review1.getContent());

        ReviewsRespondDto responseDto2 = new ReviewsRespondDto();
        responseDto2.setId(review2.getId());
        responseDto2.setContent(review2.getContent());

        when(reviewRepository.findReviewsByUserId(userId, pageable)).thenReturn(reviews);
        when(reviewMapper.toDto(review1)).thenReturn(responseDto1);
        when(reviewMapper.toDto(review2)).thenReturn(responseDto2);

        List<ReviewsRespondDto> result = reviewService.getAllByUserId(userId, pageable);

        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        assertThat(result).containsExactly(responseDto1, responseDto2);

        verify(reviewRepository).findReviewsByUserId(userId, pageable);
        verify(reviewMapper).toDto(review1);
        verify(reviewMapper).toDto(review2);
    }

    @Test
    @DisplayName("Get all review by user id with no data in DB")
    public void testGetAllByUserIdWhenRepositoryReturnsEmptyListThenReturnEmptyList() {
        Long userId = 1L;
        Pageable pageable = Pageable.unpaged();

        when(reviewRepository.findReviewsByUserId(userId, pageable)).thenReturn(Collections.emptyList());

        List<ReviewsRespondDto> result = reviewService.getAllByUserId(userId, pageable);

        assertThat(result).isNotNull();
        assertThat(result).isEmpty();

        verify(reviewRepository).findReviewsByUserId(userId, pageable);
        verify(reviewMapper, never()).toDto(any(Review.class));
    }

    @Test
    @DisplayName("Get all review with valid data")
    public void testGetAllByTourIdWhenCalledWithValidParametersThenReturnsExpectedResult() {
        Long tourId = 1L;
        Pageable pageable = Pageable.unpaged();

        Review review1 = new Review();
        review1.setId(1L);
        review1.setContent("Review 1");

        Review review2 = new Review();
        review2.setId(2L);
        review2.setContent("Review 2");

        List<Review> reviews = List.of(review1, review2);
        Page<Review> reviewPage = new PageImpl<>(reviews, pageable, reviews.size());

        ReviewsRespondDto responseDto1 = new ReviewsRespondDto();
        responseDto1.setId(review1.getId());
        responseDto1.setContent(review1.getContent());

        ReviewsRespondDto responseDto2 = new ReviewsRespondDto();
        responseDto2.setId(review2.getId());
        responseDto2.setContent(review2.getContent());

        when(reviewRepository.findByTourId(tourId, pageable)).thenReturn(reviewPage);
        when(reviewMapper.toDto(review1)).thenReturn(responseDto1);
        when(reviewMapper.toDto(review2)).thenReturn(responseDto2);

        List<ReviewsRespondDto> result = reviewService.getAllByTourId(tourId, pageable);

        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        assertThat(result).containsExactly(responseDto1, responseDto2);

        verify(reviewRepository).findByTourId(tourId, pageable);
        verify(reviewMapper).toDto(review1);
        verify(reviewMapper).toDto(review2);
    }

    @Test
    @DisplayName("Get all review by tour id with exception")
    public void testGetAllByTourIdWhenCalledWithNonExistentTourIdThenReturnsEmptyList() {
        Long tourId = 1L;
        Pageable pageable = Pageable.unpaged();
        Page<Review> emptyPage = Page.empty(pageable);

        when(reviewRepository.findByTourId(tourId, pageable)).thenReturn(emptyPage);

        List<ReviewsRespondDto> result = reviewService.getAllByTourId(tourId, pageable);

        assertThat(result).isNotNull();
        assertThat(result).isEmpty();

        verify(reviewRepository).findByTourId(tourId, pageable);
        verify(reviewMapper, never()).toDto(any(Review.class));
    }

    @Test
    @DisplayName("Get all review by tour null id ")
    public void testGetAllByTourIdWhenCalledWithNullTourIdThenThrowsIllegalArgumentException() {
        Pageable pageable = Pageable.unpaged();

        assertThatThrownBy(() -> reviewService.getAllByTourId(null, pageable))
                .isInstanceOf(InvalidIdException.class);

        verify(reviewRepository, never()).findByTourId(anyLong(), any(Pageable.class));
        verify(reviewMapper, never()).toDto(any(Review.class));
    }

    @Test
    @DisplayName("Get all review by tour id with no data in DB")
    public void testGetAllByTourIdWhenRepositoryReturnsEmptyListThenReturnEmptyList() {
        Long tourId = 1L;
        Pageable pageable = Pageable.unpaged();
        Page<Review> emptyPage = Page.empty(pageable);

        when(reviewRepository.findByTourId(tourId, pageable)).thenReturn(emptyPage);

        List<ReviewsRespondDto> result = reviewService.getAllByTourId(tourId, pageable);

        assertThat(result).isNotNull();
        assertThat(result).isEmpty();

        verify(reviewRepository).findByTourId(tourId, pageable);
        verify(reviewMapper, never()).toDto(any(Review.class));
    }

    @Test
    @DisplayName("Delete review with valid data")
    public void testDeleteById_SuccessfulDeletion() {
        Long reviewId = 1L;
        Long userId = 1L;
        Long tourId = 1L;

        when(reviewRepository.existsByIdAndTourIdAndUserId(reviewId, tourId, userId)).thenReturn(true);

        reviewService.deleteById(reviewId, userId, tourId);

        verify(reviewRepository, times(1)).deleteById(reviewId);
    }

    @Test
    @DisplayName("Delete when review not exist")
    public void testDeleteById_ReviewDoesNotExist() {
        Long reviewId = 2L;
        Long userId = 2L;
        Long tourId = 2L;

        doThrow(new EntityNotFoundException("Review not found"))
                .when(reviewRepository).existsByIdAndTourIdAndUserId(reviewId, tourId, userId);


        assertThrows(EntityNotFoundException.class, () -> {
            reviewService.deleteById(reviewId, userId, tourId);
        });

        verify(reviewRepository, never()).deleteById(anyLong());
    }
}
