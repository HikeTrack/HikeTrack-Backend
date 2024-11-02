package com.hiketrackbackend.hiketrackbackend.service.impl;

import com.hiketrackbackend.hiketrackbackend.dto.UserDevMsgRespondDto;
import com.hiketrackbackend.hiketrackbackend.dto.socialSubscription.SubscriptionRequestDto;
import com.hiketrackbackend.hiketrackbackend.exception.EntityNotFoundException;
import com.hiketrackbackend.hiketrackbackend.mapper.SocialSubscriptionMapper;
import com.hiketrackbackend.hiketrackbackend.model.SocialSubscription;
import com.hiketrackbackend.hiketrackbackend.repository.SocialSubscriptionRepository;
import com.hiketrackbackend.hiketrackbackend.service.notification.EmailSender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
    @DisplayName("Create sub when customer already subscribed")
    public void testCreateWhenEmailExistsThenReturnAlreadySubscribedMessage() {
        when(socialSubscriptionRepository.existsByEmail(requestDto.getEmail())).thenReturn(true);

        UserDevMsgRespondDto response = socialSubscriptionServiceImpl.create(requestDto);

        assertEquals("You already subscribed", response.message());
        verify(socialSubscriptionRepository, times(1)).existsByEmail(requestDto.getEmail());
        verify(socialSubscriptionRepository, never()).save(any(SocialSubscription.class));
        verify(subscriptionEmailSenderImpl, never()).send(anyString(), anyString());
    }

    @Test
    @DisplayName("Create sub when sub is not not in db yet")
    public void testCreateWhenEmailDoesNotExistThenReturnThankYouMessage() {
        when(socialSubscriptionRepository.existsByEmail(requestDto.getEmail())).thenReturn(false);
        SocialSubscription socialSubscription = new SocialSubscription();
        socialSubscription.setEmail(requestDto.getEmail());
        when(socialSubscriptionMapper.toEntity(requestDto)).thenReturn(socialSubscription);

        UserDevMsgRespondDto response = socialSubscriptionServiceImpl.create(requestDto);

        assertEquals("Thank you for subscribe", response.message());
        verify(socialSubscriptionRepository, times(1)).existsByEmail(requestDto.getEmail());
        verify(socialSubscriptionRepository, times(1)).save(socialSubscription);
        verify(subscriptionEmailSenderImpl, times(1)).send(socialSubscription.getEmail(), socialSubscription.getEmail());
    }

    @Test
    @DisplayName("Delete sub with valid email")
    public void testDeleteWhenEmailExistsThenReturnSuccessMessage() {
        String email = "test@example.com";
        when(socialSubscriptionRepository.existsByEmail(email)).thenReturn(true);

        UserDevMsgRespondDto response = socialSubscriptionServiceImpl.delete(email);

        assertEquals("Email has been deleted from subscription successfully", response.message());
        verify(socialSubscriptionRepository, times(1)).existsByEmail(email);
        verify(socialSubscriptionRepository, times(1)).deleteByEmail(email);
    }

    @Test
    @DisplayName("Delete sub with not valid email")
    public void testDeleteWhenEmailDoesNotExistThenThrowEntityNotFoundException() {
        String email = "test@example.com";
        when(socialSubscriptionRepository.existsByEmail(email)).thenReturn(false);

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            socialSubscriptionServiceImpl.delete(email);
        });

        assertEquals("Email " + email + " is not found in subscriptions", exception.getMessage());
        verify(socialSubscriptionRepository, times(1)).existsByEmail(email);
        verify(socialSubscriptionRepository, never()).deleteByEmail(email);
    }

    @Test
    @DisplayName("Create newsletter with valid data")
    public void testCreateNewsletterWhenEmailsExistThenSendNewsletters() {
        String message = "Newsletter content";
        List<String> emails = List.of("email1@example.com", "email2@example.com");
        when(socialSubscriptionRepository.findAllEmails()).thenReturn(emails);

        UserDevMsgRespondDto response = socialSubscriptionServiceImpl.createNewsletter(message);

        assertEquals("Newsletters has been send successfully", response.message());
        verify(socialSubscriptionRepository, times(1)).findAllEmails();
        verify(subscriptionEmailSenderImpl, times(1)).newsletterDistribution(message, emails);
    }

    @Test
    @DisplayName("Create newsletter with no emails in DB")
    public void testCreateNewsletterWhenNoEmailsThenSendNewsletters() {
        String message = "Newsletter content";
        List<String> emails = Collections.emptyList();
        when(socialSubscriptionRepository.findAllEmails()).thenReturn(emails);

        UserDevMsgRespondDto response = socialSubscriptionServiceImpl.createNewsletter(message);

        assertEquals("Newsletters has been send successfully", response.message());
        verify(socialSubscriptionRepository, times(1)).findAllEmails();
        verify(subscriptionEmailSenderImpl, times(1)).newsletterDistribution(message, emails);
    }
}