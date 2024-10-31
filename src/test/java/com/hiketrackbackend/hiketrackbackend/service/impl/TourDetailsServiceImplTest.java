package com.hiketrackbackend.hiketrackbackend.service.impl;

import com.hiketrackbackend.hiketrackbackend.dto.details.DetailsRequestDto;
import com.hiketrackbackend.hiketrackbackend.dto.details.DetailsRespondDto;
import com.hiketrackbackend.hiketrackbackend.exception.EntityNotFoundException;
import com.hiketrackbackend.hiketrackbackend.exception.MemoryLimitException;
import com.hiketrackbackend.hiketrackbackend.mapper.TourDetailsMapper;
import com.hiketrackbackend.hiketrackbackend.model.tour.Tour;
import com.hiketrackbackend.hiketrackbackend.model.tour.details.TourDetails;
import com.hiketrackbackend.hiketrackbackend.model.tour.details.TourDetailsFile;
import com.hiketrackbackend.hiketrackbackend.repository.tour.TourRepository;
import com.hiketrackbackend.hiketrackbackend.service.files.FileStorageService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TourDetailsServiceImplTest {

    @Mock
    private FileStorageService s3Service;

    @Mock
    private TourDetailsMapper tourDetailsMapper;

    @Mock
    private TourRepository tourRepository;

    @InjectMocks
    private TourDetailsServiceImpl tourDetailsService;

    @Test
    public void testCreateTourDetailsWhenPhotosExceedLimitThenThrowMemoryLimitException() {
        // Arrange
        Tour tour = new Tour();
        DetailsRequestDto requestDto = new DetailsRequestDto();
        List<MultipartFile> photos = mock(List.class);
        when(photos.size()).thenReturn(6); // Exceeding the limit

        // Act & Assert
        MemoryLimitException exception = assertThrows(MemoryLimitException.class, () -> {
            tourDetailsService.createTourDetails(tour, requestDto, photos);
        });

        assertEquals("Maximum 5 for uploading is 5", exception.getMessage());
    }

    @Test
    public void testCreateTourDetailsWhenPhotosWithinLimitThenReturnTourDetails() {
        // Arrange
        Tour tour = new Tour();
        DetailsRequestDto requestDto = new DetailsRequestDto();
        List<MultipartFile> photos = mock(List.class);
        when(photos.size()).thenReturn(3); // Within the limit

        TourDetails expectedTourDetails = new TourDetails();
        when(tourDetailsMapper.toEntity(requestDto)).thenReturn(expectedTourDetails);
        when(s3Service.uploadFileToS3(anyString(), eq(photos))).thenReturn(Collections.emptyList());

        // Act
        TourDetails actualTourDetails = tourDetailsService.createTourDetails(tour, requestDto, photos);

        // Assert
        assertNotNull(actualTourDetails);
        assertEquals(expectedTourDetails, actualTourDetails);
        verify(tourDetailsMapper, times(1)).toEntity(requestDto);
        verify(s3Service, times(1)).uploadFileToS3(anyString(), eq(photos));
    }

    @Test
    public void testUpdateTourDetailsPhotosWhenAdditionalPhotosExceedLimitThenThrowMemoryLimitException() {
        // Arrange
        Long userId = 1L;
        Long tourId = 1L;
        List<MultipartFile> additionalPhotos = mock(List.class);
        when(additionalPhotos.size()).thenReturn(6); // Exceeding the limit

        Tour tour = new Tour();
        TourDetails tourDetails = new TourDetails();
        List<TourDetailsFile> savedPhotos = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            savedPhotos.add(new TourDetailsFile());
        }
        tourDetails.setAdditionalPhotos(savedPhotos);
        tour.setTourDetails(tourDetails);

        when(tourRepository.findTourByIdAndUserId(tourId, userId)).thenReturn(Optional.of(tour));

        // Act & Assert
        MemoryLimitException exception = assertThrows(MemoryLimitException.class, () -> {
            tourDetailsService.updateTourDetailsPhotos(additionalPhotos, userId, tourId);
        });

        assertEquals("Max storage is limited by 5. Delete some photos or add 0", exception.getMessage());
    }

    @Test
    public void testUpdateTourDetailsPhotosWhenAdditionalPhotosWithinLimitThenReturnDetailsRespondDto() {
        // Arrange
        Long userId = 1L;
        Long tourId = 1L;
        List<MultipartFile> additionalPhotos = mock(List.class);
        when(additionalPhotos.size()).thenReturn(2); // Within the limit

        Tour tour = new Tour();
        TourDetails tourDetails = new TourDetails();
        List<TourDetailsFile> savedPhotos = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            savedPhotos.add(new TourDetailsFile());
        }
        tourDetails.setAdditionalPhotos(savedPhotos);
        tour.setTourDetails(tourDetails);

        when(tourRepository.findTourByIdAndUserId(tourId, userId)).thenReturn(Optional.of(tour));
        when(s3Service.uploadFileToS3(anyString(), eq(additionalPhotos))).thenReturn(Collections.emptyList());

        DetailsRespondDto expectedRespondDto = new DetailsRespondDto();
        when(tourDetailsMapper.toDto(tourDetails)).thenReturn(expectedRespondDto);

        // Act
        DetailsRespondDto actualRespondDto = tourDetailsService.updateTourDetailsPhotos(additionalPhotos, userId, tourId);

        // Assert
        assertNotNull(actualRespondDto);
        assertEquals(expectedRespondDto, actualRespondDto);
        verify(tourRepository, times(1)).findTourByIdAndUserId(tourId, userId);
        verify(s3Service, times(1)).uploadFileToS3(anyString(), eq(additionalPhotos));
        verify(tourDetailsMapper, times(1)).toDto(tourDetails);
    }
}