package com.hiketrackbackend.hiketrackbackend.dto.userProfile;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Getter
@Setter
public class UserProfileRespondDto {
    private Long id;
    private Long userId;
    private String country;
    private String city;
    private LocalDate registrationDate;
    private String userPhoto;
    private boolean hasCountry = true;
}
