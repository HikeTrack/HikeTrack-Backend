package com.hiketrackbackend.hiketrackbackend.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hiketrackbackend.hiketrackbackend.dto.country.CountryDeleteRequestDto;
import com.hiketrackbackend.hiketrackbackend.dto.country.CountryRequestDto;
import com.hiketrackbackend.hiketrackbackend.dto.country.CountryRespondDto;
import com.hiketrackbackend.hiketrackbackend.dto.country.CountryRespondWithPhotoDto;
import com.hiketrackbackend.hiketrackbackend.dto.country.CountrySearchParameters;
import com.hiketrackbackend.hiketrackbackend.service.CountryService;
import com.hiketrackbackend.hiketrackbackend.validation.ValidImageFile;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@RestController
@RequestMapping("/countries")
@RequiredArgsConstructor
@Validated
@Tag(name = "Country Management", description = "Endpoints for managing countries")
public class CountryController {
    private final CountryService countryService;
    private final ObjectMapper objectMapper;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Create a new country",
            description = "Allows administrators to create a new country entry, including related file upload.")
    public CountryRespondDto createCountry(
            @RequestPart("data") String dataString,
            @RequestPart("file") @ValidImageFile MultipartFile file
    ) {
        CountryRequestDto data;
        try {
            data = objectMapper.readValue(dataString, CountryRequestDto.class);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Invalid data format: " + e.getMessage());
        }
        return countryService.createCountry(data, file);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Update country",
            description = "Allows administrators to update country entry, including related file upload.")
    public CountryRespondDto updateCountry(
            @PathVariable @Positive Long id,
            @RequestPart("data") String dataString,
            @RequestPart("file") @ValidImageFile MultipartFile file
    ) {
        CountryRequestDto requestDto;
        try {
            requestDto = objectMapper.readValue(dataString, CountryRequestDto.class);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Invalid data format: " + e.getMessage());
        }
        return countryService.updateCountry(requestDto, file, id);
    }

    @GetMapping("/search")
    @Operation(summary = "Search countries", description = "Search for countries based on various parameters.")
    public List<CountryRespondDto> search(@Valid CountrySearchParameters params,
                                          @ParameterObject @PageableDefault Pageable pageable) {
        return countryService.search(params, pageable);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get country by ID", description = "Retrieve the details of a country by its ID.")
    public CountryRespondDto getById(@PathVariable @Positive Long id) {
        return countryService.getById(id);
    }

    @GetMapping
    @Operation(summary = "Get all countries", description = "Retrieve a paginated list of all countries.")
    public List<CountryRespondDto> getAll(@ParameterObject @PageableDefault Pageable pageable) {
        return countryService.getAll(pageable);
    }

    @GetMapping("/random_ten")
    @Operation(summary = "Get ten random countries", description = "Retrieve a list of ten random countries.")
    public List<CountryRespondWithPhotoDto> getTenRandomCountries() {
        return countryService.getTenRandomCountries();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping
    @Operation(summary = "Delete country by name",
            description = "Allows administrators to delete a country by its name.")
    public void deleteByCountryName(@RequestBody CountryDeleteRequestDto requestDto) {
        countryService.deleteByName(requestDto);
    }
}
