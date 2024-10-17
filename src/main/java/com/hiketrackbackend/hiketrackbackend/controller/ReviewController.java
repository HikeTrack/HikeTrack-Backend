package com.hiketrackbackend.hiketrackbackend.controller;

import com.hiketrackbackend.hiketrackbackend.dto.reviews.ReviewRequestDto;
import com.hiketrackbackend.hiketrackbackend.dto.reviews.ReviewsRespondDto;
import com.hiketrackbackend.hiketrackbackend.model.User;
import com.hiketrackbackend.hiketrackbackend.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/reviews")
@Validated
public class ReviewController {
    private final ReviewService reviewService;

    // TODO сделать что бы в ревью можно было загружать фотки и собстаенно грузить их на авс тоже и с апдейт
    /*
        получаем все ревью определенного юзера в его профиле
     */
    @PreAuthorize("hasAnyRole('USER', 'GUIDE', 'ADMIN')")
    @PostMapping("/{tourId}")
    @Operation(summary = "", description = "")
    public ReviewsRespondDto createReview(@RequestBody @Valid ReviewRequestDto requestDto,
                                          @PathVariable @Positive Long tourId,
                                          Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return reviewService.createReview(requestDto, user, tourId);
    }

    @PreAuthorize("hasAnyRole('USER', 'GUIDE', 'ADMIN')")
    @PatchMapping("/{reviewId}")
    @Operation(summary = "",
            description = "")
    public ReviewsRespondDto updateReview(@RequestBody @Valid ReviewRequestDto requestDto,
                                          @PathVariable @Positive Long reviewId) {
        return reviewService.updateReview(requestDto, reviewId);
    }

    @GetMapping("/{userId}")
    @PreAuthorize("hasAnyRole('GUIDE', 'USER', 'ADMIN')")
    @Operation(summary = "",
            description = "")
    public List<ReviewsRespondDto> getAllReviewsByUser(@PathVariable @Positive Long userId, Pageable pageable) {
        return reviewService.getAllByUserId(userId, pageable);
    }

    @PreAuthorize("hasAnyRole('USER', 'GUIDE', 'ADMIN')")
    @DeleteMapping("/{reviewId}")
    @Operation(summary = "",
            description = "")
    public void deleteReview(@PathVariable @Positive Long reviewId) {
        reviewService.deleteById(reviewId);
    }
}
