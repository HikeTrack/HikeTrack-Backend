package com.hiketrackbackend.hiketrackbackend.service.notification;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PasswordResetEmailSenderImpl implements EmailSender {
    private static final String SUBJECT = "Password Reset";
    private final EmailUtils emailUtils;

    @Value("${reset.password.base.url}")
    private String resetPasswordBaseUrl;

    @Override
    public void send(String email, String token) {
        if (token ==null || token.isEmpty() || email == null || email.isEmpty()) {
            throw new IllegalArgumentException("Email " + email + " and token "
                    + token + " should be present");
        }
        String resetUrl = resetPasswordBaseUrl + token;
        String resetMessage = generateConfirmationEmail(resetUrl);
        emailUtils.sendEmail(email, SUBJECT, resetMessage);
    }

    @PostConstruct
    private void validateConfig() {
        if (resetPasswordBaseUrl == null) {
            throw new IllegalStateException("Reset password base URL is not initialized.");
        }
    }

    private String generateConfirmationEmail(String url) {
        return String.format(
                "Dear user,\n\n" +
                        "To reset your password please use this link below:\n\n"
                        + url + "\n\n"
                        + ". If it was not you who sent this request, please visit "
                        + "our website and change the password please.\n\n"
                        + "Best regards,\n"
                        + "Hike Track Team"
        );
    }
}
