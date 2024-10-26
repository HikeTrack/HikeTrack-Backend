package com.hiketrackbackend.hiketrackbackend.service.notification;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PromotionRequestEmailSenderImpl implements EmailSender {
    private static final String SUBJECT = "Promotion request";
    private final EmailUtils emailUtils;

    @Override
    public void send(String userEmail, String request) {
        String message = "We have a promotion request from user with email: " + userEmail + ". Request: " + request;
        emailUtils.sendEmail(emailUtils.getFrom(), SUBJECT, message);
    }
}
