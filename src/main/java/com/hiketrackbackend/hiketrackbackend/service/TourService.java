package com.hiketrackbackend.hiketrackbackend.service;

import com.hiketrackbackend.hiketrackbackend.dto.details.DetailsRespondDto;
import com.hiketrackbackend.hiketrackbackend.dto.tour.*;
import com.hiketrackbackend.hiketrackbackend.model.user.User;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface TourService {
    TourRespondWithoutReviews createTour(
            TourRequestDto requestDto,
            User user,
            MultipartFile photo,
            List<MultipartFile> additionalPhoto
    );

    TourRespondWithoutReviews updateTour(TourUpdateRequestDto requestDto, Long userId, Long tourId);

    DetailsRespondDto updateTourDetailsPhotos(
            List<MultipartFile> additionalPhotos,
            Long userId,
            Long tourId
    );

    TourRespondWithoutReviews updateTourPhoto(MultipartFile mainPhoto, Long userId, Long tourId);

    List<TourRespondWithoutDetailsAndReviews> getAll(Pageable pageable);

    TourRespondDto getById(Long id, int page, int size);

    List<TourRespondWithoutDetailsAndReviews> getByRating();

    List<TourRespondWithoutDetailsAndReviews> search(TourSearchParameters params, Pageable pageable);

    void deleteTourByIdAndUserId(Long tourId, Long userId);

    void deleteTourDetailsPhotoById(Long id);
}
