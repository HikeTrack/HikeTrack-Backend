package com.hiketrackbackend.hiketrackbackend.service.impl;

import com.hiketrackbackend.hiketrackbackend.dto.UserDevMsgRespondDto;
import com.hiketrackbackend.hiketrackbackend.dto.socialSubscription.SubscriptionRequestDto;
import com.hiketrackbackend.hiketrackbackend.exception.EntityNotFoundException;
import com.hiketrackbackend.hiketrackbackend.mapper.SocialSubscriptionMapper;
import com.hiketrackbackend.hiketrackbackend.model.SocialSubscription;
import com.hiketrackbackend.hiketrackbackend.repository.SocialSubscriptionRepository;
import com.hiketrackbackend.hiketrackbackend.service.notification.EmailSender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SocialSubscriptionServiceImplTest {

    @Mock
    private SocialSubscriptionRepository socialSubscriptionRepository;

    @Mock
    private SocialSubscriptionMapper socialSubscriptionMapper;

    @Mock
    private EmailSender subscriptionEmailSenderImpl;

    @InjectMocks
    private SocialSubscriptionServiceImpl socialSubscriptionServiceImpl;

    private SubscriptionRequestDto requestDto;

    @BeforeEach
    public void setUp() {
        requestDto = new SubscriptionRequestDto();
        requestDto.setEmail("test@example.com");
    }

    @Test
    public void testCreateWhenEmailExistsThenReturnAlreadySubscribedMessage() {
        // Arrange
        when(socialSubscriptionRepository.existsByEmail(requestDto.getEmail())).thenReturn(true);

        // Act
        UserDevMsgRespondDto response = socialSubscriptionServiceImpl.create(requestDto);

        // Assert
        assertEquals("You already subscribed", response.message());
        verify(socialSubscriptionRepository, times(1)).existsByEmail(requestDto.getEmail());
        verify(socialSubscriptionRepository, never()).save(any(SocialSubscription.class));
        verify(subscriptionEmailSenderImpl, never()).send(anyString(), anyString());
    }

    @Test
    public void testCreateWhenEmailDoesNotExistThenReturnThankYouMessage() {
        // Arrange
        when(socialSubscriptionRepository.existsByEmail(requestDto.getEmail())).thenReturn(false);
        SocialSubscription socialSubscription = new SocialSubscription();
        socialSubscription.setEmail(requestDto.getEmail());
        when(socialSubscriptionMapper.toEntity(requestDto)).thenReturn(socialSubscription);

        // Act
        UserDevMsgRespondDto response = socialSubscriptionServiceImpl.create(requestDto);

        // Assert
        assertEquals("Thank you for subscribe", response.message());
        verify(socialSubscriptionRepository, times(1)).existsByEmail(requestDto.getEmail());
        verify(socialSubscriptionRepository, times(1)).save(socialSubscription);
        verify(subscriptionEmailSenderImpl, times(1)).send(socialSubscription.getEmail(), socialSubscription.getEmail());
    }

    @Test
    public void testDeleteWhenEmailExistsThenReturnSuccessMessage() {
        // Arrange
        String email = "test@example.com";
        when(socialSubscriptionRepository.existsByEmail(email)).thenReturn(true);

        // Act
        UserDevMsgRespondDto response = socialSubscriptionServiceImpl.delete(email);

        // Assert
        assertEquals("Email has been deleted from subscription successfully", response.message());
        verify(socialSubscriptionRepository, times(1)).existsByEmail(email);
        verify(socialSubscriptionRepository, times(1)).deleteByEmail(email);
    }

    @Test
    public void testDeleteWhenEmailDoesNotExistThenThrowEntityNotFoundException() {
        // Arrange
        String email = "test@example.com";
        when(socialSubscriptionRepository.existsByEmail(email)).thenReturn(false);

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            socialSubscriptionServiceImpl.delete(email);
        });

        assertEquals("Email " + email + " is not found in subscriptions", exception.getMessage());
        verify(socialSubscriptionRepository, times(1)).existsByEmail(email);
        verify(socialSubscriptionRepository, never()).deleteByEmail(email);
    }

    @Test
    public void testCreateNewsletterWhenEmailsExistThenSendNewsletters() {
        // Arrange
        String message = "Newsletter content";
        List<String> emails = List.of("email1@example.com", "email2@example.com");
        when(socialSubscriptionRepository.findAllEmails()).thenReturn(emails);

        // Act
        UserDevMsgRespondDto response = socialSubscriptionServiceImpl.createNewsletter(message);

        // Assert
        assertEquals("Newsletters has been send successfully", response.message());
        verify(socialSubscriptionRepository, times(1)).findAllEmails();
        verify(subscriptionEmailSenderImpl, times(1)).newsletterDistribution(message, emails);
    }

    @Test
    public void testCreateNewsletterWhenNoEmailsThenSendNewsletters() {
        // Arrange
        String message = "Newsletter content";
        List<String> emails = Collections.emptyList();
        when(socialSubscriptionRepository.findAllEmails()).thenReturn(emails);

        // Act
        UserDevMsgRespondDto response = socialSubscriptionServiceImpl.createNewsletter(message);

        // Assert
        assertEquals("Newsletters has been send successfully", response.message());
        verify(socialSubscriptionRepository, times(1)).findAllEmails();
        verify(subscriptionEmailSenderImpl, times(1)).newsletterDistribution(message, emails);
    }
}