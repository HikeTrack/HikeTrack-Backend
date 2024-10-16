package com.hiketrackbackend.hiketrackbackend.service.notification;

import com.hiketrackbackend.hiketrackbackend.exception.EmailSendingException;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Properties;

@Component
public class EmailUtils {
    @Value("${mail.host}")
    private String mailHost;

    @Value("${mail.port}")
    private String mailPort;

    @Getter
    @Value("${from.email}")
    private String from;

    @Value("${smtp.password}")
    private String password;


    public void sendEmail(String userEmail, String subject, String messageText) {
        Session session = createSession();
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(userEmail));
            message.setSubject(subject);
            message.setText(messageText);
            Transport.send(message);
        } catch (MessagingException e) {
            throw new EmailSendingException("Failed to send email", e);
        }
    }

    private Session createSession() {
        if (mailHost == null || mailPort == null || from == null || password == null) {
            throw new IllegalStateException("Email configuration properties are not properly initialized.");
        }

        Properties props = new Properties();
        props.put("mail.smtp.host", mailHost);
        props.put("mail.smtp.port", mailPort);
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.ssl.enable", "true");

        return Session.getInstance(
                props,
                new Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(from, password);
                    }
                }
        );
    }
}
