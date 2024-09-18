package com.hiketrackbackend.hiketrackbackend.service;

import com.hiketrackbackend.hiketrackbackend.dto.tour.TourRequestDto;
import com.hiketrackbackend.hiketrackbackend.dto.tour.TourRespondDto;
import com.hiketrackbackend.hiketrackbackend.dto.tour.TourRespondWithoutDetailsAndReviews;
import com.hiketrackbackend.hiketrackbackend.dto.tour.TourRespondWithoutReviews;
import com.hiketrackbackend.hiketrackbackend.dto.tour.TourSearchParameters;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface TourService {
    TourRespondWithoutDetailsAndReviews createTour(TourRequestDto requestDto);

    void deleteById(Long id);

    List<TourRespondWithoutDetailsAndReviews> getAll(Pageable pageable);

    TourRespondDto getById(Long id, int page, int size);

    List<TourRespondWithoutDetailsAndReviews> getByRating();

    List<TourRespondWithoutReviews> search(TourSearchParameters params, Pageable pageable);
}
