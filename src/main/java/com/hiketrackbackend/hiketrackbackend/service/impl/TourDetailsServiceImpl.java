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
import com.hiketrackbackend.hiketrackbackend.service.TourDetailsService;
import com.hiketrackbackend.hiketrackbackend.service.files.FileStorageService;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class TourDetailsServiceImpl implements TourDetailsService {
    private static final int ADDITIONAL_PHOTOS_LIMIT = 5;
    private static final String FOLDER_NAME = "tour_details";
    private final FileStorageService s3Service;
    private final TourDetailsMapper tourDetailsMapper;
    private final TourRepository tourRepository;

    @Override
    public TourDetails createTourDetails(
            Tour tour,
            DetailsRequestDto requestDto,
            List<MultipartFile> photos
    ) {
        if (photos.size() > ADDITIONAL_PHOTOS_LIMIT) {
            throw new MemoryLimitException("Maximum 5 for uploading is " + ADDITIONAL_PHOTOS_LIMIT);
        }
        TourDetails tourDetails = tourDetailsMapper.toEntity(requestDto);
        setAdditionalFilesToTourDetails(photos, tourDetails);
        tourDetails.setTour(tour);
        return tourDetails;
    }

    @Override
    @Transactional
    public DetailsRespondDto updateTourDetailsPhotos(
            List<MultipartFile> additionalPhotos,
            Long userId,
            Long tourId
    ) {
        Tour tour = findTourByIdAndUserId(tourId, userId);

        TourDetails tourDetails = tour.getTourDetails();
        List<TourDetailsFile> savedPhotos = tourDetails.getAdditionalPhotos();
        if (additionalPhotos.size() > (ADDITIONAL_PHOTOS_LIMIT - savedPhotos.size())) {
            throw new MemoryLimitException("Max storage is limited by "
                    + ADDITIONAL_PHOTOS_LIMIT + ". Delete some photos or add "
                    + (ADDITIONAL_PHOTOS_LIMIT - savedPhotos.size()));
        }
        setAdditionalFilesToTourDetails(additionalPhotos, tourDetails);
        tourDetails.setTour(tour);

        tour.setTourDetails(tourDetails);
        tourRepository.save(tour);
        return tourDetailsMapper.toDto(tourDetails);
    }

    private void setAdditionalFilesToTourDetails(
            List<MultipartFile> files,
            TourDetails tourDetails
    ) {
        List<TourDetailsFile> photos = new ArrayList<>();
        List<String> urls = s3Service.uploadFileToS3(FOLDER_NAME, files);
        for (String url : urls) {
            TourDetailsFile file = new TourDetailsFile();
            file.setFileUrl(url);
            file.setTourDetails(tourDetails);
            photos.add(file);
        }
        tourDetails.setAdditionalPhotos(photos);
    }

    private Tour findTourByIdAndUserId(Long id, Long userId) {
        return tourRepository.findTourByIdAndUserId(id, userId).orElseThrow(
                () -> new EntityNotFoundException(
                        "Tour not found with id: " + id + " and user id: " + userId)
        );
    }
}
