package com.hiketrackbackend.hiketrackbackend.dto.user.update;

import com.hiketrackbackend.hiketrackbackend.dto.userProfile.UserProfileRequestDto;
import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserUpdateRequestDto {
    @Email
    private String email;
    private String firstName;
    private String lastName;
    private UserProfileRequestDto userProfileRequestDto;
}
