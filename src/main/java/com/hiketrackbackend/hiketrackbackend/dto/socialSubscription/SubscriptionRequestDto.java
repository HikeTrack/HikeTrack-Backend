package com.hiketrackbackend.hiketrackbackend.dto.socialSubscription;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SubscriptionRequestDto {
    @Email(message = "Email should be valid")
    @NotBlank(message = "Email cannot be blank")
    private String email;
}
