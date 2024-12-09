package com.hiketrackbackend.hiketrackbackend.service;

import com.hiketrackbackend.hiketrackbackend.dto.UserDevMsgRespondDto;
import com.hiketrackbackend.hiketrackbackend.dto.user.UserRequestDto;
import com.hiketrackbackend.hiketrackbackend.dto.user.UserRespondWithProfileDto;
import com.hiketrackbackend.hiketrackbackend.dto.user.profile.UserProfileRespondDto;
import com.hiketrackbackend.hiketrackbackend.dto.user.registration.UserRegistrationRequestDto;
import com.hiketrackbackend.hiketrackbackend.dto.user.registration.UserRegistrationRespondDto;
import com.hiketrackbackend.hiketrackbackend.dto.user.update.UserUpdatePasswordRequestDto;
import com.hiketrackbackend.hiketrackbackend.dto.user.update.UserUpdateRequestDto;
import com.hiketrackbackend.hiketrackbackend.dto.user.update.UserUpdateRespondDto;
import com.hiketrackbackend.hiketrackbackend.exception.RegistrationException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {
    UserRegistrationRespondDto register(
            UserRegistrationRequestDto requestDto
    ) throws RegistrationException;

    UserDevMsgRespondDto updatePassword(UserUpdatePasswordRequestDto request, Long id);

    UserUpdateRespondDto updateUser(UserUpdateRequestDto requestDto, Long id, MultipartFile file);

    UserRespondWithProfileDto getLoggedInUser(HttpServletRequest request);

    UserProfileRespondDto getUserProfileByUserId(Long userId);

    void deleteUser(Long id);

    UserDevMsgRespondDto promoteRequest(UserRequestDto request);
}
