package com.hiketrackbackend.hiketrackbackend.service;

import com.hiketrackbackend.hiketrackbackend.dto.user.UserDevMsgRespondDto;
import com.hiketrackbackend.hiketrackbackend.dto.user.UserRequestDto;
import com.hiketrackbackend.hiketrackbackend.model.user.User;

public interface RoleService {
    void setUserDefaultRole(User user);

    UserDevMsgRespondDto changeUserRoleToGuide(UserRequestDto request);
}
