package com.hiketrackbackend.hiketrackbackend.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hiketrackbackend.hiketrackbackend.dto.details.DetailsRespondDto;
import com.hiketrackbackend.hiketrackbackend.dto.tour.TourRequestDto;
import com.hiketrackbackend.hiketrackbackend.dto.tour.TourRespondDto;
import com.hiketrackbackend.hiketrackbackend.dto.tour.TourRespondWithoutDetailsAndReviews;
import com.hiketrackbackend.hiketrackbackend.dto.tour.TourRespondWithoutReviews;
import com.hiketrackbackend.hiketrackbackend.dto.tour.TourSearchParameters;
import com.hiketrackbackend.hiketrackbackend.dto.tour.TourUpdateRequestDto;
import com.hiketrackbackend.hiketrackbackend.model.user.User;
import com.hiketrackbackend.hiketrackbackend.service.TourDetailsService;
import com.hiketrackbackend.hiketrackbackend.service.TourService;
import com.hiketrackbackend.hiketrackbackend.validation.ValidImageFile;
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
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/tours")
@RequiredArgsConstructor
@Validated
@Tag(name = "Tour Management", description = "Work with tour endpoints")
public class TourController {
    private final TourService tourService;
    private final TourDetailsService tourDetailsService;
    private final ObjectMapper objectMapper;

    @PreAuthorize("hasAnyRole('GUIDE', 'ADMIN')")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "", description = "")
    public TourRespondWithoutReviews createTour(
            @RequestPart("data") String dataString,
            @RequestPart("mainPhoto") @Valid @ValidImageFile MultipartFile mainPhoto,
            @RequestPart("additionalPhotos") @Valid  List<@ValidImageFile MultipartFile> additionalPhotos,
            Authentication authentication
    ) {
        TourRequestDto requestDto;
        try {
            requestDto = objectMapper.readValue(dataString, TourRequestDto.class);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Invalid data format: " + e.getMessage());
        }
        User user = (User) authentication.getPrincipal();
        return tourService.createTour(requestDto, user, mainPhoto, additionalPhotos);
    }

    @PreAuthorize("hasAnyRole('GUIDE', 'ADMIN') && #userId == authentication.principal.id")
    @PatchMapping("/{tourId}/{userId}")
    @Operation(summary = "",
            description = "")
    public TourRespondWithoutReviews updateGeneralInfoAboutTour(
            @RequestBody @Valid TourUpdateRequestDto requestDto,
            @PathVariable @Positive Long userId,
            @PathVariable @Positive Long tourId
    ) {
        return tourService.updateTour(requestDto, userId, tourId);
    }

    @PreAuthorize("hasAnyRole('GUIDE', 'ADMIN') && #userId == authentication.principal.id")
    @PatchMapping("/{tourId}/photo/{userId}")
    @Operation(summary = "",
            description = "")
    public TourRespondWithoutReviews updateTourPhoto(
            @RequestPart("mainPhoto") @Valid @ValidImageFile MultipartFile mainPhoto,
            @PathVariable @Positive Long userId,
            @PathVariable @Positive Long tourId
    ) {
        return tourService.updateTourPhoto(mainPhoto, userId, tourId);
    }

    @PreAuthorize("hasAnyRole('GUIDE', 'ADMIN') && #userId == authentication.principal.id")
    @PatchMapping("/{tourId}/additionalPhotos/{userId}")
    @Operation(summary = "",
            description = "")
    public DetailsRespondDto updateTourDetailsPhotos(
            @RequestPart("additionalPhotos") @Valid List<@ValidImageFile MultipartFile> additionalPhotos,
            @PathVariable @Positive Long userId,
            @PathVariable @Positive Long tourId
    ) {
        return tourDetailsService.updateTourDetailsPhotos(additionalPhotos, userId, tourId);
    }

    @GetMapping("/{id}")
    @Operation(summary = "", description = "")
    public TourRespondDto getTourById(@PathVariable @Positive Long id,
                                      @RequestParam(defaultValue = "0") int page,
                                      @RequestParam(defaultValue = "5") int size) {
        return tourService.getById(id, page, size);
    }

    @GetMapping("/popular")
    @Operation(summary = "",
            description = "")
    public List<TourRespondWithoutDetailsAndReviews> getMostRatedTours() {
        return tourService.getByRating();
    }

    @GetMapping("/guide/{guideId}")
    @Operation(summary = "", description = "")
    public List<TourRespondWithoutDetailsAndReviews> getAllToursMadeByGuide(
            @PathVariable @Positive Long guideId,
            @ParameterObject @PageableDefault Pageable pageable
    ) {
        return tourService.getAllToursMadeByGuide(guideId, pageable);
    }

    @GetMapping
    @Operation(summary = "")
    public List<TourRespondWithoutDetailsAndReviews> getAllTours(@ParameterObject @PageableDefault Pageable pageable) {
        return tourService.getAll(pageable);
    }

    @GetMapping("/search")
    @Operation(summary = "",
            description = "")
    public List<TourRespondWithoutDetailsAndReviews> searchTours(@Valid TourSearchParameters params,
                                                                 @ParameterObject @PageableDefault Pageable pageable) {
        return tourService.search(params, pageable);
    }

    @PreAuthorize("hasAnyRole('GUIDE', 'ADMIN')")
    @DeleteMapping("/{tourId}/{userId}")
    @Operation(summary = "", description = "")
    public void deleteTour(@PathVariable @Positive Long tourId,
                           @PathVariable @Positive Long userId) {
        tourService.deleteTourByIdAndUserId(tourId, userId);
    }

    @PreAuthorize("hasAnyRole('GUIDE', 'ADMIN')")
    @DeleteMapping("/photo/{additionalPhotoId}")
    @Operation(summary = "", description = "")
    public void deleteSingleTourDetailsPhoto(@PathVariable @Positive Long additionalPhotoId) {
        tourService.deleteTourDetailsPhotoById(additionalPhotoId);
    }
}
