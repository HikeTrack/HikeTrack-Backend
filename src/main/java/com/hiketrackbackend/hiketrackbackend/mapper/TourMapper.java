package com.hiketrackbackend.hiketrackbackend.mapper;

import com.hiketrackbackend.hiketrackbackend.config.MapperConfig;
import com.hiketrackbackend.hiketrackbackend.dto.tour.TourRequestDto;
import com.hiketrackbackend.hiketrackbackend.dto.tour.TourRespondDto;
import com.hiketrackbackend.hiketrackbackend.dto.tour.TourRespondWithoutDetailsAndReviews;
import com.hiketrackbackend.hiketrackbackend.dto.tour.TourRespondWithoutReviews;
import com.hiketrackbackend.hiketrackbackend.dto.tour.TourUpdateRequestDto;
import com.hiketrackbackend.hiketrackbackend.model.tour.Tour;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class, uses = {TourDetailsMapper.class})
public interface TourMapper {
    Tour toEntity(TourRequestDto requestDto);

    @Mapping(source = "tourDetails", target = "details")
    @Mapping(target = "countryId", source = "country.id")
    @Mapping(target = "guideId", source = "user.id")
    TourRespondDto toDto(Tour tour);

    @Mapping(target = "countryId", source = "country.id")
    @Mapping(target = "guideId", source = "user.id")
    TourRespondWithoutDetailsAndReviews toDtoWithoutDetailsAndReviews(Tour tour);

    @Mapping(source = "tourDetails", target = "details")
    @Mapping(target = "countryId", source = "country.id")
    @Mapping(target = "guideId", source = "user.id")
    TourRespondWithoutReviews toDtoWithoutReviews(Tour tour);

    void updateEntityFromDto(TourUpdateRequestDto requestDto, @MappingTarget Tour tour);
}
