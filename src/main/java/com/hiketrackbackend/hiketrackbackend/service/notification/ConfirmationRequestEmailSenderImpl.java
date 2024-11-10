package com.hiketrackbackend.hiketrackbackend.service.notification;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ConfirmationRequestEmailSenderImpl implements EmailSender {
    private static final String SUBJECT = "Confirm Your Email Address";
    private final EmailUtils emailUtils;

    @Value("${confirmation.email.base.url}")
    private String confirmationBaseUrl;

    @Override
    public void send(String toEmail, String token) {
        if (token == null || token.isEmpty() || toEmail == null || toEmail.isEmpty()) {
            throw new IllegalArgumentException("Email " + toEmail + " and token "
                    + token + " should be present");
        }
        String url = confirmationBaseUrl + token;
        String message = generateConfirmationEmail(url);
        emailUtils.sendEmail(toEmail, SUBJECT, message);
    }

    @PostConstruct
    private void validateConfig() {
        if (confirmationBaseUrl == null) {
            throw new IllegalStateException("Confirmation base URL is not initialized.");
        }
    }

    private String generateConfirmationEmail(String url) {
        return String.format(
                "Hello mate,\n\n"
                        + "Thank you for signing up! To complete your registration, "
                        + "please confirm your email address by clicking the link below:\n\n"
                        + url + "\n\n"
                        + ". If you did not create an account, you can ignore this email.\n\n"
                        + "Best regards,\n"
                        + "Hike Track Team"
        );
    }
}
