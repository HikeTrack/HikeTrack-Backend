package com.hiketrackbackend.hiketrackbackend.service;

import com.hiketrackbackend.hiketrackbackend.dto.rating.RatingRequestDto;
import com.hiketrackbackend.hiketrackbackend.dto.rating.RatingRespondDto;

public interface RatingService {
    RatingRespondDto updateRating(RatingRequestDto requestDto, Long userId, Long tourId);
}
