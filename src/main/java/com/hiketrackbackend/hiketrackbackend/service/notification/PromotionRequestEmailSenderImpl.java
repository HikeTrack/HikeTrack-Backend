package com.hiketrackbackend.hiketrackbackend.service.notification;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PromotionRequestEmailSenderImpl implements EmailSender {
    private static final String SUBJECT = "Promotion request";
    private final EmailUtils emailUtils;

    @Override
    public void send(String userEmail, String request) {
        if (userEmail == null || userEmail.isEmpty()) {
            throw new IllegalArgumentException("Email is mandatory: " + userEmail);
        }
        String message = generateConfirmationEmail(userEmail, request);
        emailUtils.sendEmail(emailUtils.getFrom(), SUBJECT, message);
    }

    private String generateConfirmationEmail(String email, String request) {
        return String.format(
                "Dear Boss,\n\n"
                        + "We have a promotion request from user with email %s:\n\n"
                        + "Additional request from user:\n\n"
                        + request + "\n"
                        + "Best regards,\n"
                        + "Hike Track Team",
                email
        );
    }
}
