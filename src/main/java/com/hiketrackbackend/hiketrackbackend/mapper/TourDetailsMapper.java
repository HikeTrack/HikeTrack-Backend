package com.hiketrackbackend.hiketrackbackend.mapper;

import com.hiketrackbackend.hiketrackbackend.config.MapperConfig;
import com.hiketrackbackend.hiketrackbackend.dto.tour.details.DetailsRequestDto;
import com.hiketrackbackend.hiketrackbackend.dto.tour.details.DetailsRespondDto;
import com.hiketrackbackend.hiketrackbackend.model.tour.details.TourDetails;
import com.hiketrackbackend.hiketrackbackend.model.tour.details.TourDetailsFile;
import java.util.ArrayList;
import java.util.List;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class)
public interface TourDetailsMapper {
    TourDetails toEntity(DetailsRequestDto requestDto);

    @Mapping(target = "additionalPhotos", ignore = true)
    DetailsRespondDto toDto(TourDetails tourDetails);

    @AfterMapping
    default void setUrls(@MappingTarget DetailsRespondDto respondDto, TourDetails tourDetails) {
        List<TourDetailsFile> photos = tourDetails.getAdditionalPhotos();
        List<String> urls = new ArrayList<>();
        for (TourDetailsFile photo : photos) {
            String fileUrl = photo.getFileUrl();
            urls.add(fileUrl);
        }
        respondDto.setAdditionalPhotos(urls);
    }
}
