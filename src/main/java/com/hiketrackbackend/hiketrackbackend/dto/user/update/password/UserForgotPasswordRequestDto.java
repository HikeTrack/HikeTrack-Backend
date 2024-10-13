package com.hiketrackbackend.hiketrackbackend.dto.user.update.password;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserForgotPasswordRequestDto {
    @Email
    @NotBlank
    private String email;
}
