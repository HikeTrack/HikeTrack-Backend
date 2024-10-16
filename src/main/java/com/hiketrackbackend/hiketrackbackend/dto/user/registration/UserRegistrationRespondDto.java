package com.hiketrackbackend.hiketrackbackend.dto.user.registration;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserRegistrationRespondDto {
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
}
