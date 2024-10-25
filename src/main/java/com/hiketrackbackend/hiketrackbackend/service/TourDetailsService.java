package com.hiketrackbackend.hiketrackbackend.service;

import com.hiketrackbackend.hiketrackbackend.dto.details.DetailsRequestDto;
import com.hiketrackbackend.hiketrackbackend.dto.details.DetailsRespondDto;
import com.hiketrackbackend.hiketrackbackend.model.tour.Tour;
import com.hiketrackbackend.hiketrackbackend.model.tour.details.TourDetails;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface TourDetailsService {
    TourDetails createTourDetails(Tour tour, DetailsRequestDto requestDto, List<MultipartFile> photos);

    DetailsRespondDto updateTourDetailsPhotos(List<MultipartFile> additionalPhotos, Long userId, Long tourId);
}
