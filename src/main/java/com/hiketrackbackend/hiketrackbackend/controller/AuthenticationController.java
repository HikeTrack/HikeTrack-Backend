package com.hiketrackbackend.hiketrackbackend.controller;

import com.hiketrackbackend.hiketrackbackend.dto.user.UserRequestDto;
import com.hiketrackbackend.hiketrackbackend.dto.user.login.UserLoginRequestDto;
import com.hiketrackbackend.hiketrackbackend.dto.user.login.UserResponseDto;
import com.hiketrackbackend.hiketrackbackend.dto.user.UserDevMsgRespondDto;
import com.hiketrackbackend.hiketrackbackend.dto.user.registration.UserRegistrationRequestDto;
import com.hiketrackbackend.hiketrackbackend.dto.user.registration.UserRegistrationRespondDto;
import com.hiketrackbackend.hiketrackbackend.dto.user.update.UserUpdatePasswordRequestDto;
import com.hiketrackbackend.hiketrackbackend.exception.RegistrationException;
import com.hiketrackbackend.hiketrackbackend.security.AuthenticationService;
import com.hiketrackbackend.hiketrackbackend.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/*
    Do not send requests with JWT token.
    This path is in EXCLUDE_URLS to skip token validation.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
@Validated
@Tag(name = "", description = "")
public class AuthenticationController {
    private final UserService userService;
    private final AuthenticationService authenticationService;

    @Operation(summary = "")
    @PostMapping("/registration")
    public UserRegistrationRespondDto registration(@RequestBody @Valid UserRegistrationRequestDto requestDto)
            throws RegistrationException {
        return userService.register(requestDto);
    }

    @Operation(summary = "", description = "")
    @PostMapping("/login")
    public UserResponseDto login(@RequestBody @Valid UserLoginRequestDto request) {
        return authenticationService.login(request);
    }

    @Operation(summary = "",
            description = "")
    @PostMapping("/forgot-password")
    public UserDevMsgRespondDto forgotPassword(@RequestBody @Valid UserRequestDto request) {
        return authenticationService.createRestoreRequest(request);
    }

    @Operation(summary = "",
            description = "")
    @PostMapping("/reset-password")
    public UserDevMsgRespondDto resetPassword(@RequestParam("token") String token,
                                              @RequestBody @Valid UserUpdatePasswordRequestDto request) {
        return authenticationService.restorePassword(token, request);
    }
}
