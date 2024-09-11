package com.hiketrackbackend.hiketrackbackend.controller;

import com.hiketrackbackend.hiketrackbackend.dto.tour.TourRequestDto;
import com.hiketrackbackend.hiketrackbackend.dto.tour.TourRespondDto;
import com.hiketrackbackend.hiketrackbackend.service.TourService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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


}
