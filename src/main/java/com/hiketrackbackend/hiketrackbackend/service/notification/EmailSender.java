package com.hiketrackbackend.hiketrackbackend.service.notification;

import java.util.List;

public interface EmailSender {
    void send(String toEmail, String param);

    default void newsletterDistribution(String message, List<String> emails) {
        for (String email : emails) {
            send(email, message);
        }
    }
}
