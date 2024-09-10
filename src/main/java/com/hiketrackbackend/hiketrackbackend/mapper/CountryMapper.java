package com.hiketrackbackend.hiketrackbackend.mapper;

import com.hiketrackbackend.hiketrackbackend.config.MapperConfig;
import com.hiketrackbackend.hiketrackbackend.dto.country.RequestCountryDto;
import com.hiketrackbackend.hiketrackbackend.dto.country.RespondCountryDto;
import com.hiketrackbackend.hiketrackbackend.model.Country;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface CountryMapper {
    Country toEntity(RequestCountryDto requestDto);

    RespondCountryDto toDto(Country country);
}
