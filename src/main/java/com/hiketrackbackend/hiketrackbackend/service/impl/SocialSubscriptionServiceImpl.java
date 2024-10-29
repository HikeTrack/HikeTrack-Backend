package com.hiketrackbackend.hiketrackbackend.service.impl;

import com.hiketrackbackend.hiketrackbackend.dto.UserDevMsgRespondDto;
import com.hiketrackbackend.hiketrackbackend.dto.socialSubscription.SubscriptionRequestDto;
import com.hiketrackbackend.hiketrackbackend.exception.EntityNotFoundException;
import com.hiketrackbackend.hiketrackbackend.mapper.SocialSubscriptionMapper;
import com.hiketrackbackend.hiketrackbackend.model.SocialSubscription;
import com.hiketrackbackend.hiketrackbackend.repository.SocialSubscriptionRepository;
import com.hiketrackbackend.hiketrackbackend.service.SocialSubscriptionService;
import com.hiketrackbackend.hiketrackbackend.service.notification.EmailSender;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SocialSubscriptionServiceImpl implements SocialSubscriptionService {
    private final SocialSubscriptionRepository socialSubscriptionRepository;
    private final SocialSubscriptionMapper socialSubscriptionMapper;
    private final EmailSender subscriptionEmailSenderImpl;

    @Override
    public UserDevMsgRespondDto create(SubscriptionRequestDto requestDto) {
        boolean exists = socialSubscriptionRepository.existsByEmail(requestDto.getEmail());
        if (exists) {
            return new UserDevMsgRespondDto("You already subscribed");
        }
        SocialSubscription socialSubscription = socialSubscriptionMapper.toEntity(requestDto);
        socialSubscriptionRepository.save(socialSubscription);

        subscriptionEmailSenderImpl.send(socialSubscription.getEmail(), socialSubscription.getEmail());
        return new UserDevMsgRespondDto("Thank you for subscribe");
    }

    @Override
    public UserDevMsgRespondDto delete(String email) {
        boolean exists = socialSubscriptionRepository.existsByEmail(email);
        if (!exists) {
            throw new EntityNotFoundException("Email " + email + " is not found in subscriptions");
        }
        socialSubscriptionRepository.deleteByEmail(email);
        return new UserDevMsgRespondDto("Email has been deleted from subscription successfully");
    }
}
