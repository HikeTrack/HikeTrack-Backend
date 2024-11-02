package com.hiketrackbackend.hiketrackbackend.controller;

import com.hiketrackbackend.hiketrackbackend.dto.rating.RatingRequestDto;
import com.hiketrackbackend.hiketrackbackend.dto.rating.RatingRespondDto;
import com.hiketrackbackend.hiketrackbackend.service.RatingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rating")
@Validated
@RequiredArgsConstructor
@Tag(name = "Ratings", description = "Operations related to managing user ratings for tours.")
public class RatingController {
    private final RatingService ratingService;

    @PreAuthorize("hasAnyRole('USER', 'GUIDE', 'ADMIN') && #userId == authentication.principal.id")
    @PatchMapping("/{userId}/{tourId}")
    @Operation(summary = "Update Rating",
            description = "Update the rating for a specific tour by the authenticated user.")
    public RatingRespondDto updateRating(@RequestBody @Valid RatingRequestDto requestDto,
                                         @PathVariable @Positive Long userId,
                                         @PathVariable @Positive Long tourId) {
        return ratingService.updateRating(requestDto, userId, tourId);
    }
}
