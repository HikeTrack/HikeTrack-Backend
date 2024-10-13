package com.hiketrackbackend.hiketrackbackend.dto.userProfile;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserProfileRequestDto {
    @Size(min = 1, max = 20)
    private String country;

    @Size(min = 1, max = 30)
    private String city;
    private String userPhoto;

    @Max(15)
    private String phoneNumber;

    @Size(max = 300)
    private String aboutMe;
}
