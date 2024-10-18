package com.hiketrackbackend.hiketrackbackend.service;

import com.hiketrackbackend.hiketrackbackend.dto.country.*;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface CountryService {
    CountryRespondDto createCountry(CountryRequestDto requestDto, List<MultipartFile> files);

    List<CountryRespondDto> search(CountrySearchParameters params, Pageable pageable);

    CountryRespondDto getById(Long id);

    List<CountryRespondDto> getAll(Pageable pageable);

    List<CountryRespondWithFilesDto> getTenRandomCountries();

    void deleteByName(CountryDeleteRequestDto requestDto);
}
