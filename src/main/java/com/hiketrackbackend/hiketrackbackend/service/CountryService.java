package com.hiketrackbackend.hiketrackbackend.service;

import com.hiketrackbackend.hiketrackbackend.dto.country.CountrySearchParameters;
import com.hiketrackbackend.hiketrackbackend.dto.country.RequestCountryDto;
import com.hiketrackbackend.hiketrackbackend.dto.country.RespondCountryDto;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface CountryService {
    RespondCountryDto createCountry(RequestCountryDto requestDto);

    List<RespondCountryDto> search(CountrySearchParameters params, Pageable pageable);
}
