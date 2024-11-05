package com.hiketrackbackend.hiketrackbackend.dto.user.registration;

import com.hiketrackbackend.hiketrackbackend.validation.FieldMatch;
import com.hiketrackbackend.hiketrackbackend.validation.Password;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@FieldMatch(
        password = "password",
        repeatPassword = "repeatPassword",
        message = "Passwords do not match")
public class UserRegistrationRequestDto {

    @Email(message = "Email should be valid")
    @NotBlank(message = "Email is mandatory")
    private String email;

    @NotBlank(message = "Password is mandatory")
    @Password(message = "Password must meet complexity requirements")
    private String password;

    @NotBlank(message = "Confirm Password is mandatory")
    @Password(message = "Confirm Password must meet complexity requirements")
    private String repeatPassword;

    @NotBlank(message = "First name is mandatory")
    private String firstName;

    @NotBlank(message = "Last name is mandatory")
    private String lastName;
}
