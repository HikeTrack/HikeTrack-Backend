package com.hiketrackbackend.hiketrackbackend.controller;

import com.hiketrackbackend.hiketrackbackend.dto.country.CountrySearchParameters;
import com.hiketrackbackend.hiketrackbackend.dto.country.CountryRespondDto;
import com.hiketrackbackend.hiketrackbackend.dto.country.CountryRequestDto;
import com.hiketrackbackend.hiketrackbackend.service.CountryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/countries")
@RequiredArgsConstructor
@Validated
@Tag(name = "Country Management", description = "Endpoints for managing country endpoints")
public class CountryController {
    private final CountryService countryService;

//    @PreAuthorize("hasRole('ADMIN')") add after adding auth feature
    @PostMapping("/new")
    @Operation(summary = "Create a new country", description = "Add new country to DB")
    public CountryRespondDto createCountry(@RequestBody @Valid CountryRequestDto requestDto) {
        return countryService.createCountry(requestDto);
    }

//    @PreAuthorize("hasAnyRole('USER')") add after adding auth feature
    @GetMapping("/search")
    @Operation(summary = "Search by param",
            description = "Get list of all countries sorted by chosen continent or country")
    public List<CountryRespondDto> search(@Valid CountrySearchParameters params,
                                          @ParameterObject @PageableDefault Pageable pageable) {
        return countryService.search(params, pageable);
    }

    //    @PreAuthorize("hasAnyRole('USER')") add after adding auth feature
    @GetMapping("/{id}")
    @Operation(summary = "Get country by ID",
            description = "Get get country with ID")
    public CountryRespondDto getById(@PathVariable @Positive Long id) {
        return countryService.getById(id);
    }
}
