package com.hiketrackbackend.hiketrackbackend.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hiketrackbackend.hiketrackbackend.dto.country.CountryDeleteRequestDto;
import com.hiketrackbackend.hiketrackbackend.dto.country.CountryRequestDto;
import com.hiketrackbackend.hiketrackbackend.dto.country.CountryRespondDto;
import com.hiketrackbackend.hiketrackbackend.dto.country.CountryRespondWithPhotoDto;
import com.hiketrackbackend.hiketrackbackend.dto.country.CountrySearchParameters;
import com.hiketrackbackend.hiketrackbackend.service.CountryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/countries")
@RequiredArgsConstructor
@Validated
@Tag(name = "", description = "")
public class CountryController {
    private final CountryService countryService;

//    @PreAuthorize("hasRole('ADMIN')")
//    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    @Operation(summary = "", description = "")
//    public CountryRespondDto createCountry(
//            // TODO
//            @RequestPart("data") @Valid CountryRequestDto data,
//            @RequestPart("file") MultipartFile file
//    ) {
//        return countryService.createCountry(data, file);
//    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Create a new country", description = "Allows administrators to create a new country entry, including related file upload.")
    public CountryRespondDto createCountry(
            @RequestPart("data") String dataString,
            @RequestPart("file") MultipartFile file
    ) {

        // Convert dataString to CountryRequestDto
        ObjectMapper objectMapper = new ObjectMapper();
        CountryRequestDto data;
        try {
            data = objectMapper.readValue(dataString, CountryRequestDto.class);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Invalid data format: " + e.getMessage());
        }

        if (!Objects.equals(file.getContentType(), "image/jpeg") && !Objects.equals(file.getContentType(), "image/png")) {
            throw new IllegalArgumentException("Only JPEG and PNG files are allowed");
        }
        if (file.getSize() > 5 * 1024 * 1024) { // 5MB limit
            throw new IllegalArgumentException("File size must not exceed 5MB");
        }

        return countryService.createCountry(data, file);
    }

    @GetMapping("/search")
    @Operation(summary = "",
            description = "")
    public List<CountryRespondDto> search(@Valid CountrySearchParameters params,
                                          @ParameterObject @PageableDefault Pageable pageable) {
        return countryService.search(params, pageable);
    }

    @GetMapping("/{id}")
    @Operation(summary = "",
            description = "")
    public CountryRespondDto getById(@PathVariable @Positive Long id) {
        return countryService.getById(id);
    }

    @GetMapping
    @Operation(summary = "",
            description = "")
    public List<CountryRespondDto> getAll(@ParameterObject @PageableDefault Pageable pageable) {
        return countryService.getAll(pageable);
    }

    @GetMapping("/random_ten")
    @Operation(summary = "",
            description = "")
    public List<CountryRespondWithPhotoDto> getTenRandomCountries() {
        return countryService.getTenRandomCountries();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping
    @Operation(summary = "",
            description = "")
    public void deleteByCountryName(@RequestBody CountryDeleteRequestDto requestDto) {
        countryService.deleteByName(requestDto);
    }
}
