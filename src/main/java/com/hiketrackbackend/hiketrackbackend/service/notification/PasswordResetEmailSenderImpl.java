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
    public void send(String toEmail, String token) {
        String resetUrl = resetPasswordBaseUrl + token;
        String resetMessage = "To reset your password please use this link:\n" + resetUrl
                + ". If it was not you who sent this request, please visit our website and change the password.";
        emailUtils.sendEmail(toEmail, SUBJECT, resetMessage);
    }

    @PostConstruct
    private void validateConfig() {
        if (resetPasswordBaseUrl == null) {
            throw new IllegalStateException("Reset password base URL is not initialized.");
        }
    }
}
