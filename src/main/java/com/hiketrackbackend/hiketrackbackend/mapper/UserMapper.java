package com.hiketrackbackend.hiketrackbackend.mapper;

import com.hiketrackbackend.hiketrackbackend.config.MapperConfig;
import com.hiketrackbackend.hiketrackbackend.dto.user.update.UserUpdateRequestDto;
import com.hiketrackbackend.hiketrackbackend.dto.user.UserRespondDto;
import com.hiketrackbackend.hiketrackbackend.dto.user.UserDevMsgRespondDto;
import com.hiketrackbackend.hiketrackbackend.dto.user.registration.UserRegistrationRequestDto;
import com.hiketrackbackend.hiketrackbackend.dto.user.registration.UserRegistrationRespondDto;
import com.hiketrackbackend.hiketrackbackend.dto.userProfile.UserProfileRequestDto;
import com.hiketrackbackend.hiketrackbackend.dto.userProfile.UserProfileRespondDto;
import com.hiketrackbackend.hiketrackbackend.model.Role;
import com.hiketrackbackend.hiketrackbackend.model.user.User;
import com.hiketrackbackend.hiketrackbackend.model.user.UserProfile;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;
import java.util.Set;

@Mapper(config = MapperConfig.class)
public interface UserMapper {
    User toEntity(UserRegistrationRequestDto userRegistrationRequestDto);

    UserRegistrationRespondDto toDto(User user);

    // TODO нет смысла тк делать потому что  даже если и
    //  захочешь добавить что то еще в дев смс то мапер туда не замапит бо принимает только
    //  стрингу так что убери и отдавай обычную стригу на контролеер
    UserDevMsgRespondDto toDto(String message);

    @Mapping(target = "userProfileRespondDto", source = "userProfile")
    UserRespondDto toRespondDto(User user);

    UserProfileRespondDto toDto(UserProfile userProfile);

    void updateUserFromDto(UserUpdateRequestDto requestDto, @MappingTarget User user);

    void updateUserProfileFromDto(UserProfileRequestDto requestDto, @MappingTarget UserProfile userProfile);

    @AfterMapping
    default void setUserRoles(User user, @MappingTarget UserRespondDto respondDto) {
        Set<Role> roles = user.getRoles();
        List<String> roleNames = roles.stream()
                .map(Role::getName)
                .map(Role.RoleName::name)
                .toList();
        respondDto.setRole(roleNames);
    }
}
