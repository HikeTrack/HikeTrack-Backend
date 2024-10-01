package com.hiketrackbackend.hiketrackbackend.service;

public interface MailSender {
    void sendResetPasswordMailToGMail(String userEmail, String token);
}
