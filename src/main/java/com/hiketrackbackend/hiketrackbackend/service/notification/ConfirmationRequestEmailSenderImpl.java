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
                "<html>" +
                        "<body>" +
                        "<p>Hello mate,</p>" +
                        "<p>Thank you for signing up! To complete your registration, please confirm your email address by clicking the link below:</p>" +
                        "<p><a href=\"%s\">Confirm your email</a></p>" +
                        "<p>If you did not create an account, you can ignore this email.</p>" +
                        "<p>Thank you,<br>The Hike Track Team</p>" +
                        "</body>" +
                        "</html>",
                url
        );
    }
}
