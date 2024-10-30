package com.hiketrackbackend.hiketrackbackend.controller;

import com.hiketrackbackend.hiketrackbackend.dto.user.UserRequestDto;
import com.hiketrackbackend.hiketrackbackend.dto.user.login.UserLoginRequestDto;
import com.hiketrackbackend.hiketrackbackend.dto.user.login.UserResponseDto;
import com.hiketrackbackend.hiketrackbackend.dto.UserDevMsgRespondDto;
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

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
@Validated
@Tag(name = "Authentication", description = "Operations related to user authentication such as registration, "
        + "login, and password management.")
public class AuthenticationController {
    private final UserService userService;
    private final AuthenticationService authenticationService;

    @Operation(summary = "User Registration",
            description = "Register a new user with the provided details and send confirmation email.")
    @PostMapping("/registration")
    public UserRegistrationRespondDto registration(@RequestBody @Valid UserRegistrationRequestDto requestDto)
            throws RegistrationException {
        return userService.register(requestDto);
    }

    @Operation(summary = "User Login", description = "Authenticate a user and return a JWT token.")
    @PostMapping("/login")
    public UserResponseDto login(@RequestBody @Valid UserLoginRequestDto request) {
        return authenticationService.login(request);
    }

    @Operation(summary = "Forgot Password", description = "Initiate a password reset request for the user.")
    @PostMapping("/forgot-password")
    public UserDevMsgRespondDto forgotPassword(@RequestBody @Valid UserRequestDto request) {
        return authenticationService.createRestoreRequest(request);
    }

    @Operation(summary = "Reset Password", description = "Reset the user's password using the provided token.")
    @PostMapping("/reset-password")
    public UserDevMsgRespondDto resetPassword(@RequestParam("token") String token,
                                              @RequestBody @Valid UserUpdatePasswordRequestDto request) {
        return authenticationService.restorePassword(token, request);
    }

    @Operation(summary = "Email Confirmation", description = "Confirm the user's email using the provided token.")
    @PostMapping("/confirmation")
    public UserDevMsgRespondDto emailConfirmation(@RequestParam("token") String token) {
        return authenticationService.changeConfirmingStatus(token);
    }
}
