package com.hiketrackbackend.hiketrackbackend.service;

import com.hiketrackbackend.hiketrackbackend.dto.tour.TourRequestDto;
import com.hiketrackbackend.hiketrackbackend.dto.tour.TourRespondDto;

public interface TourService {
    TourRespondDto createTour(TourRequestDto requestDto);

    void deleteById(Long id);
}
