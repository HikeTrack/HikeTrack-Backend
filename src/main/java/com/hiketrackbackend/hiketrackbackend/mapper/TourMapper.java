package com.hiketrackbackend.hiketrackbackend.mapper;

import com.hiketrackbackend.hiketrackbackend.config.MapperConfig;
import com.hiketrackbackend.hiketrackbackend.dto.details.DetailsRequestDto;
import com.hiketrackbackend.hiketrackbackend.dto.details.DetailsRespondDto;
import com.hiketrackbackend.hiketrackbackend.dto.tour.*;
import com.hiketrackbackend.hiketrackbackend.dto.user.UserRespondDto;
import com.hiketrackbackend.hiketrackbackend.model.Role;
import com.hiketrackbackend.hiketrackbackend.model.tour.details.Details;
import com.hiketrackbackend.hiketrackbackend.model.tour.Tour;
import com.hiketrackbackend.hiketrackbackend.model.tour.details.TourDetailsFile;
import com.hiketrackbackend.hiketrackbackend.model.user.User;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Mapper(config = MapperConfig.class)
public interface TourMapper {
    Tour toEntity(TourRequestDto requestDto);

    @Mapping(source = "details", target = "details")
    @Mapping(target = "countryId", source = "country.id")
    @Mapping(target = "guideId", source = "user.id")
    TourRespondDto toDto(Tour tour);

    @Mapping(target = "countryId", source = "country.id")
    @Mapping(target = "guideId", source = "user.id")
    TourRespondWithoutDetailsAndReviews toDtoWithoutDetailsAndReviews(Tour tour);

    @Mapping(source = "details", target = "details")
    @Mapping(target = "countryId", source = "country.id")
    @Mapping(target = "guideId", source = "user.id")
    TourRespondWithoutReviews toDtoWithoutReviews(Tour tour);

    Details toEntity(DetailsRequestDto requestDto);

    @Mapping(target = "additionalPhotos" , ignore = true)
    DetailsRespondDto toDto(Details tourDetails);

    void updateEntityFromDto(TourUpdateRequestDto requestDto, @MappingTarget Tour tour);

    @AfterMapping
    default void setUrls(@MappingTarget DetailsRespondDto respondDto, Details details) {
        List<TourDetailsFile> photos = details.getAdditionalPhotos();
        List<String> urls = new ArrayList<>();
        for (TourDetailsFile photo : photos) {
            String fileUrl = photo.getFileUrl();
            urls.add(fileUrl);
        }
        respondDto.setAdditionalPhotos(urls);
    }
}
