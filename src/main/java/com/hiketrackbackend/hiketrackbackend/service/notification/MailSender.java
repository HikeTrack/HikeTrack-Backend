package com.hiketrackbackend.hiketrackbackend.service.notification;

public interface MailSender {
    void sendMessage(String userEmail, String token);
}
