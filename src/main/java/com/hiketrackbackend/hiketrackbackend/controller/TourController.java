package com.hiketrackbackend.hiketrackbackend.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hiketrackbackend.hiketrackbackend.dto.tour.TourRequestDto;
import com.hiketrackbackend.hiketrackbackend.dto.tour.TourRespondDto;
import com.hiketrackbackend.hiketrackbackend.dto.tour.TourRespondWithoutDetailsAndReviews;
import com.hiketrackbackend.hiketrackbackend.dto.tour.TourRespondWithoutDetailsAndReviewsAndRating;
import com.hiketrackbackend.hiketrackbackend.dto.tour.TourRespondWithoutReviews;
import com.hiketrackbackend.hiketrackbackend.dto.tour.TourSearchParameters;
import com.hiketrackbackend.hiketrackbackend.dto.tour.TourUpdateRequestDto;
import com.hiketrackbackend.hiketrackbackend.dto.tour.details.DetailsRespondDto;
import com.hiketrackbackend.hiketrackbackend.model.user.User;
import com.hiketrackbackend.hiketrackbackend.service.TourDetailsService;
import com.hiketrackbackend.hiketrackbackend.service.TourService;
import com.hiketrackbackend.hiketrackbackend.validation.ValidImageFile;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/tours")
@RequiredArgsConstructor
@Validated
@Tag(name = "Tour Management", description = "Endpoints for managing tours")
public class TourController {
    private final TourService tourService;
    private final TourDetailsService tourDetailsService;
    private final ObjectMapper objectMapper;

    @PreAuthorize("hasAnyRole('GUIDE', 'ADMIN')")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
            summary = "Create a new tour",
            description = "Create a new tour with the provided details,"
                    + "main photo, and additional photos."
    )
    public TourRespondWithoutReviews createTour(
            @RequestPart("data") String dataString,
            @RequestPart("mainPhoto") @Valid @ValidImageFile MultipartFile mainPhoto,
            @RequestPart("additionalPhotos") @Valid
            List<@ValidImageFile MultipartFile> additionalPhotos,
            @AuthenticationPrincipal User user
    ) {
        TourRequestDto requestDto;
        try {
            requestDto = objectMapper.readValue(dataString, TourRequestDto.class);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Invalid data format: " + e.getMessage());
        }
        return tourService.createTour(requestDto, user, mainPhoto, additionalPhotos);
    }

    @PreAuthorize("hasAnyRole('GUIDE', 'ADMIN') && #userId == authentication.principal.id")
    @PatchMapping("/{tourId}/{userId}")
    @Operation(
            summary = "Update tour information",
            description = "Update the general information of an existing tour.")
    public TourRespondWithoutReviews updateGeneralInfoAboutTour(
            @RequestBody @Valid TourUpdateRequestDto requestDto,
            @PathVariable @Positive Long userId,
            @PathVariable @Positive Long tourId
    ) {
        return tourService.updateTour(requestDto, userId, tourId);
    }

    @PreAuthorize("hasAnyRole('GUIDE', 'ADMIN') && #userId == authentication.principal.id")
    @PatchMapping("/{tourId}/photo/{userId}")
    @Operation(
            summary = "Update tour main photo",
            description = "Update the main photo of an existing tour.")
    public TourRespondWithoutReviews updateTourPhoto(
            @RequestPart("mainPhoto") @Valid @ValidImageFile MultipartFile mainPhoto,
            @PathVariable @Positive Long userId,
            @PathVariable @Positive Long tourId
    ) {
        return tourService.updateTourPhoto(mainPhoto, userId, tourId);
    }

    @PreAuthorize("hasAnyRole('GUIDE', 'ADMIN') && #userId == authentication.principal.id")
    @PatchMapping("/{tourId}/additionalPhotos/{userId}")
    @Operation(summary = "Update tour additional photos",
            description = "Update the additional photos of an existing tour.")
    public DetailsRespondDto updateTourDetailsPhotos(
            @RequestPart("additionalPhotos") @Valid
            List<@ValidImageFile MultipartFile> additionalPhotos,
            @PathVariable @Positive Long userId,
            @PathVariable @Positive Long tourId
    ) {
        return tourDetailsService.updateTourDetailsPhotos(additionalPhotos, userId, tourId);
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get tour by ID",
            description = "Retrieve the details of a tour by its ID."
    )
    public TourRespondDto getTourById(@PathVariable @Positive Long id,
                                      @RequestParam(defaultValue = "0") int page,
                                      @RequestParam(defaultValue = "5") int size) {
        return tourService.getById(id, page, size);
    }

    @GetMapping("/popular")
    @Operation(
            summary = "Get most rated tours",
            description = "Retrieve a list of the most highly rated tours.")
    public List<TourRespondWithoutDetailsAndReviews> getMostRatedTours() {
        return tourService.getByRating();
    }

    @GetMapping("/guide/{guideId}")
    @Operation(
            summary = "Get tours by guide",
            description = "Retrieve all tours created by a specific guide.")
    public List<TourRespondWithoutDetailsAndReviews> getAllToursMadeByGuide(
            @PathVariable @Positive Long guideId,
            @ParameterObject @PageableDefault Pageable pageable
    ) {
        return tourService.getAllToursMadeByGuide(guideId, pageable);
    }

    @GetMapping
    @Operation(summary = "Get all tours", description = "Retrieve a paginated list of all tours.")
    public List<TourRespondWithoutDetailsAndReviewsAndRating> getAllTours(
            @ParameterObject @PageableDefault Pageable pageable
    ) {
        return tourService.getAll(pageable);
    }

    @GetMapping("/search")
    @Operation(
            summary = "Search tours",
            description = "Search for tours based on various parameters."
    )
    public List<TourRespondWithoutDetailsAndReviewsAndRating> searchTours(
            @Valid TourSearchParameters params,
            @ParameterObject @PageableDefault Pageable pageable
    ) {
        return tourService.search(params, pageable);
    }

    @PreAuthorize("hasAnyRole('GUIDE', 'ADMIN') && #userId == authentication.principal.id")
    @DeleteMapping("/{tourId}/{userId}")
    @Operation(
            summary = "Delete tour",
            description = "Delete a tour by its ID and the user ID of the guide or admin."
    )
    public void deleteTour(@PathVariable @Positive Long tourId,
                           @PathVariable @Positive Long userId) {
        tourService.deleteTourByIdAndUserId(tourId, userId);
    }

    @PreAuthorize("hasAnyRole('GUIDE', 'ADMIN')")
    @DeleteMapping("/additional_photo/{additionalPhotoId}")
    @Operation(summary = "Delete additional photo",
            description = "Delete a specific additional photo from a tour by its ID.")
    public void deleteSingleTourDetailsPhoto(@PathVariable @Positive Long additionalPhotoId) {
        tourService.deleteTourDetailsPhotoById(additionalPhotoId);
    }
}
