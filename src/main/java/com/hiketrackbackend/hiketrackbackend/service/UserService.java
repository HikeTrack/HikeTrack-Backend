package com.hiketrackbackend.hiketrackbackend.service;

import com.hiketrackbackend.hiketrackbackend.dto.user.UserRespondDto;
import com.hiketrackbackend.hiketrackbackend.dto.user.update.UserUpdateRequestDto;
import com.hiketrackbackend.hiketrackbackend.dto.user.update.password.UserPasswordRespondDto;
import com.hiketrackbackend.hiketrackbackend.dto.user.update.password.UserUpdatePasswordRequestDto;
import com.hiketrackbackend.hiketrackbackend.dto.user.registration.UserRegistrationRequestDto;
import com.hiketrackbackend.hiketrackbackend.dto.user.registration.UserRegistrationRespondDto;
import com.hiketrackbackend.hiketrackbackend.exception.RegistrationException;

public interface UserService {
    UserRegistrationRespondDto register(UserRegistrationRequestDto requestDto) throws RegistrationException;

    UserPasswordRespondDto updatePassword(UserUpdatePasswordRequestDto request, Long id);

    UserRespondDto updateUser(UserUpdateRequestDto requestDto, Long id);

    UserRespondDto getUserById(Long id);

    void deleteUser(Long id);
}
