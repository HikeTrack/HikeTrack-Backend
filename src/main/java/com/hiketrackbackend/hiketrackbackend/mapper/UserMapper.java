package com.hiketrackbackend.hiketrackbackend.mapper;

import com.hiketrackbackend.hiketrackbackend.config.MapperConfig;
import com.hiketrackbackend.hiketrackbackend.dto.user.UserForgotRespondDto;
import com.hiketrackbackend.hiketrackbackend.dto.user.UserRegistrationRequestDto;
import com.hiketrackbackend.hiketrackbackend.dto.user.UserRegistrationRespondDto;
import com.hiketrackbackend.hiketrackbackend.model.User;
import com.hiketrackbackend.hiketrackbackend.model.UserProfile;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.time.OffsetDateTime;

@Mapper(config = MapperConfig.class)
public interface UserMapper {
    @Mapping(target = "userProfile", ignore = true)
    User toEntity(UserRegistrationRequestDto userRegistrationRequestDto);

    UserRegistrationRespondDto toDto(User user);

    UserForgotRespondDto toDto(String userEmail);

    @AfterMapping
    default void createProfile(@MappingTarget User user) {
        UserProfile profile = new UserProfile();
        profile.setRegistrationDate(OffsetDateTime.now().toLocalDate());
        profile.setUserPhoto("default user photo");
        user.setUserProfile(profile);
    }
}
