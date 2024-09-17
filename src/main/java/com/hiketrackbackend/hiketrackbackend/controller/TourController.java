package com.hiketrackbackend.hiketrackbackend.controller;

import com.hiketrackbackend.hiketrackbackend.dto.tour.TourRequestDto;
import com.hiketrackbackend.hiketrackbackend.dto.tour.TourRespondDto;
import com.hiketrackbackend.hiketrackbackend.dto.tour.TourRespondWithoutDetails;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/tours")
@RequiredArgsConstructor
@Validated
@Tag(name = "Tour Management", description = "Work with tour endpoints")
public class TourController {
    private final TourService tourService;

    @PreAuthorize("hasRole('GUIDE')")
    @PostMapping("/new")
    @Operation(summary = "Create a new tour", description = "Add new tour to DB")
    public TourRespondDto createTour(@RequestBody @Valid TourRequestDto requestDto) {
        return tourService.createTour(requestDto);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete tour by ID", description = "Delete tour with out actual "
           + "deleting it from DB(soft deleting). Tour become not visible for user")
    public void deleteTour(@PathVariable @Positive Long id) {
        tourService.deleteById(id);
    }

    @GetMapping
    @Operation(summary = "Get list of all tours")
    public List<TourRespondWithoutDetails> getAll(@ParameterObject @PageableDefault Pageable pageable) {
        return tourService.getAll(pageable);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get tour by id", description = "Get a single tour")
    public TourRespondDto getTourById(@PathVariable @Positive Long id) {
        return tourService.getById(id);
    }

    @GetMapping("/popular")
    @Operation(summary = "Get tour by likes",
            description = "Get a list of 7th tours with the most popular(rated) value")
    public List<TourRespondDto> getMostRatedTour() {
        return tourService.getByRating();
    }

    @GetMapping("/search")
    @Operation(summary = "Search by param",
            description = "Get list of all countries sorted by chosen parameter")
    public List<TourRespondDto> search(@Valid TourSearchParameters params,
                                       @ParameterObject @PageableDefault Pageable pageable) {
        return tourService.search(params, pageable);
    }
}
