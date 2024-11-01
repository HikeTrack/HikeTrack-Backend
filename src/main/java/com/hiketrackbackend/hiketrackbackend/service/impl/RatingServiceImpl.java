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
import com.hiketrackbackend.hiketrackbackend.service.RatingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RatingServiceImpl implements RatingService {
    private final RatingRepository ratingRepository;
    private final UserRepository userRepository;
    private final TourRepository tourRepository;
    private final RatingMapper ratingMapper;

    @Override
    public RatingRespondDto updateRating(RatingRequestDto requestDto, Long userId, Long tourId) {
        boolean exist = isRatingExistWithUserIdAndTour(userId, tourId);
        if (!exist) {
            Rating rating = createRating(requestDto, userId, tourId);
            return ratingMapper.toDto(ratingRepository.save(rating));
        }
        Rating existedRating = getRatingByUserIdAndTourId(userId, tourId);
        ratingMapper.updateEntityFromDto(existedRating, requestDto);
        return ratingMapper.toDto(ratingRepository.save(existedRating));
    }

    private Rating createRating(RatingRequestDto requestDto, Long userId, Long tourId) {
        User user = findUserById(userId);
        Tour tour = findTourById(tourId);
        Rating rating = ratingMapper.toEntity(requestDto);
        rating.setUser(user);
        rating.setTour(tour);
        return ratingRepository.save(rating);
    }

    private Rating getRatingByUserIdAndTourId(Long userId, Long tourId) {
        return ratingRepository.findRatingByUser_IdAndTour_Id(userId, tourId).orElseThrow(
                () -> new EntityNotFoundException("Rating with user id " + userId
                        + " and tour id " + tourId + " not found")
        );
    }

    private Tour findTourById(Long id) {
        return tourRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Tour with id " + id + " not found")
        );
    }

    private User findUserById(Long id) {
        return userRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("User with id " + id + " not found")
        );
    }

    private boolean isRatingExistWithUserIdAndTour(Long userId, Long tourId) {
        return ratingRepository.existsByUserIdAndTourId(userId, tourId);
    }
}
