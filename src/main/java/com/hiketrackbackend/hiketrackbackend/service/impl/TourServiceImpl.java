package com.hiketrackbackend.hiketrackbackend.service.impl;

import com.hiketrackbackend.hiketrackbackend.dto.tour.TourRequestDto;
import com.hiketrackbackend.hiketrackbackend.dto.tour.TourRespondDto;
import com.hiketrackbackend.hiketrackbackend.exception.EntityNotFoundException;
import com.hiketrackbackend.hiketrackbackend.mapper.TourMapper;
import com.hiketrackbackend.hiketrackbackend.model.Tour;
import com.hiketrackbackend.hiketrackbackend.repository.tour.TourRepository;
import com.hiketrackbackend.hiketrackbackend.service.TourService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TourServiceImpl implements TourService {
    private final TourRepository tourRepository;
    private final TourMapper tourMapper;

    @Override
    public TourRespondDto createTour(TourRequestDto requestDto) {
        Tour tour = tourMapper.toEntity(requestDto);
        return tourMapper.toDto(tourRepository.save(tour));
    }

    @Override
    public void deleteById(Long id) {
        tourRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Tour is not found with id: " + id)
        );
        tourRepository.deleteById(id);
    }
}
