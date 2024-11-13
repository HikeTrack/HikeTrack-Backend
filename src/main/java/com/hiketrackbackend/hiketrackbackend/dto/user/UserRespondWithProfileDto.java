package com.hiketrackbackend.hiketrackbackend.dto.user;

import com.hiketrackbackend.hiketrackbackend.dto.user.profile.UserProfileRespondDto;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRespondWithProfileDto {
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private List<String> role;
    private UserProfileRespondDto userProfileRespondDto;
}
