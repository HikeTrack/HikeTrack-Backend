package com.hiketrackbackend.hiketrackbackend.service.notification;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SubscriptionEmailSenderImpl implements EmailSender {
    private static final String SUBJECT = "Join the Adventure - Hike Track Newsletter";
    private final EmailUtils emailUtils;

    @Value("${unsubscribe.base.url}")
    private String unsubscribeBaseUrl;

    @Override
    public void send(String toEmail, String param) {
        if (toEmail == null || toEmail.isEmpty()) {
            throw new IllegalArgumentException("Email is mandatory: " + toEmail);
        }
        String message = generateConfirmationMessage(toEmail);
        emailUtils.sendEmail(toEmail, SUBJECT, message);
    }

    @Override
    public void newsletterDistribution(String message, List<String> emails) {
        for (String email : emails) {
            emailUtils.sendEmail(email, SUBJECT, message);
        }
    }

    private String generateConfirmationMessage(String email) {
        return String.format(
                "Dear customer,\n\n"
                        + "Thank you for subscribing to our newsletter! We are thrilled to welcome "
                        + "you to our community.\n\n"
                        + "From now on, you will receive the latest news, "
                        + "updates, and exclusive offers "
                        + "directly to your inbox. "
                        + "We promise to share only the most valuable and interesting "
                        + "information with you.\n\n"
                        + "If you have any questions or suggestions, "
                        + "please do not hesitate to reach out to us. "
                        + "We are always here to help!\n\n"
                        + "Best regards,\n"
                        + "The Hike Track Team\n\n"
                        + "If you wish to unsubscribe from our newsletter, please click here: %s",
                unsubscribeBaseUrl + email
        );
    }
}
