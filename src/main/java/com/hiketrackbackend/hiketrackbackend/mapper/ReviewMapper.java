package com.hiketrackbackend.hiketrackbackend.mapper;

import com.hiketrackbackend.hiketrackbackend.config.MapperConfig;
import com.hiketrackbackend.hiketrackbackend.dto.reviews.ReviewRequestDto;
import com.hiketrackbackend.hiketrackbackend.dto.reviews.ReviewsRespondDto;
import com.hiketrackbackend.hiketrackbackend.model.tour.Review;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class)
public interface ReviewMapper {
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "tourId", source = "tour.id")
    ReviewsRespondDto toDto(Review review);

    Review toEntity(ReviewRequestDto requestDto);

    void updateEntityFromDto(@MappingTarget Review review, ReviewRequestDto requestDto);
}
