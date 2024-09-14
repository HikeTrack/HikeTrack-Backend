package com.hiketrackbackend.hiketrackbackend.service.impl;

import com.hiketrackbackend.hiketrackbackend.dto.tour.TourRequestDto;
import com.hiketrackbackend.hiketrackbackend.dto.tour.TourRespondDto;
import com.hiketrackbackend.hiketrackbackend.dto.tour.TourSearchParameters;
import com.hiketrackbackend.hiketrackbackend.exception.EntityNotFoundException;
import com.hiketrackbackend.hiketrackbackend.mapper.TourMapper;
import com.hiketrackbackend.hiketrackbackend.model.Country;
import com.hiketrackbackend.hiketrackbackend.model.Tour;
import com.hiketrackbackend.hiketrackbackend.repository.country.CountryRepository;
import com.hiketrackbackend.hiketrackbackend.repository.tour.TourRepository;
import com.hiketrackbackend.hiketrackbackend.repository.tour.TourSpecificationBuilder;
import com.hiketrackbackend.hiketrackbackend.service.TourService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TourServiceImpl implements TourService {
    private final TourRepository tourRepository;
    private final TourMapper tourMapper;
    private final CountryRepository countryRepository;
    private final TourSpecificationBuilder tourSpecificationBuilder;

    @Override
    public TourRespondDto createTour(TourRequestDto requestDto) {
        Tour tour = tourMapper.toEntity(requestDto);
        Country country = findCountry(requestDto.getCountryId());
        tour.setCountry(country);
        return tourMapper.toDto(tourRepository.save(tour));
    }

    @Override
    public void deleteById(Long id) {
        findTour(id);
        tourRepository.deleteById(id);
    }

    @Override
    public List<TourRespondDto> getAll(Pageable pageable) {
        return tourRepository.findAll(pageable)
                .stream()
                .map(tourMapper::toDto)
                .toList();
    }

    @Override
    public TourRespondDto getById(Long id) {
        return tourMapper.toDto(findTour(id));
    }

    @Override
    public List<TourRespondDto> search(TourSearchParameters params, Pageable pageable) {
        Specification<Tour> tourSpecification = tourSpecificationBuilder.build(params);
        return tourRepository.findAll(tourSpecification, pageable)
                .stream()
                .map(tourMapper::toDto)
                .toList();
    }

    private Country findCountry(Long id) {
        return countryRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Country not found with id: " + id)
        );
    }

    private Tour findTour(Long id) {
       return tourRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Tour is not found with id: " + id)
        );
    }
}
