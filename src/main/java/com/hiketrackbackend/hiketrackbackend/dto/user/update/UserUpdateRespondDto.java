package com.hiketrackbackend.hiketrackbackend.dto.user.update;

import com.hiketrackbackend.hiketrackbackend.dto.userProfile.UserProfileRespondDto;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UserUpdateRespondDto {
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private List<String> role;
    private UserProfileRespondDto userProfileRespondDto;
    private String token;
}
