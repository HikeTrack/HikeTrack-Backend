package com.hiketrackbackend.hiketrackbackend.service.impl;

import com.hiketrackbackend.hiketrackbackend.dto.reviews.ReviewsRespondDto;
import com.hiketrackbackend.hiketrackbackend.dto.tour.TourRequestDto;
import com.hiketrackbackend.hiketrackbackend.dto.tour.TourRespondDto;
import com.hiketrackbackend.hiketrackbackend.dto.tour.TourRespondWithoutDetailsAndReviews;
import com.hiketrackbackend.hiketrackbackend.dto.tour.TourRespondWithoutReviews;
import com.hiketrackbackend.hiketrackbackend.dto.tour.TourSearchParameters;
import com.hiketrackbackend.hiketrackbackend.exception.EntityNotFoundException;
import com.hiketrackbackend.hiketrackbackend.mapper.ReviewMapper;
import com.hiketrackbackend.hiketrackbackend.mapper.TourMapper;
import com.hiketrackbackend.hiketrackbackend.model.Review;
import com.hiketrackbackend.hiketrackbackend.model.User;
import com.hiketrackbackend.hiketrackbackend.model.country.Country;
import com.hiketrackbackend.hiketrackbackend.model.tour.Tour;
import com.hiketrackbackend.hiketrackbackend.repository.ReviewRepository;
import com.hiketrackbackend.hiketrackbackend.repository.country.CountryRepository;
import com.hiketrackbackend.hiketrackbackend.repository.tour.TourRepository;
import com.hiketrackbackend.hiketrackbackend.repository.tour.TourSpecificationBuilder;
import com.hiketrackbackend.hiketrackbackend.service.TourService;
import jakarta.persistence.EntityExistsException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TourServiceImpl implements TourService {
    private final TourRepository tourRepository;
    private final TourMapper tourMapper;
    private final CountryRepository countryRepository;
    private final TourSpecificationBuilder tourSpecificationBuilder;
    private final ReviewRepository reviewRepository;
    private final ReviewMapper reviewMapper;

    @Override
    @Transactional
    public TourRespondWithoutReviews createTour(TourRequestDto requestDto, User user) {
        isExistTour(requestDto.getName(), user.getId());
        Tour tour = tourMapper.toEntity(requestDto);
        tour.setUser(user);
        Country country = findCountry(requestDto.getCountryId());
        tour.setCountry(country);
        return tourMapper.toDtoWithoutReviews(tourRepository.save(tour));
    }

    @Override
    @Transactional
    public TourRespondWithoutReviews updateTour(TourRequestDto requestDto, Long tourId) {
        Tour tour = findTour(tourId);
        tourMapper.updateEntityFromDto(requestDto, tour);
        return tourMapper.toDtoWithoutReviews(tourRepository.save(tour));
    }

    @Override
    public List<TourRespondWithoutDetailsAndReviews> getAll(Pageable pageable) {
        return tourRepository.findAll(pageable)
                .stream()
                .map(tourMapper::toDtoWithoutDetailsAndReviews)
                .toList();
    }

    /*
        Transactional annotation is used to skip lazy init exception,
        so we can get reviews the way we needed
     */
    @Override
    @Transactional
    public TourRespondDto getById(Long id, int page, int size) {
        Tour tour = findTour(id);
        TourRespondDto respondDto = tourMapper.toDto(tour);
        Pageable pageable = PageRequest.of(page, size, Sort.by("dateCreated").descending());
        Page<Review> reviewPage = reviewRepository.findByTourId(id, pageable);
        List<ReviewsRespondDto> dtoList = reviewPage.getContent()
                .stream()
                .map(reviewMapper::toDto)
                .collect(Collectors.toList());
        respondDto.setReviews(dtoList);
        respondDto.setCurrentReviewPage(reviewPage.getNumber());
        respondDto.setTotalReviewPages(reviewPage.getTotalPages());
        respondDto.setTotalReviewElements(reviewPage.getTotalElements());
        return respondDto;
    }

    @Override
    public List<TourRespondWithoutReviews> search(TourSearchParameters params, Pageable pageable) {
        Specification<Tour> tourSpecification = tourSpecificationBuilder.build(params);
        return tourRepository.findAll(tourSpecification, pageable)
                .stream()
                .map(tourMapper::toDtoWithoutReviews)
                .toList();
    }

    @Override
    public List<TourRespondWithoutReviews> getByRating() {
        return tourRepository
                .findTop7ByRatingGreaterThanOrderByRatingDesc(0)
                .stream()
                .map(tourMapper::toDtoWithoutReviews)
                .toList();
    }

    @Override
    public void deleteById(Long id) {
        findTour(id);
        tourRepository.deleteById(id);
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

    private void isExistTour(String tourName, Long guideId) {
        boolean exist = tourRepository.existsTourByUserIdAndName(guideId, tourName);
        if (exist) {
            throw new EntityExistsException("Tour already exists fot this guide with id: " +
                    guideId + " and with tour name: " + tourName);
        }
    }
}
