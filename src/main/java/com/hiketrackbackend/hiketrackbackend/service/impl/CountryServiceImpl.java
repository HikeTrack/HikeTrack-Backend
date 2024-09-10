package com.hiketrackbackend.hiketrackbackend.service.impl;

import com.hiketrackbackend.hiketrackbackend.dto.country.CountrySearchParameters;
import com.hiketrackbackend.hiketrackbackend.dto.country.RequestCountryDto;
import com.hiketrackbackend.hiketrackbackend.dto.country.RespondCountryDto;
import com.hiketrackbackend.hiketrackbackend.mapper.CountryMapper;
import com.hiketrackbackend.hiketrackbackend.model.Country;
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
    public RespondCountryDto createCountry(RequestCountryDto requestDto) {
        Country country = countryMapper.toEntity(requestDto);
        return countryMapper.toDto(countryRepository.save(country));
    }

    @Override
    public List<RespondCountryDto> search(CountrySearchParameters params, Pageable pageable) {
        Specification<Country> countrySpecification = countrySpecificationBuilder.build(params);
        return countryRepository.findAll(countrySpecification, pageable)
                .stream()
                .map(countryMapper::toDto)
                .toList();
    }
}
