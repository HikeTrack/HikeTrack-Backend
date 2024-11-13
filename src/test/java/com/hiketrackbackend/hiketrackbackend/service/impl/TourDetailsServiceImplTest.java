package com.hiketrackbackend.hiketrackbackend.service.impl;

import com.hiketrackbackend.hiketrackbackend.dto.tour.details.DetailsRequestDto;
import com.hiketrackbackend.hiketrackbackend.dto.tour.details.DetailsRespondDto;
import com.hiketrackbackend.hiketrackbackend.exception.MemoryLimitException;
import com.hiketrackbackend.hiketrackbackend.mapper.TourDetailsMapper;
import com.hiketrackbackend.hiketrackbackend.model.tour.Tour;
import com.hiketrackbackend.hiketrackbackend.model.tour.details.TourDetails;
import com.hiketrackbackend.hiketrackbackend.model.tour.details.TourDetailsFile;
import com.hiketrackbackend.hiketrackbackend.repository.tour.TourRepository;
import com.hiketrackbackend.hiketrackbackend.service.files.FileStorageService;
import org.junit.jupiter.api.DisplayName;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
    @DisplayName("Create tour details with over limit of additional photo")
    public void testCreateTourDetailsWhenPhotosExceedLimitThenThrowMemoryLimitException() {
        Tour tour = new Tour();
        DetailsRequestDto requestDto = new DetailsRequestDto();
        List<MultipartFile> photos = mock(List.class);
        when(photos.size()).thenReturn(6);

        MemoryLimitException exception = assertThrows(MemoryLimitException.class, () -> {
            tourDetailsService.createTourDetails(tour, requestDto, photos);
        });

        assertEquals("Maximum 5 for uploading is 5", exception.getMessage());
    }

    @Test
    @DisplayName("Create tour details with normal photo limit")
    public void testCreateTourDetailsWhenPhotosWithinLimitThenReturnTourDetails() {
        Tour tour = new Tour();
        DetailsRequestDto requestDto = new DetailsRequestDto();
        List<MultipartFile> photos = mock(List.class);
        when(photos.size()).thenReturn(3);

        TourDetails expectedTourDetails = new TourDetails();
        when(tourDetailsMapper.toEntity(requestDto)).thenReturn(expectedTourDetails);
        when(s3Service.uploadFileToS3(anyString(), eq(photos))).thenReturn(Collections.emptyList());

        TourDetails actualTourDetails = tourDetailsService.createTourDetails(tour, requestDto, photos);

        assertNotNull(actualTourDetails);
        assertEquals(expectedTourDetails, actualTourDetails);
        verify(tourDetailsMapper, times(1)).toEntity(requestDto);
        verify(s3Service, times(1)).uploadFileToS3(anyString(), eq(photos));
    }

    @Test
    @DisplayName("Update tour details with over the limit of photos")
    public void testUpdateTourDetailsPhotosWhenAdditionalPhotosExceedLimitThenThrowMemoryLimitException() {
        Long userId = 1L;
        Long tourId = 1L;
        List<MultipartFile> additionalPhotos = mock(List.class);
        when(additionalPhotos.size()).thenReturn(6);

        Tour tour = new Tour();
        TourDetails tourDetails = new TourDetails();
        List<TourDetailsFile> savedPhotos = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            savedPhotos.add(new TourDetailsFile());
        }
        tourDetails.setAdditionalPhotos(savedPhotos);
        tour.setTourDetails(tourDetails);

        when(tourRepository.findTourByIdAndUserId(tourId, userId)).thenReturn(Optional.of(tour));

        MemoryLimitException exception = assertThrows(MemoryLimitException.class, () -> {
            tourDetailsService.updateTourDetailsPhotos(additionalPhotos, userId, tourId);
        });

        assertEquals("Max storage is limited by 5. Delete some photos or add 0", exception.getMessage());
    }

    @Test
    @DisplayName("Update tour details within the photo limit")
    public void testUpdateTourDetailsPhotosWhenAdditionalPhotosWithinLimitThenReturnDetailsRespondDto() {
        Long userId = 1L;
        Long tourId = 1L;
        List<MultipartFile> additionalPhotos = mock(List.class);
        when(additionalPhotos.size()).thenReturn(2);

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

        DetailsRespondDto actualRespondDto = tourDetailsService.updateTourDetailsPhotos(additionalPhotos, userId, tourId);

        assertNotNull(actualRespondDto);
        assertEquals(expectedRespondDto, actualRespondDto);
        verify(tourRepository, times(1)).findTourByIdAndUserId(tourId, userId);
        verify(s3Service, times(1)).uploadFileToS3(anyString(), eq(additionalPhotos));
        verify(tourDetailsMapper, times(1)).toDto(tourDetails);
    }
}
