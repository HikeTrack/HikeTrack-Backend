package com.hiketrackbackend.hiketrackbackend.dto.user.profile;

import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserProfileRequestDto {

    @Size(min = 1, max = 20, message = "Country name must be between 1 and 20 characters")
    private String country;

    @Size(min = 1, max = 30, message = "City name must be between 1 and 30 characters")
    private String city;

    @Size(max = 15, message = "Phone number must be at most 15 characters")
    @Pattern(regexp = "^[+]?[0-9]{1,15}$",
            message = "Phone number must contain only digits and can start with a '+'")
    private String phoneNumber;

    @Past(message = "Date of birth must be in the past")
    private LocalDate dateOfBirth;

    @Size(max = 300, message = "About me section must be at most 300 characters")
    private String aboutMe;
}
