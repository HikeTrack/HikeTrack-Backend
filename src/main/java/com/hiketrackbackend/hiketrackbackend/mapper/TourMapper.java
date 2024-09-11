package com.hiketrackbackend.hiketrackbackend.mapper;

import com.hiketrackbackend.hiketrackbackend.config.MapperConfig;
import com.hiketrackbackend.hiketrackbackend.dto.tour.TourRequestDto;
import com.hiketrackbackend.hiketrackbackend.dto.tour.TourRespondDto;
import com.hiketrackbackend.hiketrackbackend.model.Tour;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface TourMapper {
    Tour toEntity(TourRequestDto requestDto);

    TourRespondDto toDto(Tour tour);
}
