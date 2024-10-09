package com.hiketrackbackend.hiketrackbackend.service;

import com.hiketrackbackend.hiketrackbackend.dto.user.*;
import com.hiketrackbackend.hiketrackbackend.exception.RegistrationException;

public interface UserService {
    UserRegistrationRespondDto register(UserRegistrationRequestDto requestDto) throws RegistrationException;

    UserForgotRespondDto createRestoreRequest(UserForgotRequestDto request);

    UserLoginResponseDto updatePassword(UserRestoreRequestDto request, String email);

    UserRegistrationRespondDto getById(Long id);
}
