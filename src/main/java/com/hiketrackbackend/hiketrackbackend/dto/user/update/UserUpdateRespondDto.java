package com.hiketrackbackend.hiketrackbackend.dto.user.update;

import com.hiketrackbackend.hiketrackbackend.dto.userProfile.UserProfileRespondDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserUpdateRespondDto {
    private String email;
    private String firstName;
    private String lastName;
    private UserProfileRespondDto userProfileRespondDto;
}
