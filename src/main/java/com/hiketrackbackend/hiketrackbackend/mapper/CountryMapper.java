package com.hiketrackbackend.hiketrackbackend.mapper;

import com.hiketrackbackend.hiketrackbackend.config.MapperConfig;
import com.hiketrackbackend.hiketrackbackend.dto.country.CountryRequestDto;
import com.hiketrackbackend.hiketrackbackend.dto.country.CountryRespondDto;
import com.hiketrackbackend.hiketrackbackend.model.country.Country;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface CountryMapper {
    Country toEntity(CountryRequestDto requestDto);

    CountryRespondDto toDto(Country country);
}
