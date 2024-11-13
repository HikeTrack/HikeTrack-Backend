package com.hiketrackbackend.hiketrackbackend.controller;

import com.hiketrackbackend.hiketrackbackend.dto.reviews.ReviewRequestDto;
import com.hiketrackbackend.hiketrackbackend.dto.reviews.ReviewsRespondDto;
import com.hiketrackbackend.hiketrackbackend.model.user.User;
import com.hiketrackbackend.hiketrackbackend.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
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
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/reviews")
@Validated
@Tag(name = "Reviews", description = "Operations related to managing user reviews for tours.")
public class ReviewController {
    private final ReviewService reviewService;

    @PreAuthorize("hasAnyRole('USER', 'GUIDE', 'ADMIN')")
    @PostMapping("/{tourId}")
    @Operation(summary = "Create Review",
            description = "Create a new review for a specific tour by the authenticated user.")
    public ReviewsRespondDto createReview(@RequestBody @Valid ReviewRequestDto requestDto,
                                          @PathVariable @Positive Long tourId,
                                          @AuthenticationPrincipal User user) {
        return reviewService.createReview(requestDto, user, tourId);
    }

    @PreAuthorize("hasAnyRole('USER', 'GUIDE', 'ADMIN') && #userId == authentication.principal.id")
    @PatchMapping("/{userId}/{reviewId}/{tourId}")
    @Operation(summary = "Update Review", description = "Update an existing review by its ID.")
    public ReviewsRespondDto updateReview(@RequestBody @Valid ReviewRequestDto requestDto,
                                          @PathVariable @Positive Long reviewId,
                                          @PathVariable @Positive Long tourId,
                                          @PathVariable @Positive Long userId) {
        return reviewService.updateReview(requestDto, reviewId, tourId, userId);
    }

    @GetMapping("user/{userId}")
    @PreAuthorize("hasAnyRole('GUIDE', 'USER', 'ADMIN') && #userId == authentication.principal.id")
    @Operation(
            summary = "Get User Reviews",
            description = "Retrieve all reviews created by a specific user.")
    public List<ReviewsRespondDto> getAllReviewsByUser(
            @PathVariable @Positive Long userId,
            Pageable pageable
    ) {
        return reviewService.getAllByUserId(userId, pageable);
    }

    @GetMapping("tour/{tourId}")
    @Operation(
            summary = "Get Tour Reviews",
            description = "Retrieve all reviews by a specific tour."
    )
    public List<ReviewsRespondDto> getAllReviewsByTour(
            @PathVariable @Positive Long tourId,
            Pageable pageable
    ) {
        return reviewService.getAllByTourId(tourId, pageable);
    }

    @PreAuthorize("hasAnyRole('USER', 'GUIDE', 'ADMIN') && #userId == authentication.principal.id")
    @DeleteMapping("/{userId}/{reviewId}/{tourId}")
    @Operation(summary = "Delete Review", description = "Delete a review by its ID.")
    public void deleteReview(@PathVariable @Positive Long reviewId,
                             @PathVariable @Positive Long userId,
                             @PathVariable @Positive Long tourId) {
        reviewService.deleteById(reviewId, userId, tourId);
    }
}
