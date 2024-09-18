package com.hiketrackbackend.hiketrackbackend.service;

import com.hiketrackbackend.hiketrackbackend.dto.userProfile.UserProfileRequestDto;
import com.hiketrackbackend.hiketrackbackend.dto.userProfile.UserProfileRespondDto;
import com.hiketrackbackend.hiketrackbackend.model.User;
import com.hiketrackbackend.hiketrackbackend.model.UserProfile;

public interface UserProfileService {
    UserProfile createUserProfile(User user);

    UserProfileRespondDto updateUserProfile(UserProfileRequestDto requestDto, Long id);

    UserProfileRespondDto getById(Long userId);
}
