package com.hiketrackbackend.hiketrackbackend.service.notification;

public interface EmailSender {
    void send(String email, String param);
}
