package com.hiketrackbackend.hiketrackbackend.mapper;

import com.hiketrackbackend.hiketrackbackend.config.MapperConfig;
import com.hiketrackbackend.hiketrackbackend.dto.country.CountryRequestDto;
import com.hiketrackbackend.hiketrackbackend.dto.country.CountryRespondDto;
import com.hiketrackbackend.hiketrackbackend.dto.country.CountryRespondWithPhotoDto;
import com.hiketrackbackend.hiketrackbackend.model.country.Country;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(config = MapperConfig.class)
public interface CountryMapper {
    Country toEntity(CountryRequestDto requestDto);

    CountryRespondDto toDto(Country country);

    List<CountryRespondWithPhotoDto> toDto(List<Country> country);
}
