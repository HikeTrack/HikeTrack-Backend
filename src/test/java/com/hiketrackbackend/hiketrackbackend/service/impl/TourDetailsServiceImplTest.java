package com.hiketrackbackend.hiketrackbackend.service.impl;

import com.hiketrackbackend.hiketrackbackend.dto.tour.details.DetailsRequestDto;
import com.hiketrackbackend.hiketrackbackend.dto.tour.details.DetailsRespondDto;
import com.hiketrackbackend.hiketrackbackend.dto.tour.details.file.TourDetailFileRespondDto;
import com.hiketrackbackend.hiketrackbackend.exception.EntityNotFoundException;
import com.hiketrackbackend.hiketrackbackend.exception.MemoryLimitException;
import com.hiketrackbackend.hiketrackbackend.mapper.TourDetailsMapper;
import com.hiketrackbackend.hiketrackbackend.model.tour.Tour;
import com.hiketrackbackend.hiketrackbackend.model.tour.details.TourDetails;
import com.hiketrackbackend.hiketrackbackend.model.tour.details.TourDetailsFile;
import com.hiketrackbackend.hiketrackbackend.repository.TourDetailsFileRepository;
import com.hiketrackbackend.hiketrackbackend.repository.tour.TourRepository;
import com.hiketrackbackend.hiketrackbackend.service.files.FileStorageService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TourDetailsServiceImplTest {
    protected static MockMvc mockMvc;

    @Mock
    private FileStorageService s3Service;

    @Mock
    private TourDetailsMapper tourDetailsMapper;

    @Mock
    private TourRepository tourRepository;

    @Mock
    private TourDetailsFileRepository tourDetailsFileRepository;

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
        Set<TourDetailsFile> savedPhotos = new HashSet<>();
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
        Set<TourDetailsFile> savedPhotos = new HashSet<>();
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

    @Test
    @DisplayName("Get single photo with valid data")
    void getTourDetailPhoto_ShouldReturnDto_WhenEntityExists() {
        Long id = 1L;
        TourDetailsFile detailsFile = new TourDetailsFile();
        TourDetailFileRespondDto expectedDto = new TourDetailFileRespondDto();

        when(tourDetailsFileRepository.findById(id)).thenReturn(Optional.of(detailsFile));
        when(tourDetailsMapper.toDto(detailsFile)).thenReturn(expectedDto);

        TourDetailFileRespondDto actualDto = tourDetailsService.getTourDetailPhoto(id);

        verify(tourDetailsFileRepository).findById(id);
        verify(tourDetailsMapper).toDto(detailsFile);
        assertEquals(expectedDto, actualDto);
    }

    @Test
    @DisplayName("Get single photo by not exist id")
    void getTourDetailPhoto_ShouldThrowEntityNotFoundException_WhenEntityDoesNotExist() {
        Long id = 2L;

        when(tourDetailsFileRepository.findById(id)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> tourDetailsService.getTourDetailPhoto(id)
        );
        assertEquals("Tour details photo not found with id: " + id, exception.getMessage());
        verify(tourDetailsFileRepository).findById(id);
        verifyNoInteractions(tourDetailsMapper);
    }

    @Test
    @DisplayName("Valid number of repository and mapper calls")
    void getTourDetailPhoto_ShouldCallRepositoryAndMapperOnce_WhenEntityExists() {
        Long id = 3L;
        TourDetailsFile detailsFile = new TourDetailsFile();
        TourDetailFileRespondDto expectedDto = new TourDetailFileRespondDto();

        when(tourDetailsFileRepository.findById(id)).thenReturn(Optional.of(detailsFile));
        when(tourDetailsMapper.toDto(detailsFile)).thenReturn(expectedDto);

        tourDetailsService.getTourDetailPhoto(id);

        verify(tourDetailsFileRepository, times(1)).findById(id);
        verify(tourDetailsMapper, times(1)).toDto(detailsFile);
    }

    @Test
    @DisplayName("Test get all photos by detail id return set dtos")
    void getAllByTourDetail_ShouldReturnSetOfDtos_WhenEntitiesExist() {
        Long tourDetailId = 1L;
        TourDetailsFile file1 = new TourDetailsFile();
        TourDetailsFile file2 = new TourDetailsFile();
        TourDetailFileRespondDto dto1 = new TourDetailFileRespondDto();
        TourDetailFileRespondDto dto2 = new TourDetailFileRespondDto();

        Set<TourDetailsFile> files = Set.of(file1, file2);

        when(tourDetailsFileRepository.findByTourDetailsId(tourDetailId)).thenReturn(files);
        when(tourDetailsMapper.toDto(file1)).thenReturn(dto1);
        when(tourDetailsMapper.toDto(file2)).thenReturn(dto2);

        Set<TourDetailFileRespondDto> result = tourDetailsService.getAllByTourDetail(tourDetailId);

        verify(tourDetailsFileRepository).findByTourDetailsId(tourDetailId);
        verify(tourDetailsMapper).toDto(file1);
        verify(tourDetailsMapper).toDto(file2);

        assertEquals(2, result.size());
        assertTrue(result.contains(dto1));
        assertTrue(result.contains(dto2));
    }

    @Test
    @DisplayName("Get empty set when id not exist id DB")
    void getAllByTourDetail_ShouldReturnEmptySet_WhenNoEntitiesExist() {
        Long tourDetailId = 2L;

       when(tourDetailsFileRepository.findByTourDetailsId(tourDetailId)).thenReturn(Collections.emptySet());

        Set<TourDetailFileRespondDto> result = tourDetailsService.getAllByTourDetail(tourDetailId);

        verify(tourDetailsFileRepository).findByTourDetailsId(tourDetailId);
        verifyNoInteractions(tourDetailsMapper);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Test get all when id is valid and return is valid")
    void getAllByTourDetail_ShouldReturnValidDtos_WhenValidDataProvided() {
        Long tourDetailId = 4L;

        TourDetailsFile file1 = new TourDetailsFile();
        file1.setId(1L);
        file1.setFileUrl("link1");

        TourDetailsFile file2 = new TourDetailsFile();
        file2.setId(2L);
        file2.setFileUrl("link2");

        TourDetailFileRespondDto dto1 = new TourDetailFileRespondDto();
        dto1.setId(1L);
        dto1.setFileUrl("link1");

        TourDetailFileRespondDto dto2 = new TourDetailFileRespondDto();
        dto2.setId(2L);
        dto2.setFileUrl("link2");

        Set<TourDetailsFile> files = Set.of(file1, file2);
        when(tourDetailsFileRepository.findByTourDetailsId(tourDetailId)).thenReturn(files);
        when(tourDetailsMapper.toDto(file1)).thenReturn(dto1);
        when(tourDetailsMapper.toDto(file2)).thenReturn(dto2);

        Set<TourDetailFileRespondDto> result = tourDetailsService.getAllByTourDetail(tourDetailId);

        verify(tourDetailsFileRepository).findByTourDetailsId(tourDetailId);
        verify(tourDetailsMapper).toDto(file1);
        verify(tourDetailsMapper).toDto(file2);

        assertEquals(2, result.size());
        assertTrue(result.contains(dto1));
        assertTrue(result.contains(dto2));
    }

    @Test
    @DisplayName("Delete additional tour photo")
    public void testDeleteTourDetailsPhotoByIdWhenPhotoFoundThenDeletePhoto() {
        Long photoId = 1L;
        TourDetailsFile photo = new TourDetailsFile();
        photo.setId(photoId);

        when(tourDetailsFileRepository.findById(photoId)).thenReturn(Optional.of(photo));

        tourDetailsService.deleteTourDetailsPhotoById(photoId);

        verify(tourDetailsFileRepository, times(1)).delete(photo);
    }

    @Test
    @DisplayName("Delete additional tour photo with not valid id")
    public void testDeleteTourDetailsPhotoByIdWhenPhotoNotFoundThenThrowEntityNotFoundException() {
        Long photoId = 1L;

        when(tourDetailsFileRepository.findById(photoId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> tourDetailsService.deleteTourDetailsPhotoById(photoId));
    }
}
