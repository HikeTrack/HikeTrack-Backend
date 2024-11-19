package com.hiketrackbackend.hiketrackbackend.controller;

import com.hiketrackbackend.hiketrackbackend.dto.tour.details.DetailsRespondDto;
import com.hiketrackbackend.hiketrackbackend.dto.tour.details.file.TourDetailFileRespondDto;
import com.hiketrackbackend.hiketrackbackend.service.TourDetailsService;
import com.hiketrackbackend.hiketrackbackend.validation.ValidImageFile;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/tour_details")
@RequiredArgsConstructor
@Validated
@Tag(name = "Tour details Management", description = "Managing all exist details in tour")
public class TourDetailsController {
    private final TourDetailsService tourDetailsService;

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

    @Operation(summary = "Get tour additional photo",
            description = "Get a single additional tour photo by its ID")
    @GetMapping("/photo/{photoId}")
    public TourDetailFileRespondDto getTourDetailPhoto(
            @PathVariable @Positive Long photoId
    ) {
        return tourDetailsService.getTourDetailPhoto(photoId);
    }

    @Operation(summary = "Get all tour additional photo",
            description = "Get all photos of specified tour by his ID")
    @GetMapping("/all_detail_photos/{tourDetailId}")
    public Set<TourDetailFileRespondDto> getAllTourDetailPhotosByDetailId(
            @PathVariable @Positive Long tourDetailId
    ) {
        return tourDetailsService.getAllByTourDetail(tourDetailId);
    }

    @PreAuthorize("hasAnyRole('GUIDE', 'ADMIN')")
    @DeleteMapping("/additional_photo/{additionalPhotoId}")
    @Operation(summary = "Delete additional photo",
            description = "Delete a specific additional photo from a tour by its ID.")
    public void deleteSingleTourDetailsPhoto(@PathVariable @Positive Long additionalPhotoId) {
        tourDetailsService.deleteTourDetailsPhotoById(additionalPhotoId);
    }
}
