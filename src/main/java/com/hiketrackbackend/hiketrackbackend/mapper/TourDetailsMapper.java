package com.hiketrackbackend.hiketrackbackend.mapper;

import com.hiketrackbackend.hiketrackbackend.config.MapperConfig;
import com.hiketrackbackend.hiketrackbackend.dto.tour.details.DetailsRequestDto;
import com.hiketrackbackend.hiketrackbackend.dto.tour.details.DetailsRespondDto;
import com.hiketrackbackend.hiketrackbackend.model.tour.details.TourDetails;
import com.hiketrackbackend.hiketrackbackend.model.tour.details.TourDetailsFile;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
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
    default void setPhotoIds(@MappingTarget DetailsRespondDto respondDto, TourDetails tourDetails) {
        Set<TourDetailsFile> photos = tourDetails.getAdditionalPhotos();
        List<Long> ids = new ArrayList<>();
        for (TourDetailsFile photo : photos) {
            Long id = photo.getId();
            ids.add(id);
        }
        respondDto.setAdditionalPhotos(ids);
    }
}
