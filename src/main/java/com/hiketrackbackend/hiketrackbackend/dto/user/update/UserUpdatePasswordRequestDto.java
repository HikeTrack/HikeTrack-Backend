package com.hiketrackbackend.hiketrackbackend.dto.user.update;

import com.hiketrackbackend.hiketrackbackend.validation.FieldMatch;
import com.hiketrackbackend.hiketrackbackend.validation.Password;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@FieldMatch(password = "password", repeatPassword = "repeatPassword")
public class UserUpdatePasswordRequestDto {
    @NotBlank
    @Password
    private String password;

    @NotBlank(message = "Confirm Password is mandatory")
    @Password
    private String repeatPassword;
}
