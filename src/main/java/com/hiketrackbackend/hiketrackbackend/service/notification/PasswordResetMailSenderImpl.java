package com.hiketrackbackend.hiketrackbackend.service.notification;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.Properties;

@Service
public class PasswordResetMailSenderImpl implements MailSender {
    @Value("${mail.host}")
    private String mailHost;
    @Value("${mail.port}")
    private String mailPort;
    @Value("${from.email}")
    private String from;
    @Value("${smtp.password}")
    private String password;

    @Override
    public void sendMessage(String userEmail, String token) {
        Properties props = new Properties();
        props.put("mail.smtp.host", mailHost);
        props.put("mail.smtp.port", mailPort);
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.ssl.enable", "true");

        Session session = Session.getInstance(
                props,
                new Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(from, password);
                    }
                }
        );

        String resetUrl = "https://hiketrack.github.io/auth/reset-password?token=" + token;
        // TODO create a template for sending to user
        String resetMessage = "To reset your password please use this link:\n" + resetUrl
                + ". If it was not u who sent this request please visit our website and change the password";
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(userEmail));
            message.setSubject("Reset Password");
            message.setText(resetMessage);
            Transport.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }
}
