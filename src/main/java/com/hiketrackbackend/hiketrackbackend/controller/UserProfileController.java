package com.hiketrackbackend.hiketrackbackend.controller;

import com.hiketrackbackend.hiketrackbackend.dto.userProfile.UserProfileRequestDto;
import com.hiketrackbackend.hiketrackbackend.dto.userProfile.UserProfileRespondDto;
import com.hiketrackbackend.hiketrackbackend.security.AuthenticationService;
import com.hiketrackbackend.hiketrackbackend.service.UserProfileService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/profile")
public class UserProfileController {
    private final UserProfileService userProfileService;
    private final AuthenticationService authenticationService;


    @PreAuthorize(("hasRole('USER')"))
    @GetMapping
    @Operation(summary = "Get user profile", description = "Get authorized user information")
    public UserProfileRespondDto getUserProfile(Authentication authentication) {
        Long userId = authenticationService.getUserId(authentication);
        return userProfileService.getById(userId);
    }

    @PreAuthorize(("hasRole('USER')"))
    @PutMapping
    @Operation(summary = "Get user profile", description = "Get authorized user information")
    public UserProfileRespondDto updateUserProfile(@RequestBody @Valid UserProfileRequestDto requestDto,
                                                   Authentication authentication) {
        Long userId = authenticationService.getUserId(authentication);
        return userProfileService.updateUserProfile(requestDto, userId);
    }
}
