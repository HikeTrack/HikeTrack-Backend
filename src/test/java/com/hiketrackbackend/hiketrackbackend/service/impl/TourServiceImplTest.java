
package com.hiketrackbackend.hiketrackbackend.service.impl;

import com.hiketrackbackend.hiketrackbackend.dto.details.DetailsRequestDto;
import com.hiketrackbackend.hiketrackbackend.dto.reviews.ReviewsRespondDto;
import com.hiketrackbackend.hiketrackbackend.dto.tour.*;
import com.hiketrackbackend.hiketrackbackend.exception.EntityNotFoundException;
import com.hiketrackbackend.hiketrackbackend.mapper.ReviewMapper;
import com.hiketrackbackend.hiketrackbackend.mapper.TourMapper;
import com.hiketrackbackend.hiketrackbackend.model.country.Country;
import com.hiketrackbackend.hiketrackbackend.model.tour.Difficulty;
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
import com.hiketrackbackend.hiketrackbackend.service.files.FileStorageService;
import jakarta.persistence.EntityExistsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TourServiceImplTest {

    @Mock
    private TourRepository tourRepository;

    @Mock
    private TourMapper tourMapper;

    @Mock
    private CountryRepository countryRepository;

    @Mock
    private FileStorageService s3Service;

    @Mock
    private TourDetailsService tourDetailsService;

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private ReviewMapper reviewMapper;

    @Mock
    private TourSpecificationBuilder tourSpecificationBuilder;

    @Mock
    private TourDetailsFileRepository tourDetailsFileRepository;

    @InjectMocks
    private TourServiceImpl tourService;

    private TourRequestDto tourRequestDto;
    private TourUpdateRequestDto tourUpdateRequestDto;
    private User user;
    private MultipartFile mainPhoto;
    private List<MultipartFile> additionalPhotos;
    private Tour tour;
    private Country country;

    @BeforeEach
    public void setUp() {
        tourRequestDto = new TourRequestDto();
        tourRequestDto.setName("Test Tour");
        tourRequestDto.setLength(10);
        tourRequestDto.setPrice(BigDecimal.valueOf(100));
        tourRequestDto.setDate(ZonedDateTime.now());
        tourRequestDto.setDifficulty(Difficulty.Medium);
        tourRequestDto.setCountryId(1L);
        tourRequestDto.setDetailsRequestDto(new DetailsRequestDto());

        tourUpdateRequestDto = new TourUpdateRequestDto();
        tourUpdateRequestDto.setName("Updated Test Tour");
        tourUpdateRequestDto.setLength(15);
        tourUpdateRequestDto.setPrice(BigDecimal.valueOf(150));
        tourUpdateRequestDto.setDate(ZonedDateTime.now().plusDays(1));
        tourUpdateRequestDto.setDifficulty(Difficulty.Hard);
        tourUpdateRequestDto.setCountryId(2L);
        tourUpdateRequestDto.setDetailsRequestDto(new DetailsRequestDto());

        user = new User();
        user.setId(1L);

        mainPhoto = mock(MultipartFile.class);
        additionalPhotos = Collections.emptyList();

        tour = new Tour();
        tour.setId(1L);
        tour.setName("Test Tour");

        country = new Country();
        country.setId(1L);
    }

    @Test
    public void testCreateTourWhenMainPhotoIsEmptyThenThrowRuntimeException() {
        // Arrange
        when(mainPhoto.isEmpty()).thenReturn(true);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            tourService.createTour(tourRequestDto, user, mainPhoto, additionalPhotos);
        });

        assertThat(exception.getMessage()).isEqualTo("Tour main photo is mandatory. Please upload a file.");
    }

    @Test
    public void testCreateTourWhenTourAlreadyExistsThenThrowEntityExistsException() {
        // Arrange
        when(mainPhoto.isEmpty()).thenReturn(false);
        when(tourRepository.existsTourByUserIdAndName(user.getId(), tourRequestDto.getName())).thenReturn(true);

        // Act & Assert
        EntityExistsException exception = assertThrows(EntityExistsException.class, () -> {
            tourService.createTour(tourRequestDto, user, mainPhoto, additionalPhotos);
        });

        assertThat(exception.getMessage()).isEqualTo("Tour already exists fot this guide with id: " + user.getId() + " and with tour name: " + tourRequestDto.getName());
    }

    @Test
    public void testCreateTourWhenCountryDoesNotExistThenThrowEntityNotFoundException() {
        // Arrange
        when(mainPhoto.isEmpty()).thenReturn(false);
        when(tourRepository.existsTourByUserIdAndName(user.getId(), tourRequestDto.getName())).thenReturn(false);
        when(countryRepository.findById(tourRequestDto.getCountryId())).thenReturn(Optional.empty());

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            tourService.createTour(tourRequestDto, user, mainPhoto, additionalPhotos);
        });

        assertThat(exception.getMessage()).isEqualTo("Country not found with id: " + tourRequestDto.getCountryId());
    }

    @Test
    public void testCreateTourWhenTourIsSuccessfullyCreatedThenReturnTourRespondWithoutReviews() {
        // Arrange
        when(mainPhoto.isEmpty()).thenReturn(false);
        when(tourRepository.existsTourByUserIdAndName(user.getId(), tourRequestDto.getName())).thenReturn(false);
        when(countryRepository.findById(tourRequestDto.getCountryId())).thenReturn(Optional.of(country));
        when(tourMapper.toEntity(tourRequestDto)).thenReturn(tour);
        when(s3Service.uploadFileToS3(anyString(), anyList())).thenReturn(Collections.singletonList("mainPhotoUrl"));
        when(tourDetailsService.createTourDetails(any(Tour.class), any(DetailsRequestDto.class), anyList())).thenReturn(new TourDetails());
        when(tourRepository.save(any(Tour.class))).thenReturn(tour);
        when(tourMapper.toDtoWithoutReviews(any(Tour.class))).thenReturn(new TourRespondWithoutReviews());

        // Act
        TourRespondWithoutReviews result = tourService.createTour(tourRequestDto, user, mainPhoto, additionalPhotos);

        // Assert
        assertThat(result).isNotNull();
        verify(tourRepository, times(1)).save(any(Tour.class));
    }

    @Test
    public void testUpdateTourWhenTourIsSuccessfullyUpdatedThenReturnUpdatedTour() {
        // Arrange
        when(tourRepository.findTourByIdAndUserId(tour.getId(), user.getId())).thenReturn(Optional.of(tour));
        when(countryRepository.findById(tourUpdateRequestDto.getCountryId())).thenReturn(Optional.of(country));
        when(tourRepository.save(any(Tour.class))).thenReturn(tour);
        when(tourMapper.toDtoWithoutReviews(any(Tour.class))).thenReturn(new TourRespondWithoutReviews());

        // Act
        TourRespondWithoutReviews result = tourService.updateTour(tourUpdateRequestDto, user.getId(), tour.getId());

        // Assert
        assertThat(result).isNotNull();
        verify(tourRepository, times(1)).save(any(Tour.class));
    }

    @Test
    public void testUpdateTourWhenTourIsNotFoundThenThrowEntityNotFoundException() {
        // Arrange
        when(tourRepository.findTourByIdAndUserId(tour.getId(), user.getId())).thenReturn(Optional.empty());

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            tourService.updateTour(tourUpdateRequestDto, user.getId(), tour.getId());
        });

        assertThat(exception.getMessage()).isEqualTo("Tour not found with id: " + tour.getId() + " and user id: " + user.getId());
    }

    @Test
    public void testUpdateTourWhenCountryIsNotFoundThenThrowEntityNotFoundException() {
        // Arrange
        when(tourRepository.findTourByIdAndUserId(tour.getId(), user.getId())).thenReturn(Optional.of(tour));
        when(countryRepository.findById(tourUpdateRequestDto.getCountryId())).thenReturn(Optional.empty());

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            tourService.updateTour(tourUpdateRequestDto, user.getId(), tour.getId());
        });

        assertThat(exception.getMessage()).isEqualTo("Country not found with id: " + tourUpdateRequestDto.getCountryId());
    }

    @Test
    public void testUpdateTourPhotoWhenMainPhotoIsNull() {
        // Arrange
        when(tourRepository.findTourByIdAndUserId(tour.getId(), user.getId())).thenReturn(Optional.of(tour));
        when(s3Service.uploadFileToS3(anyString(), anyList())).thenReturn(Collections.singletonList("newMainPhotoUrl"));
        when(tourRepository.save(any(Tour.class))).thenReturn(tour);
        when(tourMapper.toDtoWithoutReviews(any(Tour.class))).thenReturn(new TourRespondWithoutReviews());

        // Act
        TourRespondWithoutReviews result = tourService.updateTourPhoto(null, user.getId(), tour.getId());

        // Assert
        assertThat(result).isNotNull();
        verify(s3Service, never()).deleteFileFromS3(anyString());
        verify(s3Service, times(1)).uploadFileToS3(anyString(), anyList());
        verify(tourRepository, times(1)).save(any(Tour.class));
    }

    @Test
    public void testUpdateTourPhotoWhenMainPhotoIsNotNull() {
        // Arrange
        tour.setMainPhoto("oldMainPhotoUrl"); // Установим значение mainPhoto у объекта tour
        when(tourRepository.findTourByIdAndUserId(tour.getId(), user.getId())).thenReturn(Optional.of(tour));
        when(s3Service.uploadFileToS3(anyString(), anyList())).thenReturn(Collections.singletonList("newMainPhotoUrl"));
        when(tourRepository.save(any(Tour.class))).thenReturn(tour);
        when(tourMapper.toDtoWithoutReviews(any(Tour.class))).thenReturn(new TourRespondWithoutReviews());

        // Act
        TourRespondWithoutReviews result = tourService.updateTourPhoto(mainPhoto, user.getId(), tour.getId());

        // Assert
        assertThat(result).isNotNull();
        verify(s3Service, times(1)).deleteFileFromS3("oldMainPhotoUrl");
        verify(s3Service, times(1)).uploadFileToS3(anyString(), anyList());
        verify(tourRepository, times(1)).save(any(Tour.class));
    }

    @Test
    public void testGetAllWhenRepositoryReturnsListOfTours() {
        // Arrange
        List<Tour> tours = List.of(tour);
        Pageable pageable = PageRequest.of(0, 10);
        when(tourRepository.findAll(pageable)).thenReturn(new PageImpl<>(tours));
        when(tourMapper.toDtoWithoutDetailsAndReviews(any(Tour.class))).thenReturn(new TourRespondWithoutDetailsAndReviews());

        // Act
        List<TourRespondWithoutDetailsAndReviews> result = tourService.getAll(pageable);

        // Assert
        assertThat(result).isNotEmpty();
        assertThat(result.size()).isEqualTo(tours.size());
        verify(tourRepository, times(1)).findAll(pageable);
    }

    @Test
    public void testGetAllWhenRepositoryReturnsEmptyList() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        when(tourRepository.findAll(pageable)).thenReturn(new PageImpl<>(Collections.emptyList()));

        // Act
        List<TourRespondWithoutDetailsAndReviews> result = tourService.getAll(pageable);

        // Assert
        assertThat(result).isEmpty();
        verify(tourRepository, times(1)).findAll(pageable);
    }

    @Test
    public void testGetByIdWhenTourFoundThenReturnDto() {
        // Arrange
        Long tourId = 1L;
        int page = 0;
        int size = 10;
        TourRespondDto tourRespondDto = new TourRespondDto();
        List<Review> reviews = List.of(new Review());
        Page<Review> reviewPage = new PageImpl<>(reviews, PageRequest.of(page, size, Sort.by("dateCreated").descending()), reviews.size());

        when(tourRepository.findTourById(tourId)).thenReturn(Optional.of(tour));
        when(tourMapper.toDto(tour)).thenReturn(tourRespondDto);
        when(reviewRepository.findByTourId(tourId, PageRequest.of(page, size, Sort.by("dateCreated").descending()))).thenReturn(reviewPage);
        when(reviewMapper.toDto(any(Review.class))).thenReturn(new ReviewsRespondDto());

        // Act
        TourRespondDto result = tourService.getById(tourId, page, size);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getReviews()).isNotEmpty();
        assertThat(result.getCurrentReviewPage()).isEqualTo(reviewPage.getNumber());
        assertThat(result.getTotalReviewPages()).isEqualTo(reviewPage.getTotalPages());
        assertThat(result.getTotalReviewElements()).isEqualTo(reviewPage.getTotalElements());
        verify(tourRepository, times(1)).findTourById(tourId);
        verify(reviewRepository, times(1)).findByTourId(tourId, PageRequest.of(page, size, Sort.by("dateCreated").descending()));
    }

    @Test
    public void testGetByIdWhenTourNotFoundThenThrowException() {
        // Arrange
        Long tourId = 1L;
        int page = 0;
        int size = 10;

        when(tourRepository.findTourById(tourId)).thenReturn(Optional.empty());

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            tourService.getById(tourId, page, size);
        });

        assertThat(exception.getMessage()).isEqualTo("Tour details photo not found with id: " + tourId);
        verify(tourRepository, times(1)).findTourById(tourId);
    }

    @Test
    public void testSearchWhenRepositoryReturnsListOfToursThenReturnListOfTours() {
        // Arrange
        TourSearchParameters params = new TourSearchParameters(
                new String[]{"routeType1", "routeType2"},
                new String[]{"difficulty1", "difficulty2"},
                new String[]{"length1", "length2"},
                new String[]{"activity1", "activity2"},
                new String[]{"date1", "date2"},
                new String[]{"duration1", "duration2"},
                new String[]{"price1", "price2"},
                new String[]{"country1", "country2"}
        );
        Pageable pageable = PageRequest.of(0, 10);
        List<Tour> tours = List.of(tour);
        Specification<Tour> specification = mock(Specification.class);

        when(tourSpecificationBuilder.build(params)).thenReturn(specification);
        when(tourRepository.findAll(specification, pageable)).thenReturn(new PageImpl<>(tours));
        when(tourMapper.toDtoWithoutDetailsAndReviews(any(Tour.class))).thenReturn(new TourRespondWithoutDetailsAndReviews());

        // Act
        List<TourRespondWithoutDetailsAndReviews> result = tourService.search(params, pageable);

        // Assert
        assertThat(result).isNotEmpty();
        assertThat(result.size()).isEqualTo(tours.size());
        verify(tourRepository, times(1)).findAll(specification, pageable);
    }

    @Test
    public void testSearchWhenRepositoryReturnsEmptyListThenReturnEmptyList() {
        // Arrange
        TourSearchParameters params = new TourSearchParameters(
                new String[]{"routeType1", "routeType2"},
                new String[]{"difficulty1", "difficulty2"},
                new String[]{"length1", "length2"},
                new String[]{"activity1", "activity2"},
                new String[]{"date1", "date2"},
                new String[]{"duration1", "duration2"},
                new String[]{"price1", "price2"},
                new String[]{"country1", "country2"}
        );
        Pageable pageable = PageRequest.of(0, 10);
        Specification<Tour> specification = mock(Specification.class);

        when(tourSpecificationBuilder.build(params)).thenReturn(specification);
        when(tourRepository.findAll(specification, pageable)).thenReturn(new PageImpl<>(Collections.emptyList()));

        // Act
        List<TourRespondWithoutDetailsAndReviews> result = tourService.search(params, pageable);

        // Assert
        assertThat(result).isEmpty();
        verify(tourRepository, times(1)).findAll(specification, pageable);
    }

    @Test
    public void testGetByRatingWhenRepositoryReturnsListOfToursThenReturnListOfTours() {
        // Arrange
        List<Tour> tours = List.of(tour);
        when(tourRepository.findTopToursWithHighestRatings(Pageable.ofSize(7))).thenReturn(tours);
        when(tourMapper.toDtoWithoutDetailsAndReviews(any(Tour.class))).thenReturn(new TourRespondWithoutDetailsAndReviews());

        // Act
        List<TourRespondWithoutDetailsAndReviews> result = tourService.getByRating();

        // Assert
        assertThat(result).isNotEmpty();
        assertThat(result.size()).isEqualTo(tours.size());
        verify(tourRepository, times(1)).findTopToursWithHighestRatings(Pageable.ofSize(7));
    }

    @Test
    public void testGetByRatingWhenRepositoryReturnsEmptyListThenReturnEmptyList() {
        // Arrange
        when(tourRepository.findTopToursWithHighestRatings(Pageable.ofSize(7))).thenReturn(Collections.emptyList());

        // Act
        List<TourRespondWithoutDetailsAndReviews> result = tourService.getByRating();

        // Assert
        assertThat(result).isEmpty();
        verify(tourRepository, times(1)).findTopToursWithHighestRatings(Pageable.ofSize(7));
    }

    @Test
    public void testGetAllToursMadeByGuideWhenToursExistThenReturnTours() {
        // Arrange
        Long userId = 1L;
        Pageable pageable = PageRequest.of(0, 10);
        Tour tour = new Tour();
        tour.setId(1L);
        List<Tour> tours = List.of(tour);
        TourRespondWithoutDetailsAndReviews tourDto = new TourRespondWithoutDetailsAndReviews();
        tourDto.setId(1L);

        when(tourRepository.findAllTourByUserId(userId, pageable)).thenReturn(tours);
        when(tourMapper.toDtoWithoutDetailsAndReviews(tour)).thenReturn(tourDto);

        // Act
        List<TourRespondWithoutDetailsAndReviews> result = tourService.getAllToursMadeByGuide(userId, pageable);

        // Assert
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
    }

    @Test
    public void testGetAllToursMadeByGuideWhenNoToursExistThenReturnEmptyList() {
        // Arrange
        Long userId = 1L;
        Pageable pageable = PageRequest.of(0, 10);
        List<Tour> tours = Collections.emptyList();

        when(tourRepository.findAllTourByUserId(userId, pageable)).thenReturn(tours);

        // Act
        List<TourRespondWithoutDetailsAndReviews> result = tourService.getAllToursMadeByGuide(userId, pageable);

        // Assert
        assertEquals(0, result.size());
    }

    @Test
    public void testDeleteTourByIdAndUserIdWhenTourDoesNotExistThenDeleteTour() {
        // Arrange
        Long tourId = 1L;
        Long userId = 1L;

        when(tourRepository.existsByIdAndUserId(tourId, userId)).thenReturn(false);

        // Act
        tourService.deleteTourByIdAndUserId(tourId, userId);

        // Assert
        verify(tourRepository, times(1)).deleteById(tourId);
    }

    @Test
    public void testDeleteTourByIdAndUserIdWhenTourExistsThenThrowException() {
        // Arrange
        Long tourId = 1L;
        Long userId = 1L;

        when(tourRepository.existsByIdAndUserId(tourId, userId)).thenReturn(true);

        // Act & Assert
        assertThrows(EntityExistsException.class, () -> tourService.deleteTourByIdAndUserId(tourId, userId));
    }

    @Test
    public void testGetAllToursMadeByGuideWhenRepositoryReturnsToursThenReturnTours() {
        // Arrange
        Long userId = 1L;
        Pageable pageable = PageRequest.of(0, 10);
        Tour tour = new Tour();
        tour.setId(1L);
        List<Tour> tours = List.of(tour);
        TourRespondWithoutDetailsAndReviews tourDto = new TourRespondWithoutDetailsAndReviews();
        tourDto.setId(1L);

        when(tourRepository.findAllTourByUserId(userId, pageable)).thenReturn(tours);
        when(tourMapper.toDtoWithoutDetailsAndReviews(tour)).thenReturn(tourDto);

        // Act
        List<TourRespondWithoutDetailsAndReviews> result = tourService.getAllToursMadeByGuide(userId, pageable);

        // Assert
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
    }

    @Test
    public void testGetAllToursMadeByGuideWhenRepositoryReturnsEmptyListThenReturnEmptyList() {
        // Arrange
        Long userId = 1L;
        Pageable pageable = PageRequest.of(0, 10);
        List<Tour> tours = Collections.emptyList();

        when(tourRepository.findAllTourByUserId(userId, pageable)).thenReturn(tours);

        // Act
        List<TourRespondWithoutDetailsAndReviews> result = tourService.getAllToursMadeByGuide(userId, pageable);

        // Assert
        assertEquals(0, result.size());
    }

    @Test
    public void testDeleteTourDetailsPhotoByIdWhenPhotoFoundThenDeletePhoto() {
        // Arrange
        Long photoId = 1L;
        TourDetailsFile photo = new TourDetailsFile();
        photo.setId(photoId);

        when(tourDetailsFileRepository.findById(photoId)).thenReturn(Optional.of(photo));

        // Act
        tourService.deleteTourDetailsPhotoById(photoId);

        // Assert
        verify(tourDetailsFileRepository, times(1)).delete(photo);
    }

    @Test
    public void testDeleteTourDetailsPhotoByIdWhenPhotoNotFoundThenThrowEntityNotFoundException() {
        // Arrange
        Long photoId = 1L;

        when(tourDetailsFileRepository.findById(photoId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> tourService.deleteTourDetailsPhotoById(photoId));
    }
}