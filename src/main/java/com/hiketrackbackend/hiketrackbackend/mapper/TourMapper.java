package com.hiketrackbackend.hiketrackbackend.mapper;

import com.hiketrackbackend.hiketrackbackend.config.MapperConfig;
import com.hiketrackbackend.hiketrackbackend.dto.details.DetailsRequestDto;
import com.hiketrackbackend.hiketrackbackend.dto.details.DetailsRespondDto;
import com.hiketrackbackend.hiketrackbackend.dto.tour.TourRequestDto;
import com.hiketrackbackend.hiketrackbackend.dto.tour.TourRespondDto;
import com.hiketrackbackend.hiketrackbackend.dto.tour.TourRespondWithoutDetailsAndReviews;
import com.hiketrackbackend.hiketrackbackend.dto.tour.TourRespondWithoutReviews;
import com.hiketrackbackend.hiketrackbackend.model.details.Details;
import com.hiketrackbackend.hiketrackbackend.model.tour.Tour;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class)
public interface TourMapper {
    @Mapping(target = "details", ignore = true)
    Tour toEntity(TourRequestDto requestDto);

    @Mapping(source = "details", target = "details")
    @Mapping(target = "countryId", source = "country.id")
    @Mapping(target = "guideId", source = "user.id")
    TourRespondDto toDto(Tour tour);

    @Mapping(target = "countryId", source = "country.id")
    TourRespondWithoutDetailsAndReviews toDtoWithoutDetailsAndReviews(Tour tour);

    @Mapping(source = "details", target = "details")
    @Mapping(target = "countryId", source = "country.id")
    @Mapping(target = "guideId", source = "user.id")
    TourRespondWithoutReviews toDtoWithoutReviews(Tour tour);

    DetailsRespondDto toDto(Details tourDetails);

    @AfterMapping
    default void linkTourDetails(@MappingTarget Tour tour, TourRequestDto dto) {
        if (tour.getDetails() == null && dto.getDetailsRequestDto() != null) {
            Details tourDetails = new Details();
            DetailsRequestDto detailsRequestDto = dto.getDetailsRequestDto();
            tourDetails.setAdditionalPhotos(detailsRequestDto.getAdditionalPhotos());
            tourDetails.setElevationGain(detailsRequestDto.getElevationGain());
            tourDetails.setRouteType(detailsRequestDto.getRouteType());
            tourDetails.setDuration(detailsRequestDto.getDuration());
            tourDetails.setMap(detailsRequestDto.getMap());
            tourDetails.setActivity(detailsRequestDto.getActivity());
            tour.setTourDetails(tourDetails);
        }
    }
}
