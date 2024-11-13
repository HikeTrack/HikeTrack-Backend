package com.hiketrackbackend.hiketrackbackend.service.impl;

import com.hiketrackbackend.hiketrackbackend.dto.reviews.ReviewsRespondDto;
import com.hiketrackbackend.hiketrackbackend.dto.tour.TourRequestDto;
import com.hiketrackbackend.hiketrackbackend.dto.tour.TourRespondDto;
import com.hiketrackbackend.hiketrackbackend.dto.tour.TourRespondWithoutDetailsAndReviews;
import com.hiketrackbackend.hiketrackbackend.dto.tour.TourRespondWithoutReviews;
import com.hiketrackbackend.hiketrackbackend.dto.tour.TourSearchParameters;
import com.hiketrackbackend.hiketrackbackend.dto.tour.TourUpdateRequestDto;
import com.hiketrackbackend.hiketrackbackend.exception.EntityNotFoundException;
import com.hiketrackbackend.hiketrackbackend.exception.FileIsEmptyException;
import com.hiketrackbackend.hiketrackbackend.mapper.ReviewMapper;
import com.hiketrackbackend.hiketrackbackend.mapper.TourMapper;
import com.hiketrackbackend.hiketrackbackend.model.country.Country;
import com.hiketrackbackend.hiketrackbackend.model.tour.Review;
import com.hiketrackbackend.hiketrackbackend.model.tour.Tour;
import com.hiketrackbackend.hiketrackbackend.model.tour.details.TourDetails;
import com.hiketrackbackend.hiketrackbackend.model.tour.details.TourDetailsFile;
import com.hiketrackbackend.hiketrackbackend.model.user.User;
import com.hiketrackbackend.hiketrackbackend.repository.ReviewRepository;
import com.hiketrackbackend.hiketrackbackend.repository.TourDetailsFileRepository;
import com.hiketrackbackend.hiketrackbackend.repository.country.CountryRepository;
import com.hiketrackbackend.hiketrackbackend.repository.tour.TourRepository;
import com.hiketrackbackend.hiketrackbackend.repository.tour.TourSpecificationBuilder;
import com.hiketrackbackend.hiketrackbackend.service.TourDetailsService;
import com.hiketrackbackend.hiketrackbackend.service.TourService;
import com.hiketrackbackend.hiketrackbackend.service.files.FileStorageService;
import jakarta.persistence.EntityExistsException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class TourServiceImpl implements TourService {
    private static final String FOLDER_NAME = "tours";
    private static final int FIRST_ELEMENT = 0;
    private static final int AMOUNT_OF_MOST_RATED_TOURS = 7;
    private final TourRepository tourRepository;
    private final TourMapper tourMapper;
    private final CountryRepository countryRepository;
    private final TourSpecificationBuilder tourSpecificationBuilder;
    private final ReviewRepository reviewRepository;
    private final ReviewMapper reviewMapper;
    private final FileStorageService s3Service;
    private final TourDetailsFileRepository tourDetailsFileRepository;
    private final TourDetailsService tourDetailsService;

    @Override
    @Transactional
    public TourRespondWithoutReviews createTour(
            TourRequestDto requestDto,
            User user,
            MultipartFile mainPhoto,
            List<MultipartFile> additionalPhotos
    ) {
        if (mainPhoto.isEmpty()) {
            throw new FileIsEmptyException("Tour main photo is mandatory. Please upload a file.");
        }
        isExistTourByName(requestDto.getName(), user.getId());
        Country country = findCountry(requestDto.getCountryId());
        Tour tour = tourMapper.toEntity(requestDto);
        tour.setUser(user);
        List<String> mainPhotoUrl = s3Service.uploadFileToS3(
                FOLDER_NAME, Collections.singletonList(mainPhoto));
        tour.setMainPhoto(mainPhotoUrl.get(FIRST_ELEMENT));

        tour.setCountry(country);

        TourDetails tourDetails = tourDetailsService.createTourDetails(
                tour, requestDto.getDetailsRequestDto(), additionalPhotos
        );
        tour.setTourDetails(tourDetails);

        return tourMapper.toDtoWithoutReviews(tourRepository.save(tour));
    }

    @Override
    @Transactional
    public TourRespondWithoutReviews updateTour(
            TourUpdateRequestDto requestDto,
            Long userId,
            Long tourId
    ) {
        Tour tour = findTourByIdAndUserId(tourId, userId);
        tourMapper.updateEntityFromDto(requestDto, tour);
        Country country = findCountry(requestDto.getCountryId());
        tour.setCountry(country);
        return tourMapper.toDtoWithoutReviews(tourRepository.save(tour));
    }

    @Override
    @Transactional
    public TourRespondWithoutReviews updateTourPhoto(
            MultipartFile mainPhoto,
            Long userId,
            Long tourId
    ) {
        Tour tour = findTourByIdAndUserId(tourId, userId);
        if (mainPhoto != null) {
            s3Service.deleteFileFromS3(tour.getMainPhoto());
        }

        List<String> newMainPhotoUrl = s3Service.uploadFileToS3(
                FOLDER_NAME, Collections.singletonList(mainPhoto));
        tour.setMainPhoto(newMainPhotoUrl.get(FIRST_ELEMENT));

        return tourMapper.toDtoWithoutReviews(tourRepository.save(tour));
    }

    @Override
    public List<TourRespondWithoutDetailsAndReviews> getAll(Pageable pageable) {
        return tourRepository.findAll(pageable)
                .stream()
                .map(tourMapper::toDtoWithoutDetailsAndReviews)
                .toList();
    }

    @Override
    public List<TourRespondWithoutDetailsAndReviews> getAllToursMadeByGuide(
            Long userId,
            Pageable pageable
    ) {
        return tourRepository.findAllTourByUserId(userId, pageable)
                .stream()
                .map(tourMapper::toDtoWithoutDetailsAndReviews)
                .toList();
    }

    @Override
    public TourRespondDto getById(Long id, int page, int size) {
        Tour tour = findTourByIdWithDetailsAndAdditionalPhotos(id);
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
    public List<TourRespondWithoutDetailsAndReviews> search(
            TourSearchParameters params,
            Pageable pageable
    ) {
        Specification<Tour> tourSpecification = tourSpecificationBuilder.build(params);
        return tourRepository.findAll(tourSpecification, pageable)
                .stream()
                .map(tourMapper::toDtoWithoutDetailsAndReviews)
                .toList();
    }

    @Override
    @Transactional
    public List<TourRespondWithoutDetailsAndReviews> getByRating() {
        return tourRepository
                .findTopToursWithHighestRatings(Pageable.ofSize(AMOUNT_OF_MOST_RATED_TOURS))
                .stream()
                .map(tourMapper::toDtoWithoutDetailsAndReviews)
                .toList();
    }

    @Override
    @Transactional
    public void deleteTourByIdAndUserId(Long tourId, Long userId) {
        boolean exist = isExistTourById(tourId, userId);
        if (!exist) {
            throw new EntityNotFoundException("Tour with id " + tourId + " not found");
        }
        tourRepository.deleteById(tourId);
    }

    @Override
    @Transactional
    public void deleteTourDetailsPhotoById(Long id) {
        TourDetailsFile photo = tourDetailsFileRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Tour details photo not found with id: " + id)
        );
        tourDetailsFileRepository.delete(photo);
    }

    private boolean isExistTourById(Long tourId, Long userId) {
        return tourRepository.existsByIdAndUserId(tourId, userId);
    }

    private Country findCountry(Long id) {
        return countryRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Country not found with id: " + id)
        );
    }

    private Tour findTourByIdWithDetailsAndAdditionalPhotos(Long id) {
        return tourRepository.findTourById(id).orElseThrow(
                () -> new EntityNotFoundException("Tour details photo not found with id: " + id)
        );
    }

    private void isExistTourByName(String tourName, Long guideId) {
        boolean exist = tourRepository.existsTourByUserIdAndName(guideId, tourName);
        if (exist) {
            throw new EntityExistsException("Tour already exists fot this guide with id: "
                    + guideId + " and with tour name: " + tourName);
        }
    }

    private Tour findTourByIdAndUserId(Long id, Long userId) {
        return tourRepository.findTourByIdAndUserId(id, userId).orElseThrow(
                () -> new EntityNotFoundException(
                        "Tour not found with id: " + id + " and user id: " + userId)
        );
    }
}
