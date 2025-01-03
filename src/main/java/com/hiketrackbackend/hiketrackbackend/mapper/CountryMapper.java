package com.hiketrackbackend.hiketrackbackend.mapper;

import com.hiketrackbackend.hiketrackbackend.config.MapperConfig;
import com.hiketrackbackend.hiketrackbackend.dto.country.CountryRequestDto;
import com.hiketrackbackend.hiketrackbackend.dto.country.CountryRespondDto;
import com.hiketrackbackend.hiketrackbackend.dto.country.CountryRespondWithPhotoDto;
import com.hiketrackbackend.hiketrackbackend.model.country.Country;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class)
public interface CountryMapper {
    Country toEntity(CountryRequestDto requestDto);

    CountryRespondDto toDto(Country country);

    List<CountryRespondWithPhotoDto> toDto(List<Country> country);

    @Mapping(target = "photo", ignore = true)
    void updateCountryFromDto(@MappingTarget Country country, CountryRequestDto requestDto);
}
