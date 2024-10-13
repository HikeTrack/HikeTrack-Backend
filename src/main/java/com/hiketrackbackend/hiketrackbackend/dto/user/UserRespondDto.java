package com.hiketrackbackend.hiketrackbackend.dto.user;

import com.hiketrackbackend.hiketrackbackend.dto.userProfile.UserProfileRespondDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRespondDto {
    private String email;
    private String firstName;
    private String lastName;
    private UserProfileRespondDto userProfileRespondDto;
}
