package com.hiketrackbackend.hiketrackbackend.dto.user.update;

import com.hiketrackbackend.hiketrackbackend.validation.FieldMatch;
import com.hiketrackbackend.hiketrackbackend.validation.Password;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@FieldMatch(
        password = "password",
        repeatPassword = "repeatPassword",
        message = "Passwords do not match"
)
public class UserUpdatePasswordRequestDto {

    @NotBlank(message = "Password is mandatory")
    @Password(message = "Password must meet complexity requirements")
    private String password;

    @NotBlank(message = "Confirm Password is mandatory")
    @Password(message = "Confirm Password must meet complexity requirements")
    private String repeatPassword;
}
