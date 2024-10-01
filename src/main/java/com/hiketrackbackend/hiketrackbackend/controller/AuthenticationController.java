package com.hiketrackbackend.hiketrackbackend.controller;

import com.hiketrackbackend.hiketrackbackend.dto.user.*;
import com.hiketrackbackend.hiketrackbackend.exception.RegistrationException;
import com.hiketrackbackend.hiketrackbackend.security.AuthenticationService;
import com.hiketrackbackend.hiketrackbackend.security.UUIDTokenServiceImpl;
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
    private final UUIDTokenServiceImpl uuidTokenService;

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

    @Operation(summary = "",
            description = "")
    @PostMapping("/forgot-password")
    public UserForgotRespondDto forgotPassword(@RequestBody @Valid UserForgotRequestDto request) {
        return userService.createRestoreRequest(request);
    }

//    тут походу все таки надо посылать токен. но в таком случае получается система больше не знает креды в контексте и если на какой то ендпоинт поставлю
//                                                        @PreAuthorize("#username == authentication.name") то я не смогу ничего сделать?
    @Operation(summary = "",
            description = "")
    @GetMapping("/reset-password")
    public UserForgotRespondDto resetPassword(@ValidToken @RequestParam("UUIDToken") String UUIDToken) {
       return uuidTokenService.validateResetRequest(UUIDToken);
    }


//    узнав емаил можно поменять пароль любому кого знаешь мыло
//    еще ты забыл что ты поставил что бы на эту  ссылку(аус) не приходили жвт токены из за гугла
//
//
//        походу переделать все на гвт токен
    @Operation(summary = "",
            description = "")
    @PostMapping("/update-password/{email}")
    public UserLoginResponseDto updatePassword(@RequestBody @Valid UserRestoreRequestDto request,
                               @PathVariable String email) {
        return userService.updatePassword(request, email);
    }

    @Operation(summary = "",
            description = "")
    @PostMapping("/logout")
    public String logout(HttpServletRequest request,
                         Authentication authentication) {
        String email = authentication.getName();
        authenticationService.logout(request, email);
        return "Logged out successfully";
    }
}
