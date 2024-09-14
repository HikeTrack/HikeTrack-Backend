package com.hiketrackbackend.hiketrackbackend.service;

import com.hiketrackbackend.hiketrackbackend.dto.tour.TourRequestDto;
import com.hiketrackbackend.hiketrackbackend.dto.tour.TourRespondDto;
import com.hiketrackbackend.hiketrackbackend.dto.tour.TourSearchParameters;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TourService {
    TourRespondDto createTour(TourRequestDto requestDto);

    void deleteById(Long id);

    List<TourRespondDto> getAll(Pageable pageable);

    TourRespondDto getById(Long id);

    List<TourRespondDto> search(TourSearchParameters params, Pageable pageable);
}
