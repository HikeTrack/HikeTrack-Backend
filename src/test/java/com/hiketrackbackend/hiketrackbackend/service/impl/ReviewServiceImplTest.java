package com.hiketrackbackend.hiketrackbackend.service.impl;

import com.hiketrackbackend.hiketrackbackend.dto.reviews.ReviewRequestDto;
import com.hiketrackbackend.hiketrackbackend.dto.reviews.ReviewsRespondDto;
import com.hiketrackbackend.hiketrackbackend.exception.EntityNotFoundException;
import com.hiketrackbackend.hiketrackbackend.mapper.ReviewMapper;
import com.hiketrackbackend.hiketrackbackend.model.tour.Review;
import com.hiketrackbackend.hiketrackbackend.model.tour.Tour;
import com.hiketrackbackend.hiketrackbackend.model.user.User;
import com.hiketrackbackend.hiketrackbackend.repository.ReviewRepository;
import com.hiketrackbackend.hiketrackbackend.repository.tour.TourRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
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
    public void testCreateReviewWhenReviewIsSuccessfullyCreatedThenReturnReviewResponseDto() {
        // Arrange
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

        // Act
        ReviewsRespondDto result = reviewService.createReview(requestDto, user, tour.getId());

        // Assert
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
    public void testCreateReviewWhenTourIsNotFoundThenThrowEntityNotFoundException() {
        // Arrange
        ReviewRequestDto requestDto = new ReviewRequestDto();
        requestDto.setContent("Great tour!");

        User user = new User();
        user.setId(1L);

        Long tourId = 1L;

        when(tourRepository.findById(tourId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> reviewService.createReview(requestDto, user, tourId))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Tour not found with id " + tourId);

        verify(reviewMapper, never()).toEntity(any(ReviewRequestDto.class));
        verify(reviewRepository, never()).save(any(Review.class));
    }

    @Test
    public void testUpdateReviewWhenReviewIsSuccessfullyUpdatedThenReturnReviewsRespondDto() {
        // Arrange
        ReviewRequestDto requestDto = new ReviewRequestDto();
        requestDto.setContent("Updated review content");

        Tour tour = new Tour();
        tour.setId(1L);

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

        when(tourRepository.findTourByReviewsId(review.getId())).thenReturn(Optional.of(tour));
        when(reviewRepository.existsByIdAndTourId(review.getId(), tour.getId())).thenReturn(true);
        when(reviewRepository.findById(review.getId())).thenReturn(Optional.of(review));
        doNothing().when(reviewMapper).updateEntityFromDto(review, requestDto);
        when(reviewRepository.save(review)).thenReturn(review);
        when(reviewMapper.toDto(review)).thenReturn(responseDto);

        // Act
        ReviewsRespondDto result = reviewService.updateReview(requestDto, review.getId());

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEqualTo(requestDto.getContent());
        assertThat(result.getTourId()).isEqualTo(tour.getId());

        verify(tourRepository).findTourByReviewsId(review.getId());
        verify(reviewRepository).existsByIdAndTourId(review.getId(), tour.getId());
        verify(reviewRepository).findById(review.getId());
        verify(reviewMapper).updateEntityFromDto(review, requestDto);
        verify(reviewRepository).save(review);
        verify(reviewMapper).toDto(review);
    }

    @Test
    public void testUpdateReviewWhenReviewIsNotFoundThenThrowEntityNotFoundException() {
        // Arrange
        ReviewRequestDto requestDto = new ReviewRequestDto();
        requestDto.setContent("Updated review content");

        Long reviewId = 1L;

        when(tourRepository.findTourByReviewsId(reviewId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> reviewService.updateReview(requestDto, reviewId))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Tour not found with review id: " + reviewId);

        verify(reviewRepository, never()).existsByIdAndTourId(anyLong(), anyLong());
        verify(reviewRepository, never()).findById(anyLong());
        verify(reviewMapper, never()).updateEntityFromDto(any(Review.class), any(ReviewRequestDto.class));
        verify(reviewRepository, never()).save(any(Review.class));
    }

    @Test
    public void testUpdateReviewWhenReviewDoesNotExistForGivenTourThenThrowEntityNotFoundException() {
        // Arrange
        ReviewRequestDto requestDto = new ReviewRequestDto();
        requestDto.setContent("Updated review content");

        Tour tour = new Tour();
        tour.setId(1L);

        Long reviewId = 1L;

        when(tourRepository.findTourByReviewsId(reviewId)).thenReturn(Optional.of(tour));
        when(reviewRepository.existsByIdAndTourId(reviewId, tour.getId())).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> reviewService.updateReview(requestDto, reviewId))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Review not found with id " + reviewId + " and tour id " + tour.getId());

        verify(reviewRepository).existsByIdAndTourId(reviewId, tour.getId());
        verify(reviewRepository, never()).findById(anyLong());
        verify(reviewMapper, never()).updateEntityFromDto(any(Review.class), any(ReviewRequestDto.class));
        verify(reviewRepository, never()).save(any(Review.class));
    }

    @Test
    public void testGetAllByUserIdWhenRepositoryReturnsListOfReviewsThenReturnListOfReviewsRespondDto() {
        // Arrange
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

        // Act
        List<ReviewsRespondDto> result = reviewService.getAllByUserId(userId, pageable);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        assertThat(result).containsExactly(responseDto1, responseDto2);

        verify(reviewRepository).findReviewsByUserId(userId, pageable);
        verify(reviewMapper).toDto(review1);
        verify(reviewMapper).toDto(review2);
    }

    @Test
    public void testGetAllByUserIdWhenRepositoryReturnsEmptyListThenReturnEmptyList() {
        // Arrange
        Long userId = 1L;
        Pageable pageable = Pageable.unpaged();

        when(reviewRepository.findReviewsByUserId(userId, pageable)).thenReturn(Collections.emptyList());

        // Act
        List<ReviewsRespondDto> result = reviewService.getAllByUserId(userId, pageable);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();

        verify(reviewRepository).findReviewsByUserId(userId, pageable);
        verify(reviewMapper, never()).toDto(any(Review.class));
    }

    @Test
    public void testGetAllByTourIdWhenCalledWithValidParametersThenReturnsExpectedResult() {
        // Arrange
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

        // Act
        List<ReviewsRespondDto> result = reviewService.getAllByTourId(tourId, pageable);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        assertThat(result).containsExactly(responseDto1, responseDto2);

        verify(reviewRepository).findByTourId(tourId, pageable);
        verify(reviewMapper).toDto(review1);
        verify(reviewMapper).toDto(review2);
    }

    @Test
    public void testGetAllByTourIdWhenCalledWithNonExistentTourIdThenReturnsEmptyList() {
        // Arrange
        Long tourId = 1L;
        Pageable pageable = Pageable.unpaged();
        Page<Review> emptyPage = Page.empty(pageable);

        when(reviewRepository.findByTourId(tourId, pageable)).thenReturn(emptyPage);

        // Act
        List<ReviewsRespondDto> result = reviewService.getAllByTourId(tourId, pageable);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();

        verify(reviewRepository).findByTourId(tourId, pageable);
        verify(reviewMapper, never()).toDto(any(Review.class));
    }

    @Test
    public void testGetAllByTourIdWhenCalledWithNullTourIdThenThrowsIllegalArgumentException() {
        // Arrange
        Pageable pageable = Pageable.unpaged();

        // Act & Assert
        assertThatThrownBy(() -> reviewService.getAllByTourId(null, pageable))
                .isInstanceOf(IllegalArgumentException.class);

        verify(reviewRepository, never()).findByTourId(anyLong(), any(Pageable.class));
        verify(reviewMapper, never()).toDto(any(Review.class));
    }

    @Test
    public void testGetAllByTourIdWhenRepositoryReturnsListOfReviewsThenReturnListOfReviews() {
        // Arrange
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

        // Act
        List<ReviewsRespondDto> result = reviewService.getAllByTourId(tourId, pageable);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        assertThat(result).containsExactly(responseDto1, responseDto2);

        verify(reviewRepository).findByTourId(tourId, pageable);
        verify(reviewMapper).toDto(review1);
        verify(reviewMapper).toDto(review2);
    }

    @Test
    public void testGetAllByTourIdWhenRepositoryReturnsEmptyListThenReturnEmptyList() {
        // Arrange
        Long tourId = 1L;
        Pageable pageable = Pageable.unpaged();
        Page<Review> emptyPage = Page.empty(pageable);

        when(reviewRepository.findByTourId(tourId, pageable)).thenReturn(emptyPage);

        // Act
        List<ReviewsRespondDto> result = reviewService.getAllByTourId(tourId, pageable);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();

        verify(reviewRepository).findByTourId(tourId, pageable);
        verify(reviewMapper, never()).toDto(any(Review.class));
    }

    @Test
    public void testDeleteByIdWhenReviewExistsThenReviewIsDeleted() {
        // Arrange
        Long reviewId = 1L;
        Tour tour = new Tour();
        tour.setId(1L);

        when(tourRepository.findTourByReviewsId(reviewId)).thenReturn(Optional.of(tour));
        when(reviewRepository.existsByIdAndTourId(reviewId, tour.getId())).thenReturn(true);

        // Act
        reviewService.deleteById(reviewId);

        // Assert
        verify(tourRepository).findTourByReviewsId(reviewId);
        verify(reviewRepository).existsByIdAndTourId(reviewId, tour.getId());
        verify(reviewRepository).deleteById(reviewId);
    }

    @Test
    public void testDeleteByIdWhenReviewDoesNotExistThenExceptionIsThrown() {
        // Arrange
        Long reviewId = 1L;
        Tour tour = new Tour();
        tour.setId(1L);

        when(tourRepository.findTourByReviewsId(reviewId)).thenReturn(Optional.of(tour));
        when(reviewRepository.existsByIdAndTourId(reviewId, tour.getId())).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> reviewService.deleteById(reviewId))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Review not found with id " + reviewId + " and tour id " + tour.getId());

        verify(tourRepository).findTourByReviewsId(reviewId);
        verify(reviewRepository).existsByIdAndTourId(reviewId, tour.getId());
        verify(reviewRepository, never()).deleteById(reviewId);
    }
}