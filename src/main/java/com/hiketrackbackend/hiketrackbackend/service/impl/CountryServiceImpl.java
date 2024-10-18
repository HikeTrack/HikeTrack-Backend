package com.hiketrackbackend.hiketrackbackend.service.impl;

import com.hiketrackbackend.hiketrackbackend.dto.country.CountryRespondDto;
import com.hiketrackbackend.hiketrackbackend.dto.country.CountrySearchParameters;
import com.hiketrackbackend.hiketrackbackend.dto.country.CountryRequestDto;
import com.hiketrackbackend.hiketrackbackend.exception.EntityNotFoundException;
import com.hiketrackbackend.hiketrackbackend.mapper.CountryMapper;
import com.hiketrackbackend.hiketrackbackend.model.country.Country;
import com.hiketrackbackend.hiketrackbackend.model.country.CountryFile;
import com.hiketrackbackend.hiketrackbackend.repository.country.CountryRepository;
import com.hiketrackbackend.hiketrackbackend.repository.country.CountrySpecificationBuilder;
import com.hiketrackbackend.hiketrackbackend.service.CountryService;
import com.hiketrackbackend.hiketrackbackend.service.files.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CountryServiceImpl implements CountryService {
    private static final String FOLDER_NAME = "country";
    private final CountryRepository countryRepository;
    private final CountryMapper countryMapper;
    private final CountrySpecificationBuilder countrySpecificationBuilder;
    private final FileStorageService s3Service;

    @Override
    @Transactional
    public CountryRespondDto createCountry(CountryRequestDto requestDto, List<MultipartFile> files) {
        if (files == null || files.isEmpty()) {
            throw new RuntimeException("Country photo cannot be empty of null");
        }
        Country country = countryMapper.toEntity(requestDto);
        List<CountryFile> countryFiles = saveCountryFile(files, country);
        country.setPhotos(countryFiles);
        return countryMapper.toDto(countryRepository.save(country));
    }

    @Override
    public CountryRespondDto getById(Long id) {
        Country country = countryRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Can`t find country with id: " + id)
        );
        return countryMapper.toDto(country);
    }

    @Override
    public List<CountryRespondDto> search(CountrySearchParameters params, Pageable pageable) {
        Specification<Country> countrySpecification = countrySpecificationBuilder.build(params);
        return countryRepository.findAll(countrySpecification, pageable)
                .stream()
                .map(countryMapper::toDto)
                .toList();
    }

    @Override
    public List<CountryRespondDto> getAll(Pageable pageable) {
        return countryRepository.findAll()
                .stream()
                .map(countryMapper::toDto)
                .toList();
    }

    protected List<CountryFile> saveCountryFile(List<MultipartFile> files, Country country) {
        List<CountryFile> countryFiles = new ArrayList<>();
        List<String> urls = s3Service.uploadFile(FOLDER_NAME, files);
        for (String url : urls) {
            CountryFile countryFile = new CountryFile();
            countryFile.setFileUrl(url);
            countryFile.setCountry(country);
            countryFile.setCreatedAt(LocalDateTime.now());
            countryFiles.add(countryFile);
        }
        return countryFiles;
    }
}
