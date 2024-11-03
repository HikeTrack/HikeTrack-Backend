package com.hiketrackbackend.hiketrackbackend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hiketrackbackend.hiketrackbackend.dto.UserDevMsgRespondDto;
import com.hiketrackbackend.hiketrackbackend.dto.socialSubscription.SubscriptionRequestDto;
import com.hiketrackbackend.hiketrackbackend.security.JwtUtil;
import com.hiketrackbackend.hiketrackbackend.security.token.UserTokenService;
import com.hiketrackbackend.hiketrackbackend.service.SocialSubscriptionService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SocialSubscriptionController.class)
@SpringJUnitWebConfig
public class SocialSubscriptionControllerTest {
    private static MockMvc mockMvc;

    @MockBean
    private static SocialSubscriptionService socialSubscriptionService;

    @MockBean
    private static JwtUtil jwtUtil;

    @MockBean
    private static UserDetailsService userDetailsService;

    @MockBean
    private static UserTokenService<HttpServletRequest> userTokenService;

    @BeforeAll
    static void beforeAll(@Autowired WebApplicationContext applicationContext) {
        mockMvc = MockMvcBuilders.webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
    }

    @Test
    @DisplayName("Test the behavior of the controller method when the service layer returns a successful response.")
    @WithMockUser(username = "user1")
    public void testCreateEmailNewsletterSubscriptionWhenServiceReturnsSuccessThenReturn200() throws Exception {
        SubscriptionRequestDto requestDto = new SubscriptionRequestDto();
        requestDto.setEmail("test@example.com");
        UserDevMsgRespondDto responseDto = new UserDevMsgRespondDto("Subscription successful");

        Mockito.when(socialSubscriptionService.create(Mockito.any(SubscriptionRequestDto.class)))
                .thenReturn(responseDto);

        mockMvc.perform(post("/social/subscribe")
                        .content(new ObjectMapper().writeValueAsString(requestDto))
                        .contentType("application/json")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().json(new ObjectMapper().writeValueAsString(responseDto)));
    }

    @Test
    @DisplayName("Test the behavior of the controller method when the service layer throws an exception.")
    @WithMockUser(username = "user1")
    public void testCreateEmailNewsletterSubscriptionWhenServiceThrowsExceptionThenReturn400() throws Exception {
        SubscriptionRequestDto requestDto = new SubscriptionRequestDto();
        requestDto.setEmail("test@example.com");

        Mockito.when(socialSubscriptionService.create(Mockito.any(SubscriptionRequestDto.class)))
                .thenThrow(new RuntimeException("Invalid email"));

        mockMvc.perform(post("/social/subscribe")
                        .content(new ObjectMapper().writeValueAsString(requestDto))
                        .contentType("application/json")
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid email"));
    }

    @Test
    @DisplayName("Test the behavior of the controller method when the service layer returns a successful response for unsubscribe.")
    @WithMockUser(username = "user1")
    public void testDeleteEmailNewsletterSubscriptionWhenServiceReturnsSuccessThenReturn200() throws Exception {
        String email = "test@example.com";
        UserDevMsgRespondDto responseDto = new UserDevMsgRespondDto("Unsubscription successful");

        Mockito.when(socialSubscriptionService.delete(Mockito.anyString()))
                .thenReturn(responseDto);

        mockMvc.perform(post("/social/unsubscribe")
                        .param("email", email)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().json(new ObjectMapper().writeValueAsString(responseDto)));
    }

    @Test
    @DisplayName("Test the behavior of the controller method when the service layer throws an exception for unsubscribe.")
    @WithMockUser(username = "user1")
    public void testDeleteEmailNewsletterSubscriptionWhenServiceThrowsExceptionThenReturn400() throws Exception {
        String email = "test@example.com";

        Mockito.when(socialSubscriptionService.delete(Mockito.anyString()))
                .thenThrow(new RuntimeException("Invalid email"));

        mockMvc.perform(post("/social/unsubscribe")
                        .param("email", email)
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid email"));
    }

    @Test
    @DisplayName("Test the behavior of the controller method when the service layer returns a successful response for sending newsletter.")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void testSendNewsletterToAllSubsWhenServiceReturnsSuccessThenReturn200() throws Exception {
        String message = "This is a newsletter";
        UserDevMsgRespondDto responseDto = new UserDevMsgRespondDto("Newsletter sent successfully");

        Mockito.when(socialSubscriptionService.createNewsletter(Mockito.anyString()))
                .thenReturn(responseDto);

        mockMvc.perform(post("/social/newsletter")
                        .content(message)
                        .contentType("application/json")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().json(new ObjectMapper().writeValueAsString(responseDto)));
    }

    @Test
    @DisplayName("Test the behavior of the controller method "
            + "when the service layer throws an exception for sending newsletter.")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void testSendNewsletterToAllSubsWhenServiceThrowsExceptionThenReturn400() throws Exception {
        String message = "This is a newsletter";

        Mockito.when(socialSubscriptionService.createNewsletter(Mockito.anyString()))
                .thenThrow(new RuntimeException("Failed to send newsletter"));

        mockMvc.perform(post("/social/newsletter")
                        .content(message)
                        .contentType("application/json")
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Failed to send newsletter"));
    }
}
