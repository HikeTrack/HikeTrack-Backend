package com.hiketrackbackend.hiketrackbackend.service.impl;

import com.hiketrackbackend.hiketrackbackend.dto.country.CountryRespondDto;
import com.hiketrackbackend.hiketrackbackend.dto.country.CountrySearchParameters;
import com.hiketrackbackend.hiketrackbackend.dto.country.CountryRequestDto;
import com.hiketrackbackend.hiketrackbackend.exception.EntityNotFoundException;
import com.hiketrackbackend.hiketrackbackend.mapper.CountryMapper;
import com.hiketrackbackend.hiketrackbackend.model.country.Country;
import com.hiketrackbackend.hiketrackbackend.repository.country.CountryRepository;
import com.hiketrackbackend.hiketrackbackend.repository.country.CountrySpecificationBuilder;
import com.hiketrackbackend.hiketrackbackend.service.CountryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CountryServiceImpl implements CountryService {
    private final CountryRepository countryRepository;
    private final CountryMapper countryMapper;
    private final CountrySpecificationBuilder countrySpecificationBuilder;

    @Override
    public CountryRespondDto createCountry(CountryRequestDto requestDto) {
        Country country = countryMapper.toEntity(requestDto);
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
}
