package com.hiketrackbackend.hiketrackbackend.mapper;

import com.hiketrackbackend.hiketrackbackend.config.MapperConfig;
import com.hiketrackbackend.hiketrackbackend.dto.userProfile.UserProfileRequestDto;
import com.hiketrackbackend.hiketrackbackend.dto.userProfile.UserProfileRespondDto;
import com.hiketrackbackend.hiketrackbackend.model.UserProfile;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class)
public interface UserProfileMapper {
    UserProfileRespondDto toDto(UserProfile userProfile);

    void updateFromDto(UserProfileRequestDto requestDto, @MappingTarget UserProfile userProfile);

    @AfterMapping
    default void setCountryAndUserIds(@MappingTarget UserProfileRespondDto respondDto, UserProfile userProfile) {
        Long userId = userProfile.getUser().getId();
        respondDto.setUserId(userId);
        if (userProfile.getCountry() == null) {
            respondDto.setHasCountry(false);
        } else {
            Long countryId = userProfile.getCountry().getId();
            respondDto.setCountryId(countryId);
        }
    }
}
