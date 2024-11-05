package com.hiketrackbackend.hiketrackbackend.mapper;

import com.hiketrackbackend.hiketrackbackend.config.MapperConfig;
import com.hiketrackbackend.hiketrackbackend.dto.tour.TourRequestDto;
import com.hiketrackbackend.hiketrackbackend.dto.tour.TourRespondDto;
import com.hiketrackbackend.hiketrackbackend.dto.tour.TourRespondWithoutDetailsAndReviews;
import com.hiketrackbackend.hiketrackbackend.dto.tour.TourRespondWithoutReviews;
import com.hiketrackbackend.hiketrackbackend.dto.tour.TourUpdateRequestDto;
import com.hiketrackbackend.hiketrackbackend.model.tour.Rating;
import com.hiketrackbackend.hiketrackbackend.model.tour.Tour;
import java.util.List;
import org.mapstruct.AfterMapping;
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

    @AfterMapping
    default void setRating(Tour tour, @MappingTarget TourRespondDto respondDto) {
        List<Rating> ratings = tour.getRatings();
        if (ratings.isEmpty()) {
            respondDto.setTotalAmountOfMarks(0L);
            respondDto.setAverageRating(0L);
            return;
        }
        respondDto.setTotalAmountOfMarks((long) ratings.size());
        Long ratingSum = 0L;
        for (Rating value : ratings) {
            Integer rating = value.getRating();
            ratingSum += rating;
        }
        respondDto.setAverageRating(ratingSum / ratings.size());
    }
}
