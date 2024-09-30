package com.hiketrackbackend.hiketrackbackend.controller;

import com.hiketrackbackend.hiketrackbackend.dto.user.UserForgotRequestDto;
import com.hiketrackbackend.hiketrackbackend.dto.user.UserForgotRespondDto;
import com.hiketrackbackend.hiketrackbackend.dto.user.UserLoginRequestDto;
import com.hiketrackbackend.hiketrackbackend.dto.user.UserLoginResponseDto;
import com.hiketrackbackend.hiketrackbackend.dto.user.UserRegistrationRequestDto;
import com.hiketrackbackend.hiketrackbackend.dto.user.UserRegistrationRespondDto;
import com.hiketrackbackend.hiketrackbackend.dto.user.UserRestoreRequestDto;
import com.hiketrackbackend.hiketrackbackend.exception.RegistrationException;
import com.hiketrackbackend.hiketrackbackend.security.AuthenticationService;
import com.hiketrackbackend.hiketrackbackend.service.UserService;
import com.hiketrackbackend.hiketrackbackend.validation.ValidToken;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
@Validated
@Tag(name = "Authentication Management", description = "Registration and login functions")
public class AuthenticationController {
    private final UserService userService;
    private final AuthenticationService authenticationService;

    @Operation(summary = "Registration of a new user")
    @PostMapping("/registration")
    public UserRegistrationRespondDto registration(@RequestBody @Valid UserRegistrationRequestDto requestDto)
            throws RegistrationException {
        return userService.register(requestDto);
    }

    @Operation(summary = "User login", description = "Login to user`s account via username and password")
    @PostMapping("/login")
    public UserLoginResponseDto login(@RequestBody @Valid UserLoginRequestDto request) {
        return authenticationService.login(request);
    }

    @Operation(summary = "Request about forgotten password",
            description = "Create a request to users email to restore a password with link(expire in 1h)")
    @PostMapping("/forgot-password")
    public UserForgotRespondDto forgotPassword(@RequestBody @Valid UserForgotRequestDto request) {
        return userService.createRestoreRequest(request);
    }

    @Operation(summary = "Password restore",
            description = "Get token from user and validate it then update users password")
    @GetMapping("/reset-password")
    public String resetPassword(
            @ValidToken @RequestParam("token") String token,
            @RequestBody @Valid UserRestoreRequestDto request) {
        userService.updatePassword(request, token);
        return "Password successfully restored";
    }

    @PostMapping("/logout")
    public String logout(HttpServletRequest request,
                         Authentication authentication) {
        String email = authentication.getName();
        userService.logout(request, email);
        return "Logged out successfully";
    }
}
