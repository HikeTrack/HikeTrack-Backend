package com.hiketrackbackend.hiketrackbackend.dto.userProfile;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Getter
@Setter
public class UserProfileRespondDto {
    private Long id;
    private String country;
    private String city;
    private String phoneNumber;
    private String aboutMe;
    private LocalDate registrationDate;
    private String userPhoto;
}
