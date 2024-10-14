package com.hiketrackbackend.hiketrackbackend.controller;

import com.hiketrackbackend.hiketrackbackend.dto.user.update.password.UserForgotPasswordRequestDto;
import com.hiketrackbackend.hiketrackbackend.dto.user.login.UserLoginRequestDto;
import com.hiketrackbackend.hiketrackbackend.dto.user.login.UserLoginResponseDto;
import com.hiketrackbackend.hiketrackbackend.dto.user.update.password.UserPasswordRespondDto;
import com.hiketrackbackend.hiketrackbackend.dto.user.registration.UserRegistrationRequestDto;
import com.hiketrackbackend.hiketrackbackend.dto.user.registration.UserRegistrationRespondDto;
import com.hiketrackbackend.hiketrackbackend.dto.user.update.password.UserUpdatePasswordRequestDto;
import com.hiketrackbackend.hiketrackbackend.exception.RegistrationException;
import com.hiketrackbackend.hiketrackbackend.security.AuthenticationService;
import com.hiketrackbackend.hiketrackbackend.service.UserService;
import com.hiketrackbackend.hiketrackbackend.validation.ValidToken;
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
    public UserLoginResponseDto login(@RequestBody @Valid UserLoginRequestDto request) {
        return authenticationService.login(request);
    }

    @Operation(summary = "",
            description = "")
    @PostMapping("/forgot-password")
    public UserPasswordRespondDto forgotPassword(@RequestBody @Valid UserForgotPasswordRequestDto request) {
        return authenticationService.createRestoreRequest(request);
    }


    @Operation(summary = "",
            description = "")
    @PostMapping("/reset-password")
    public UserPasswordRespondDto resetPassword(@ValidToken @RequestParam("token") String token,
                                                @RequestBody @Valid UserUpdatePasswordRequestDto request) {
        return authenticationService.restorePassword(token, request);
    }


    // TODO dodelat logout

    //    тут походу все таки надо посылать токен. но в таком случае получается система больше не знает креды в контексте и если на какой то ендпоинт поставлю
//                                                        @PreAuthorize("#username == authentication.name") то я не смогу ничего сделать?

//    узнав емаил можно поменять пароль любому кого знаешь мыло
//    еще ты забыл что ты поставил что бы на эту  ссылку(аус) не приходили жвт токены из за гугла
//
//
//        походу переделать все на гвт токен
//    @Operation(summary = "",
//            description = "")
//    @PostMapping("/update-password/{email}")
//    public UserLoginResponseDto updatePassword(@RequestBody @Valid UserRestoreRequestDto request,
//                               @PathVariable String email) {
//        return userService.updatePassword(request, email);
//    }



}
