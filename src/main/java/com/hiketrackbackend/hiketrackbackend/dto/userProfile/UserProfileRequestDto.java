package com.hiketrackbackend.hiketrackbackend.dto.userProfile;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserProfileRequestDto {
    private Long countryId;
    private String city;
    private String userPhoto;
}
