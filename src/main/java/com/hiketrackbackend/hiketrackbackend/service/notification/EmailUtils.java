package com.hiketrackbackend.hiketrackbackend.service.notification;

import com.hiketrackbackend.hiketrackbackend.exception.EmailSendingException;
import jakarta.annotation.PostConstruct;
import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.util.Properties;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class EmailUtils {
    @Value("${mail.host}")
    private String mailHost;

    @Value("${mail.port}")
    private String mailPort;

    @Getter // made to have access out of this class
    @Value("${from.email}")
    private String from;

    @Value("${smtp.password}")
    private String password;

    @Value("${mail.smtp.ssl.enable}")
    private String smtpSslEnable;

    @Value("${mail.smtp.auth}")
    private String smtpAuth;
    private Session session;

    public void sendEmail(String toEmail, String subject, String messageText) {
        if (session == null) {
            throw new IllegalStateException("Email session is not initialized.");
        }

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(toEmail));
            message.setSubject(subject);
            message.setText(messageText);
            Transport.send(message);
        } catch (MessagingException e) {
            throw new EmailSendingException("Failed to send email", e);
        }
    }

    @PostConstruct
    private void initializeSession() {
        if (mailHost == null || mailPort == null || from == null || password == null) {
            throw new IllegalStateException(
                    "Email configuration properties are not properly initialized."
            );
        }

        Properties props = new Properties();
        props.put("mail.smtp.host", mailHost);
        props.put("mail.smtp.port", mailPort);
        props.put("mail.smtp.auth", smtpAuth);
        props.put("mail.smtp.ssl.enable", smtpSslEnable);

        session = Session.getInstance(
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
