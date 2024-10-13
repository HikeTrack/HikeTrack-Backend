package com.hiketrackbackend.hiketrackbackend.mapper;

import com.hiketrackbackend.hiketrackbackend.config.MapperConfig;
import com.hiketrackbackend.hiketrackbackend.dto.user.update.UserUpdateRequestDto;
import com.hiketrackbackend.hiketrackbackend.dto.user.UserRespondDto;
import com.hiketrackbackend.hiketrackbackend.dto.user.update.password.UserPasswordRespondDto;
import com.hiketrackbackend.hiketrackbackend.dto.user.registration.UserRegistrationRequestDto;
import com.hiketrackbackend.hiketrackbackend.dto.user.registration.UserRegistrationRespondDto;
import com.hiketrackbackend.hiketrackbackend.dto.userProfile.UserProfileRequestDto;
import com.hiketrackbackend.hiketrackbackend.dto.userProfile.UserProfileRespondDto;
import com.hiketrackbackend.hiketrackbackend.model.User;
import com.hiketrackbackend.hiketrackbackend.model.UserProfile;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class)
public interface UserMapper {
    User toEntity(UserRegistrationRequestDto userRegistrationRequestDto);

    UserRegistrationRespondDto toDto(User user);

    UserPasswordRespondDto toDto(String message);

    @Mapping(target = "userProfileRespondDto", source = "userProfile")
    UserRespondDto toRespondDto(User user);

    UserProfileRespondDto toDto(UserProfile userProfile);

    void updateUserFromDto(UserUpdateRequestDto requestDto, @MappingTarget User user);

    void updateUserProfileFromDto(UserProfileRequestDto requestDto, @MappingTarget UserProfile userProfile);
}
