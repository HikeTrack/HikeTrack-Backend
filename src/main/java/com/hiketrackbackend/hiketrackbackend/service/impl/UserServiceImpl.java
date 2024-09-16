package com.hiketrackbackend.hiketrackbackend.service.impl;

import com.hiketrackbackend.hiketrackbackend.dto.user.UserRegistrationRequestDto;
import com.hiketrackbackend.hiketrackbackend.dto.user.UserRegistrationRespondDto;
import com.hiketrackbackend.hiketrackbackend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {


    @Override
    public UserRegistrationRespondDto register(UserRegistrationRequestDto requestDto) {
        return null;
    }
}
