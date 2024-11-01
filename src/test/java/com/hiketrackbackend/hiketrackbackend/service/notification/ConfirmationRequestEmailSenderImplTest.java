package com.hiketrackbackend.hiketrackbackend.service.notification;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class ConfirmationRequestEmailSenderImplTest {

    @Mock
    private EmailUtils emailUtils;

    @InjectMocks
    private ConfirmationRequestEmailSenderImpl confirmationRequestEmailSender;

    private static final String CONFIRMATION_BASE_URL = "http://example.com/confirm?token=";
    private static final String TO_EMAIL = "test@example.com";
    private static final String TOKEN = "12345";
    private static final String SUBJECT = "Confirm Your Email Address";
    private static final String EXPECTED_MESSAGE = "<html>" +
            "<body>" +
            "<p>Hello mate,</p>" +
            "<p>Thank you for signing up! To complete your registration, please confirm your email address by clicking the link below:</p>" +
            "<p><a href=\"http://example.com/confirm?token=12345\">Confirm your email</a></p>" +
            "<p>If you did not create an account, you can ignore this email.</p>" +
            "<p>Thank you,<br>The Hike Track Team</p>" +
            "</body>" +
            "</html>";

    @BeforeEach
    public void setUp() throws Exception {
        // Use reflection to set the private field confirmationBaseUrl
        java.lang.reflect.Field field = ConfirmationRequestEmailSenderImpl.class.getDeclaredField("confirmationBaseUrl");
        field.setAccessible(true);
        field.set(confirmationRequestEmailSender, CONFIRMATION_BASE_URL);
    }

    @Test
    public void testSendWhenCalledThenEmailSent() {
        confirmationRequestEmailSender.send(TO_EMAIL, TOKEN);

        verify(emailUtils, times(1)).sendEmail(TO_EMAIL, SUBJECT, EXPECTED_MESSAGE);
    }

    // Handles null or empty token gracefully
    @Test
    public void test_handle_null_or_empty_token() {
        EmailUtils emailUtilsMock = Mockito.mock(EmailUtils.class);
        ConfirmationRequestEmailSenderImpl emailSender = new ConfirmationRequestEmailSenderImpl(emailUtilsMock);
        ReflectionTestUtils.setField(emailSender, "confirmationBaseUrl", "http://example.com/confirm?token=");

        String toEmail = "test@example.com";

        // Test with null token
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            emailSender.send(toEmail, null);
        });

        // Test with empty token
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            emailSender.send(toEmail, "");
        });
    }
}