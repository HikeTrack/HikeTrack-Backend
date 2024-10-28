package com.hiketrackbackend.hiketrackbackend.service;

import com.hiketrackbackend.hiketrackbackend.dto.country.CountryDeleteRequestDto;
import com.hiketrackbackend.hiketrackbackend.dto.country.CountryRequestDto;
import com.hiketrackbackend.hiketrackbackend.dto.country.CountryRespondDto;
import com.hiketrackbackend.hiketrackbackend.dto.country.CountryRespondWithPhotoDto;
import com.hiketrackbackend.hiketrackbackend.dto.country.CountrySearchParameters;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

public interface CountryService {
    CountryRespondDto createCountry(CountryRequestDto requestDto, MultipartFile file);

    List<CountryRespondDto> search(CountrySearchParameters params, Pageable pageable);

    CountryRespondDto getById(Long id);

    List<CountryRespondDto> getAll(Pageable pageable);

    List<CountryRespondWithPhotoDto> getTenRandomCountries();

    void deleteByName(CountryDeleteRequestDto requestDto);

    CountryRespondDto updateCountry(CountryRequestDto requestDto, MultipartFile file, Long id);
}
