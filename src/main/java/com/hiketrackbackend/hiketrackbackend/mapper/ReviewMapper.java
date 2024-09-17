package com.hiketrackbackend.hiketrackbackend.mapper;

import com.hiketrackbackend.hiketrackbackend.config.MapperConfig;
import com.hiketrackbackend.hiketrackbackend.dto.reviews.ReviewsRespondDto;
import com.hiketrackbackend.hiketrackbackend.model.Review;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface ReviewMapper {
    ReviewsRespondDto toDto(Review review);
}
