package com.hiketrackbackend.hiketrackbackend.service;

import com.hiketrackbackend.hiketrackbackend.dto.user.UserForgotRequestDto;
import com.hiketrackbackend.hiketrackbackend.dto.user.UserForgotRespondDto;
import com.hiketrackbackend.hiketrackbackend.dto.user.UserRegistrationRequestDto;
import com.hiketrackbackend.hiketrackbackend.dto.user.UserRegistrationRespondDto;
import com.hiketrackbackend.hiketrackbackend.dto.user.UserRestoreRequestDto;
import com.hiketrackbackend.hiketrackbackend.exception.RegistrationException;
import jakarta.servlet.http.HttpServletRequest;

public interface UserService {
    UserRegistrationRespondDto register(UserRegistrationRequestDto requestDto) throws RegistrationException;

    UserForgotRespondDto createRestoreRequest(UserForgotRequestDto request);

    void updatePassword(UserRestoreRequestDto request, String token);

    void logout(HttpServletRequest request, String email);
}
