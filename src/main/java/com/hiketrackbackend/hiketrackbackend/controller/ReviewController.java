package com.hiketrackbackend.hiketrackbackend.controller;

import com.hiketrackbackend.hiketrackbackend.dto.reviews.ReviewsRespondDto;
import com.hiketrackbackend.hiketrackbackend.security.AuthenticationService;
import com.hiketrackbackend.hiketrackbackend.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
