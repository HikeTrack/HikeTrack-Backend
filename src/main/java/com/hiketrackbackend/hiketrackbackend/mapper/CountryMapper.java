package com.hiketrackbackend.hiketrackbackend.mapper;

import com.hiketrackbackend.hiketrackbackend.config.MapperConfig;
import com.hiketrackbackend.hiketrackbackend.dto.country.CountryRequestDto;
import com.hiketrackbackend.hiketrackbackend.dto.country.CountryRespondDto;
import com.hiketrackbackend.hiketrackbackend.model.country.Country;
import com.hiketrackbackend.hiketrackbackend.model.country.CountryFile;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.ArrayList;
import java.util.List;

@Mapper(config = MapperConfig.class)
public interface CountryMapper {
    Country toEntity(CountryRequestDto requestDto);

    CountryRespondDto toDto(Country country);

    @AfterMapping
    default void setUrls(@MappingTarget CountryRespondDto respondDto, Country country) {
        List<CountryFile> photos = country.getPhotos();
        List<String> urls = new ArrayList<>();
        for (CountryFile photo : photos) {
            String fileUrl = photo.getFileUrl();
            urls.add(fileUrl);
        }
        respondDto.setPhotoUrls(urls);
    }
}
