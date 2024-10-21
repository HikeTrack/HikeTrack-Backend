package com.hiketrackbackend.hiketrackbackend.controller;

import com.hiketrackbackend.hiketrackbackend.dto.tour.*;
import com.hiketrackbackend.hiketrackbackend.model.user.User;
import com.hiketrackbackend.hiketrackbackend.service.TourService;
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

    // TODO set max 5 additional photo
    @PreAuthorize("hasAnyRole('GUIDE', 'ADMIN')")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "", description = "")
    public TourRespondWithoutReviews createTour(
            @RequestPart("requestDto") @Valid TourRequestDto requestDto,
            @RequestPart("mainPhoto") @Valid @ValidImageFileList MultipartFile mainPhoto,
            @RequestPart("additionalPhotos") @Valid @ValidImageFileList List<MultipartFile> additionalPhotos,
            Authentication authentication
    ) {
        User user = (User) authentication.getPrincipal();
        return tourService.createTour(requestDto, user, mainPhoto, additionalPhotos);
    }

    // TODO грузить фотки на авс сделать отдельный ендпоинт для обновления и удаления фото

    // в этом ендпоинте только обновление текстовой инфы без файтов
    @PreAuthorize("hasAnyRole('GUIDE', 'ADMIN')")
    @PatchMapping("/{userId}/{tourId}")
    @Operation(summary = "",
            description = "")
    public TourRespondWithoutReviews updateGeneralInfoAboutTour(
            @RequestBody @Valid TourUpdateRequestDto requestDto,
            @PathVariable @Positive Long userId,
            @PathVariable @Positive Long tourId
    ) {
        return tourService.updateTour(requestDto, userId, tourId);
    }

    @GetMapping("/{id}")
    @Operation(summary = "", description = "")
    public TourRespondDto getTourById(@PathVariable @Positive Long id,
                                      @RequestParam(defaultValue = "0") int page,
                                      @RequestParam(defaultValue = "5") int size) {
        return tourService.getById(id, page, size);
    }

    @GetMapping
    @Operation(summary = "")
    public List<TourRespondWithoutDetailsAndReviews> getAllTours(@ParameterObject @PageableDefault Pageable pageable) {
        return tourService.getAll(pageable);
    }

    @GetMapping("/popular")
    @Operation(summary = "",
            description = "")
    public List<TourRespondWithoutDetailsAndReviews> getMostRatedTours() {
        return tourService.getByRating();
    }

    @GetMapping("/search")
    @Operation(summary = "",
            description = "")
    public List<TourRespondWithoutDetailsAndReviews> searchTours(
            @Valid TourSearchParameters params,
            @ParameterObject @PageableDefault Pageable pageable
    ) {
        return tourService.search(params, pageable);
    }

    @PreAuthorize("hasAnyRole('GUIDE', 'ADMIN')")
    @DeleteMapping("/{id}")
    @Operation(summary = "", description = "")
    public void deleteTour(@PathVariable @Positive Long id) {
        tourService.deleteById(id);
    }
}
