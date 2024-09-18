package com.hiketrackbackend.hiketrackbackend.mapper;

import com.hiketrackbackend.hiketrackbackend.config.MapperConfig;
import com.hiketrackbackend.hiketrackbackend.dto.tour.TourRequestDto;
import com.hiketrackbackend.hiketrackbackend.dto.tour.TourRespondDto;
import com.hiketrackbackend.hiketrackbackend.dto.tour.TourRespondWithoutDetailsAndReviews;
import com.hiketrackbackend.hiketrackbackend.dto.tour.TourRespondWithoutReviews;
import com.hiketrackbackend.hiketrackbackend.model.tour.Tour;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class)
public interface TourMapper {
    Tour toEntity(TourRequestDto requestDto);

    TourRespondDto toDto(Tour tour);

    TourRespondWithoutDetailsAndReviews toDtoWithoutDetailsAndReviews(Tour tour);

    TourRespondWithoutReviews toDtoWithoutReviews(Tour tour);

    @AfterMapping
    default void setCountryIds(@MappingTarget TourRespondWithoutReviews respondDto, Tour tour) {
        Long id = tour.getCountry().getId();
        respondDto.setCountryId(id);
    }

    @AfterMapping
    default void setCountryIds(@MappingTarget TourRespondWithoutDetailsAndReviews respondDto, Tour tour) {
        Long id = tour.getCountry().getId();
        respondDto.setCountryId(id);
    }

    @AfterMapping
    default void setCountryIds(@MappingTarget TourRespondDto respondDto, Tour tour) {
        Long id = tour.getCountry().getId();
        respondDto.setCountryId(id);
    }
}
