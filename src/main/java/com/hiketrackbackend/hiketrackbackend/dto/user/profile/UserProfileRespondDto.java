package com.hiketrackbackend.hiketrackbackend.dto.user.profile;

import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

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
