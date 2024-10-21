package com.hiketrackbackend.hiketrackbackend.service;

import com.hiketrackbackend.hiketrackbackend.dto.country.*;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface CountryService {
    CountryRespondDto createCountry(String requestDto, MultipartFile file);

    List<CountryRespondDto> search(CountrySearchParameters params, Pageable pageable);

    CountryRespondDto getById(Long id);

    List<CountryRespondDto> getAll(Pageable pageable);

    List<CountryRespondWithPhotoDto> getTenRandomCountries();

    void deleteByName(CountryDeleteRequestDto requestDto);
}
