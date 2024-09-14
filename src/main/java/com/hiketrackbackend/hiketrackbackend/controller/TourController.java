package com.hiketrackbackend.hiketrackbackend.controller;

import com.hiketrackbackend.hiketrackbackend.dto.country.CountryRespondDto;
import com.hiketrackbackend.hiketrackbackend.dto.country.CountrySearchParameters;
import com.hiketrackbackend.hiketrackbackend.dto.tour.TourRequestDto;
import com.hiketrackbackend.hiketrackbackend.dto.tour.TourRespondDto;
import com.hiketrackbackend.hiketrackbackend.dto.tour.TourSearchParameters;
import com.hiketrackbackend.hiketrackbackend.service.TourService;
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
@RequestMapping("/tours")
@RequiredArgsConstructor
@Validated
@Tag(name = "Tour Management", description = "Work with tour endpoints")
public class TourController {
    private final TourService tourService;

    //    @PreAuthorize("hasRole('ADMIN')") add after adding auth feature
    @PostMapping("/new")
    @Operation(summary = "Create a new tour", description = "Add new tour to DB")
    public TourRespondDto createTour(@RequestBody @Valid TourRequestDto requestDto) {
        return tourService.createTour(requestDto);
    }

//    @PreAuthorize("hasRole('ADMIN')")add after adding auth feature
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete tour by ID", description = "Delete tour with out actual "
           + "deleting it from DB(soft deleting). Tour become not visible for user")
    public void deleteTour(@PathVariable @Positive Long id) {
        tourService.deleteById(id);
    }

//    @PreAuthorize("hasRole('USER')") add after adding auth feature
    @GetMapping
    @Operation(summary = "Get list of all tours")
    public List<TourRespondDto> getAll(@ParameterObject @PageableDefault Pageable pageable) {
        return tourService.getAll(pageable);
    }

//    @PreAuthorize("hasRole('USER')") add after adding auth feature
    @GetMapping("/{id}")
    @Operation(summary = "Get tour by id", description = "Get a single tour")
    public TourRespondDto getTourById(@PathVariable @Positive Long id) {
        return tourService.getById(id);
    }

    //    @PreAuthorize("hasAnyRole('USER')") add after adding auth feature
    @GetMapping("/search")
    @Operation(summary = "Search by param",
            description = "Get list of all countries sorted by chosen parameter")
    public List<TourRespondDto> search(@Valid TourSearchParameters params,
                                          @ParameterObject @PageableDefault Pageable pageable) {
        return tourService.search(params, pageable);
    }


}
