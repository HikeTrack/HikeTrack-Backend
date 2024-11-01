package com.hiketrackbackend.hiketrackbackend.service.impl;

import com.hiketrackbackend.hiketrackbackend.dto.rating.RatingRequestDto;
import com.hiketrackbackend.hiketrackbackend.dto.rating.RatingRespondDto;
import com.hiketrackbackend.hiketrackbackend.exception.EntityNotFoundException;
import com.hiketrackbackend.hiketrackbackend.mapper.RatingMapper;
import com.hiketrackbackend.hiketrackbackend.model.tour.Rating;
import com.hiketrackbackend.hiketrackbackend.model.tour.Tour;
import com.hiketrackbackend.hiketrackbackend.model.user.User;
import com.hiketrackbackend.hiketrackbackend.repository.RatingRepository;
import com.hiketrackbackend.hiketrackbackend.repository.UserRepository;
import com.hiketrackbackend.hiketrackbackend.repository.tour.TourRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RatingServiceImplTest {

    @InjectMocks
    private RatingServiceImpl ratingService;

    @Mock
    private RatingRepository ratingRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TourRepository tourRepository;

    @Mock
    private RatingMapper ratingMapper;

    private RatingRequestDto ratingRequestDto;
    private Rating rating;
    private User user;
    private Tour tour;

    @BeforeEach
    public void setUp() {
        ratingRequestDto = new RatingRequestDto();
        ratingRequestDto.setRating(5L);

        user = new User();
        user.setId(1L);

        tour = new Tour();
        tour.setId(1L);

        rating = new Rating();
        rating.setId(1L);
        rating.setRating(5L);
        rating.setUser(user);
        rating.setTour(tour);
    }

    @Test
    public void testUpdateRatingWhenRatingDoesNotExistThenCreateRating() {
        // Arrange
        when(ratingRepository.existsByUserIdAndTourId(any(Long.class), any(Long.class))).thenReturn(false);
        when(ratingMapper.toEntity(any(RatingRequestDto.class))).thenReturn(rating);
        when(userRepository.findById(any(Long.class))).thenReturn(Optional.of(user));
        when(tourRepository.findById(any(Long.class))).thenReturn(Optional.of(tour));
        when(ratingRepository.save(any(Rating.class))).thenReturn(rating);
        RatingRespondDto ratingRespondDto = new RatingRespondDto();
        ratingRespondDto.setRating(5L);
        when(ratingMapper.toDto(any(Rating.class))).thenReturn(ratingRespondDto);

        // Act
        RatingRespondDto result = ratingService.updateRating(ratingRequestDto, 1L, 1L);

        // Assert
        verify(ratingRepository, times(1)).existsByUserIdAndTourId(1L, 1L);
        verify(ratingMapper, times(1)).toEntity(ratingRequestDto);
        verify(userRepository, times(1)).findById(1L);
        verify(tourRepository, times(1)).findById(1L);
        verify(ratingRepository, times(2)).save(rating);
        verify(ratingMapper, times(1)).toDto(rating);

        // Additional assertions
        assertEquals(5L, result.getRating());
    }



    @Test
    public void testUpdateRatingWhenTourDoesNotExistThenThrowException() {
        // Arrange
        when(ratingRepository.existsByUserIdAndTourId(any(Long.class), any(Long.class))).thenReturn(false);
        when(userRepository.findById(any(Long.class))).thenReturn(Optional.of(user));
        when(tourRepository.findById(any(Long.class))).thenReturn(Optional.empty());

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            ratingService.updateRating(ratingRequestDto, 1L, 1L);
        });

        assertEquals("Tour with id 1 not found", exception.getMessage());
        verify(userRepository, times(1)).findById(1L);
        verify(tourRepository, times(1)).findById(1L);
        verify(ratingRepository, never()).save(any(Rating.class));
        verify(ratingMapper, never()).toDto(any(Rating.class));
    }
}