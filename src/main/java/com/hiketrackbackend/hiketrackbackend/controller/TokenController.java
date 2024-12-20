package com.hiketrackbackend.hiketrackbackend.controller;

import com.hiketrackbackend.hiketrackbackend.dto.user.login.UserResponseDto;
import com.hiketrackbackend.hiketrackbackend.security.AuthenticationService;
import com.hiketrackbackend.hiketrackbackend.security.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tokens")
@RequiredArgsConstructor
@Validated
@Tag(name = "Token", description = "Operations related to JWT token management.")
public class TokenController {
    private final JwtUtil jwtUtil;
    private final AuthenticationService authenticationService;

    @PostMapping("/refresh")
    @PreAuthorize("hasAnyRole('USER', 'GUIDE', 'ADMIN')")
    @Operation(
            summary = "Refresh JWT Token",
            description = "Refresh the JWT token for the authenticated user."
    )
    public UserResponseDto refreshJwtToken(HttpServletRequest request) {
        return jwtUtil.refreshToken(request);
    }

    @Operation(
            summary = "Get JWT token",
            description = "Get token after successful login via OAuth2"
    )
    @GetMapping("/access_token")
    public UserResponseDto getJwtToken(@RequestParam("token") String token) {
        return authenticationService.getToken(token);
    }
}
