package com.hiketrackbackend.hiketrackbackend.mapper;

import com.hiketrackbackend.hiketrackbackend.config.MapperConfig;
import com.hiketrackbackend.hiketrackbackend.dto.user.UserRegistrationRequestDto;
import com.hiketrackbackend.hiketrackbackend.dto.user.UserRegistrationRespondDto;
import com.hiketrackbackend.hiketrackbackend.model.User;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface UserMapper {
    User toEntity(UserRegistrationRequestDto userRegistrationRequestDto);

    UserRegistrationRespondDto toDto(User user);
}
