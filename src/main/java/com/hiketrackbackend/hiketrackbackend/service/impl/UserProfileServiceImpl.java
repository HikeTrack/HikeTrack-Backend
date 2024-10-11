package com.hiketrackbackend.hiketrackbackend.service.impl;

import com.hiketrackbackend.hiketrackbackend.dto.userProfile.UserProfileRequestDto;
import com.hiketrackbackend.hiketrackbackend.dto.userProfile.UserProfileRespondDto;
import com.hiketrackbackend.hiketrackbackend.exception.EntityNotFoundException;
import com.hiketrackbackend.hiketrackbackend.mapper.UserProfileMapper;
import com.hiketrackbackend.hiketrackbackend.model.User;
import com.hiketrackbackend.hiketrackbackend.model.UserProfile;
import com.hiketrackbackend.hiketrackbackend.model.country.Country;
import com.hiketrackbackend.hiketrackbackend.repository.UserProfileRepository;
import com.hiketrackbackend.hiketrackbackend.repository.country.CountryRepository;
import com.hiketrackbackend.hiketrackbackend.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class UserProfileServiceImpl implements UserProfileService {
    private final UserProfileRepository userProfileRepository;
    private final UserProfileMapper userProfileMapper;

    @Override
    @Transactional
    public UserProfileRespondDto updateUserProfile(UserProfileRequestDto requestDto, Long id) {
        UserProfile userProfile = findByUserId(id);
        userProfileMapper.updateFromDto(requestDto, userProfile);
        userProfileRepository.save(userProfile);
        return userProfileMapper.toDto(userProfile);
    }

    @Override
    public UserProfileRespondDto getById(Long userId) {
        UserProfile userProfile = findByUserId(userId);
        return userProfileMapper.toDto(userProfile);
    }

    private UserProfile findByUserId(Long id) {
        return userProfileRepository.findByUserId(id).orElseThrow(
                () -> new EntityNotFoundException("Profile with user id " + id + " is not exist")
        );
    }
}
