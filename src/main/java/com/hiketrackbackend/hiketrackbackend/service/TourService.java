package com.hiketrackbackend.hiketrackbackend.service;

import com.hiketrackbackend.hiketrackbackend.dto.tour.TourRequestDto;
import com.hiketrackbackend.hiketrackbackend.dto.tour.TourRespondDto;
import com.hiketrackbackend.hiketrackbackend.dto.tour.TourRespondWithoutDetailsAndReviews;
import com.hiketrackbackend.hiketrackbackend.dto.tour.TourRespondWithoutReviews;
import com.hiketrackbackend.hiketrackbackend.dto.tour.TourSearchParameters;
import com.hiketrackbackend.hiketrackbackend.dto.tour.TourUpdateRequestDto;
import com.hiketrackbackend.hiketrackbackend.model.user.User;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface TourService {
    TourRespondWithoutReviews createTour(
            TourRequestDto requestDto,
            User user,
            MultipartFile photo,
            List<MultipartFile> additionalPhoto
    );

    TourRespondWithoutReviews updateTour(TourUpdateRequestDto requestDto, Long userId, Long tourId);

    TourRespondWithoutReviews updateTourPhoto(MultipartFile mainPhoto, Long userId, Long tourId);

    List<TourRespondWithoutDetailsAndReviews> getAll(Pageable pageable);

    TourRespondDto getById(Long id, int page, int size);

    List<TourRespondWithoutDetailsAndReviews> getByRating();

    List<TourRespondWithoutDetailsAndReviews> search(
            TourSearchParameters params,
            Pageable pageable
    );

    void deleteTourByIdAndUserId(Long tourId, Long userId);

    void deleteTourDetailsPhotoById(Long id);

    List<TourRespondWithoutDetailsAndReviews> getAllToursMadeByGuide(
            Long userId, Pageable pageable
    );
}
