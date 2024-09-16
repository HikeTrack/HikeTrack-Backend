package com.hiketrackbackend.hiketrackbackend.controller;

import com.hiketrackbackend.hiketrackbackend.dto.user.UserRegistrationRequestDto;
import com.hiketrackbackend.hiketrackbackend.dto.user.UserRegistrationRespondDto;
import com.hiketrackbackend.hiketrackbackend.exception.RegistrationException;
import com.hiketrackbackend.hiketrackbackend.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
@Validated
@Tag(name = "Authentication Management", description = "Registration and login functions")
public class AuthenticationController {
    private final UserService userService;

    @Operation(summary = "Registration of a new user")
    @PostMapping("/registration")
    public UserRegistrationRespondDto registration(@RequestBody @Valid UserRegistrationRequestDto requestDto) throws RegistrationException {
        return userService.register(requestDto);
    }
}
