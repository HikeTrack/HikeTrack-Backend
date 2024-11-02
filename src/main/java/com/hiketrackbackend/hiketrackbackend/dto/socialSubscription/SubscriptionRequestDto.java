package com.hiketrackbackend.hiketrackbackend.dto.socialSubscription;

import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SubscriptionRequestDto {
    @Email
    private String email;
}
