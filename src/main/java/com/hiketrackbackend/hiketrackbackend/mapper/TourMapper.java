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
    @Mapping(target = "reviews", ignore = true)
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
        applyRating(tour, respondDto);
    }

    @AfterMapping
    default void setRating(Tour tour,
                           @MappingTarget TourRespondWithoutDetailsAndReviews respondDto) {
        applyRating(tour, respondDto);
    }

    private void applyRating(Tour tour, Object respondDto) {
        List<Rating> ratings = tour.getRatings();
        Long totalAmountOfMarks = ratings.isEmpty() ? 0L : (long) ratings.size();
        Long averageRating = ratings.isEmpty() ? 0L :
                ratings.stream().mapToLong(Rating::getRating).sum() / ratings.size();

        if (respondDto instanceof TourRespondDto dto) {
            dto.setTotalAmountOfMarks(totalAmountOfMarks);
            dto.setAverageRating(averageRating);
        } else if (respondDto instanceof TourRespondWithoutDetailsAndReviews dto) {
            dto.setTotalAmountOfMarks(totalAmountOfMarks);
            dto.setAverageRating(averageRating);
        }
    }
}
