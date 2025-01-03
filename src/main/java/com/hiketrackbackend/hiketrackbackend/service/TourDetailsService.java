package com.hiketrackbackend.hiketrackbackend.service;

import com.hiketrackbackend.hiketrackbackend.dto.tour.details.DetailsRequestDto;
import com.hiketrackbackend.hiketrackbackend.dto.tour.details.DetailsRespondDto;
import com.hiketrackbackend.hiketrackbackend.dto.tour.details.file.TourDetailFileRespondDto;
import com.hiketrackbackend.hiketrackbackend.model.tour.Tour;
import com.hiketrackbackend.hiketrackbackend.model.tour.details.TourDetails;
import java.util.List;
import java.util.Set;
import org.springframework.web.multipart.MultipartFile;

public interface TourDetailsService {
    TourDetails createTourDetails(
            Tour tour,
            DetailsRequestDto requestDto,
            List<MultipartFile> photos
    );

    DetailsRespondDto updateTourDetailsPhotos(
            List<MultipartFile> additionalPhotos,
            Long userId,
            Long tourId
    );

    TourDetailFileRespondDto getTourDetailPhoto(Long id);

    Set<TourDetailFileRespondDto> getAllByTourDetail(Long tourDetailId);

    void deleteTourDetailsPhotoById(Long id);
}
