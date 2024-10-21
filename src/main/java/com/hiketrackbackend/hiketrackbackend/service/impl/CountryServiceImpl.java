package com.hiketrackbackend.hiketrackbackend.service.impl;

import com.hiketrackbackend.hiketrackbackend.dto.country.CountryDeleteRequestDto;
import com.hiketrackbackend.hiketrackbackend.dto.country.CountryRequestDto;
import com.hiketrackbackend.hiketrackbackend.dto.country.CountryRespondDto;
import com.hiketrackbackend.hiketrackbackend.dto.country.CountryRespondWithPhotoDto;
import com.hiketrackbackend.hiketrackbackend.dto.country.CountrySearchParameters;
import com.hiketrackbackend.hiketrackbackend.exception.AASdasdExeption;
import com.hiketrackbackend.hiketrackbackend.exception.EntityNotFoundException;
import com.hiketrackbackend.hiketrackbackend.mapper.CountryMapper;
import com.hiketrackbackend.hiketrackbackend.model.country.Country;
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

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CountryServiceImpl implements CountryService {
    private static final String FOLDER_NAME = "country";
    private static final int FIRST_ELEMENT = 0;
    private final CountryRepository countryRepository;
    private final CountryMapper countryMapper;
    private final CountrySpecificationBuilder countrySpecificationBuilder;
    private final FileStorageService s3Service;

    @Override
    @Transactional
    public CountryRespondDto createCountry(CountryRequestDto requestDto, MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new AASdasdExeption("Country photo cannot be empty of null");
        }
        Country country = countryMapper.toEntity(requestDto);
        String photoUrl = saveFile(file);
        country.setPhoto(photoUrl);
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

    @Override
    public List<CountryRespondWithPhotoDto> getTenRandomCountries() {
        return countryMapper.toDto(countryRepository.findTenRandomCountry());
    }

    @Override
    public void deleteByName(CountryDeleteRequestDto requestDto) {
        countryRepository.delete(findCountryByName(requestDto.getName()));
    }

    private String saveFile(MultipartFile file) {
        List<MultipartFile> files = new ArrayList<>();
        files.add(file);
        List<String> urls = s3Service.uploadFileToS3(FOLDER_NAME, files);
        return urls.get(FIRST_ELEMENT);
    }

    private Country findCountryByName(String name) {
        return countryRepository.findCountryByName(name).orElseThrow(
                () -> new EntityNotFoundException("Could not find country with name: " + name)
        );
    }
}
