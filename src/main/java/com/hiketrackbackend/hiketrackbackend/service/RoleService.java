package com.hiketrackbackend.hiketrackbackend.service;

import com.hiketrackbackend.hiketrackbackend.dto.user.UserDevMsgRespondDto;
import com.hiketrackbackend.hiketrackbackend.dto.user.UserRequestDto;
import com.hiketrackbackend.hiketrackbackend.model.Role;

public interface RoleService {
    Role createUserDefaultRole();

    UserDevMsgRespondDto changeUserRoleToGuide(UserRequestDto request);
}
