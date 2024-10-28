package com.hiketrackbackend.hiketrackbackend.service.notification;

public interface EmailSender {
    void send(String toEmail, String param);
}
