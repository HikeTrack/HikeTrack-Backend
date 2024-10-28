package com.hiketrackbackend.hiketrackbackend.mapper;

import com.hiketrackbackend.hiketrackbackend.config.MapperConfig;
import com.hiketrackbackend.hiketrackbackend.dto.rating.RatingRequestDto;
import com.hiketrackbackend.hiketrackbackend.dto.rating.RatingRespondDto;
import com.hiketrackbackend.hiketrackbackend.model.tour.Rating;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class)
public interface RatingMapper {
    Rating toEntity(RatingRequestDto requestDto);

    RatingRespondDto toDto(Rating rating);

    void updateEntityFromDto(@MappingTarget Rating rating, RatingRequestDto requestDto);
}
