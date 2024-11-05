package com.hiketrackbackend.hiketrackbackend.controller;

import com.hiketrackbackend.hiketrackbackend.dto.user.login.UserResponseDto;
import com.hiketrackbackend.hiketrackbackend.security.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/token")
@RequiredArgsConstructor
@Validated
@Tag(name = "Token", description = "Operations related to JWT token management.")
public class TokenController {
    private final JwtUtil jwtUtil;

    @PostMapping
    @PreAuthorize("hasAnyRole('USER', 'GUIDE', 'ADMIN')")
    @Operation(
            summary = "Refresh JWT Token",
            description = "Refresh the JWT token for the authenticated user."
    )
    public UserResponseDto refreshJwtToken(HttpServletRequest request) {
        return jwtUtil.refreshToken(request);
    }
}
