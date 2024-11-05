package com.hiketrackbackend.hiketrackbackend.dto.user.update;

import com.hiketrackbackend.hiketrackbackend.dto.user.profile.UserProfileRespondDto;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

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
