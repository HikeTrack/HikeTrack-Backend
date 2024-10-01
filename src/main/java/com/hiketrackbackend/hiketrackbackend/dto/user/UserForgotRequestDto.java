package com.hiketrackbackend.hiketrackbackend.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserForgotRequestDto {
    @Email
    @NotBlank
    private String email;
}
