package com.hiketrackbackend.hiketrackbackend.service.impl;

import com.hiketrackbackend.hiketrackbackend.dto.AdminRequestDto;
import com.hiketrackbackend.hiketrackbackend.dto.UserDevMsgRespondDto;
import com.hiketrackbackend.hiketrackbackend.repository.SocialSubscriptionRepository;
import com.hiketrackbackend.hiketrackbackend.service.AdministrationService;
import com.hiketrackbackend.hiketrackbackend.service.notification.EmailSender;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdministrationServiceImpl implements AdministrationService {
    private final EmailSender subscriptionEmailSenderImpl;
    private final SocialSubscriptionRepository socialSubscriptionRepository;

    @Override
    public UserDevMsgRespondDto createNewsletter(AdminRequestDto request) {
        List<String> emails = socialSubscriptionRepository.findAllEmails();
        subscriptionEmailSenderImpl.newsletterDistribution(request.getText(), emails);
        return new UserDevMsgRespondDto("Newsletters has been send successfully");
    }
}
