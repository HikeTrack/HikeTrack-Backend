package com.hiketrackbackend.hiketrackbackend.service;

import com.hiketrackbackend.hiketrackbackend.dto.user.UserRegistrationRequestDto;
import com.hiketrackbackend.hiketrackbackend.dto.user.UserRegistrationRespondDto;

public interface UserService {
    UserRegistrationRespondDto register(UserRegistrationRequestDto requestDto);
}
