package com.hiketrackbackend.hiketrackbackend.service;

import com.hiketrackbackend.hiketrackbackend.dto.UserDevMsgRespondDto;
import com.hiketrackbackend.hiketrackbackend.dto.subscription.SubscriptionRequestDto;

public interface SocialSubscriptionService {
    UserDevMsgRespondDto create(SubscriptionRequestDto requestDto);

    UserDevMsgRespondDto delete(String email);

    UserDevMsgRespondDto createNewsletter(String message);
}
