package com.hiketrackbackend.hiketrackbackend.service;

import com.hiketrackbackend.hiketrackbackend.dto.user.UserRequestDto;
import com.hiketrackbackend.hiketrackbackend.dto.user.UserRespondDto;
import com.hiketrackbackend.hiketrackbackend.dto.user.update.UserUpdateRequestDto;
import com.hiketrackbackend.hiketrackbackend.dto.user.UserDevMsgRespondDto;
import com.hiketrackbackend.hiketrackbackend.dto.user.update.UserUpdatePasswordRequestDto;
import com.hiketrackbackend.hiketrackbackend.dto.user.registration.UserRegistrationRequestDto;
import com.hiketrackbackend.hiketrackbackend.dto.user.registration.UserRegistrationRespondDto;
import com.hiketrackbackend.hiketrackbackend.exception.RegistrationException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {
    UserRegistrationRespondDto register(UserRegistrationRequestDto requestDto) throws RegistrationException;

    UserDevMsgRespondDto updatePassword(UserUpdatePasswordRequestDto request, Long id);

    UserRespondDto updateUser(UserUpdateRequestDto requestDto, Long id, MultipartFile file);

    UserRespondDto getLoggedInUser(HttpServletRequest request);

    void deleteUser(Long id);

    UserDevMsgRespondDto promoteRequest(UserRequestDto request);
}
