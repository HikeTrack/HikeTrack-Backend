package com.hiketrackbackend.hiketrackbackend.dto.user;

import com.hiketrackbackend.hiketrackbackend.validation.FieldMatch;
import com.hiketrackbackend.hiketrackbackend.validation.Password;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@FieldMatch(password = "password", repeatPassword = "repeatPassword")
public class UserRegistrationRequestDto {
    @Email(message = "Email should be valid")
    @NotBlank(message = "Email is mandatory")
    private String email;

    @NotBlank
    @Password
    private String password;

    @NotBlank(message = "Confirm Password is mandatory")
    @Password
    private String repeatPassword;

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;
}
