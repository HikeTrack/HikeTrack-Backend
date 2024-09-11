package com.hiketrackbackend.hiketrackbackend.mapper;

import com.hiketrackbackend.hiketrackbackend.config.MapperConfig;
import com.hiketrackbackend.hiketrackbackend.dto.country.CountryRequestDto;
import com.hiketrackbackend.hiketrackbackend.dto.tour.TourRequestDto;
import com.hiketrackbackend.hiketrackbackend.dto.tour.TourRespondDto;
import com.hiketrackbackend.hiketrackbackend.model.Country;
import com.hiketrackbackend.hiketrackbackend.model.Tour;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(config = MapperConfig.class)
public interface TourMapper {
    Tour toEntity(TourRequestDto requestDto);

    TourRespondDto toDto(Tour tour);

    @AfterMapping
    default void setCountryIds(@MappingTarget TourRespondDto respondDto, Tour tour) {
        Long id = tour.getCountry().getId();
        respondDto.setCountryId(id);
    }
}
