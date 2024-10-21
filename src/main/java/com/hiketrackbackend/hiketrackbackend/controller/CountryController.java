package com.hiketrackbackend.hiketrackbackend.controller;

import com.hiketrackbackend.hiketrackbackend.dto.country.CountryDeleteRequestDto;
import com.hiketrackbackend.hiketrackbackend.dto.country.CountryRequestDto;
import com.hiketrackbackend.hiketrackbackend.dto.country.CountryRespondDto;
import com.hiketrackbackend.hiketrackbackend.dto.country.CountryRespondWithPhotoDto;
import com.hiketrackbackend.hiketrackbackend.dto.country.CountrySearchParameters;
import com.hiketrackbackend.hiketrackbackend.service.CountryService;
import com.hiketrackbackend.hiketrackbackend.validation.ValidImageFileList;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/countries")
@RequiredArgsConstructor
@Validated
@Tag(name = "", description = "")
public class CountryController {
    private final CountryService countryService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "", description = "")
    public CountryRespondDto createCountry(
            @RequestPart("requestDto") @Valid CountryRequestDto requestDto,
            @RequestPart("file") @Valid MultipartFile file
    ) {
        return countryService.createCountry(requestDto, file);
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
