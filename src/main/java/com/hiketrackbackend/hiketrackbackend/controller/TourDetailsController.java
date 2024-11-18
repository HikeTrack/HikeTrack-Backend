package com.hiketrackbackend.hiketrackbackend.controller;

import com.hiketrackbackend.hiketrackbackend.dto.tour.details.DetailsRespondDto;
import com.hiketrackbackend.hiketrackbackend.dto.tour.details.file.TourDetailFileRespondDto;
import com.hiketrackbackend.hiketrackbackend.model.tour.details.TourDetails;
import com.hiketrackbackend.hiketrackbackend.service.TourDetailsService;
import com.hiketrackbackend.hiketrackbackend.validation.ValidImageFile;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@RestController
@RequestMapping("/tour_details")
@RequiredArgsConstructor
@Validated
@Tag(name = "Tour details Management", description = "Managing all exist details in tour")
public class TourDetailsController {
    private TourDetailsService tourDetailsService;

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

    @GetMapping("/{tourDetailsId}/single_photo")
    public TourDetailFileRespondDto getRandomSingleTourDetailPhoto(
            @PathVariable @Positive Long tourDetailsId
    ) {
        return tourDetailsService.getSinglePhoto(tourDetailsId);
    }
}
