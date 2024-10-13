package com.hiketrackbackend.hiketrackbackend.controller;

import com.hiketrackbackend.hiketrackbackend.dto.reviews.ReviewRequestDto;
import com.hiketrackbackend.hiketrackbackend.dto.reviews.ReviewsRespondDto;
import com.hiketrackbackend.hiketrackbackend.model.User;
import com.hiketrackbackend.hiketrackbackend.security.AuthenticationService;
import com.hiketrackbackend.hiketrackbackend.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/profile/reviews")
public class ReviewController {
    private final ReviewService reviewService;
    private final AuthenticationService authenticationService;
    
    @GetMapping
    @PreAuthorize("hasAnyRole('GUIDE', 'USER', 'ADMIN')")
    public List<ReviewsRespondDto> getAllReviewsByUser(Authentication authentication, Pageable pageable) {
        Long userId = authenticationService.getUserId(authentication);
        return reviewService.getAllByUserId(userId, pageable);
    }

    // TODO change to patch mapping
    // TODO Change path
    @PreAuthorize("hasAnyRole('USER', 'GUIDE', 'ADMIN')")
    @PutMapping("/{tourId}/reviews/{reviewId}")
    @Operation(summary = "Update review",
            description = "Update review for authorized user with tour id")
    public ReviewsRespondDto updateReview(@RequestBody @Valid ReviewRequestDto requestDto,
                                          @PathVariable @Positive Long tourId,
                                          @PathVariable @Positive Long reviewId) {
        return reviewService.updateReview(requestDto, tourId, reviewId);
    }

    // TODO CHANGE PATH
    @PreAuthorize("hasAnyRole('USER', 'GUIDE', 'ADMIN')")
    @DeleteMapping("/{tourId}/reviews/{reviewId}")
    @Operation(summary = "Delete review by ID",
            description = "Delete review from DB with registered user by review ID adn tour ID")
    public void deleteReview( @PathVariable @Positive Long tourId,
                              @PathVariable @Positive Long reviewId) {
        reviewService.deleteById(reviewId, tourId);
    }

    // TODO CHANGE PATH
    @PreAuthorize("hasAnyRole('USER', 'GUIDE', 'ADMIN')")
    @PostMapping("/{tourId}/reviews")
    @Operation(summary = "Create a new review", description = "Save new registered users review")
    public ReviewsRespondDto createReview(@RequestBody @Valid ReviewRequestDto requestDto,
                                          @PathVariable @Positive Long tourId,
                                          Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return reviewService.createReview(requestDto, user, tourId);
    }
}
