package com.hiketrackbackend.hiketrackbackend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hiketrackbackend.hiketrackbackend.dto.UserDevMsgRespondDto;
import com.hiketrackbackend.hiketrackbackend.dto.subscription.SubscriptionRequestDto;
import com.hiketrackbackend.hiketrackbackend.service.notification.SubscriptionEmailSenderImpl;
import io.jsonwebtoken.io.IOException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SocialSubscriptionControllerTest {
    private static MockMvc mockMvc;

    @MockBean
    protected SubscriptionEmailSenderImpl subscriptionEmailSenderImpl;

    @BeforeAll
    static void beforeAll(@Autowired WebApplicationContext applicationContext,
                          @Autowired DataSource dataSource) {
        mockMvc = MockMvcBuilders.webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
        List<String> scripts = List.of(
                "database/social/delete-social-subs.sql"
        );

        executeSqlScripts(dataSource, scripts);
    }

    @AfterEach
    void afterEach(@Autowired DataSource dataSource) {
        List<String> scripts = List.of(
                "database/social/delete-social-subs.sql"
        );

        executeSqlScripts(dataSource, scripts);
    }

    @Test
    @DisplayName("Test social sub to newsletter with valid email")
    public void testCreateEmailNewsletterSubscriptionWhenServiceReturnsSuccessThenReturn200() throws Exception {
        SubscriptionRequestDto requestDto = new SubscriptionRequestDto();
        requestDto.setEmail("test@test.com");
        UserDevMsgRespondDto responseDto = new UserDevMsgRespondDto("Thank you for subscribe");

        doNothing().when(subscriptionEmailSenderImpl).send(
                requestDto.getEmail(), requestDto.getEmail());

        mockMvc.perform(post("/socials/subscribe")
                        .content(new ObjectMapper().writeValueAsString(requestDto))
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(content().json(new ObjectMapper().writeValueAsString(responseDto)));
    }

    @Test
    @DisplayName("Test subscription with invalid email")
    public void testCreateEmailNewsletterSubscriptionWithInvalidEmailThenReturn400() throws Exception {
        SubscriptionRequestDto requestDto = new SubscriptionRequestDto();
        requestDto.setEmail("invalidEmail");

        mockMvc.perform(post("/socials/subscribe")
                        .content(new ObjectMapper().writeValueAsString(requestDto))
                        .contentType("application/json")
                        .with(csrf()))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Delete valid email from social subs")
    @Sql(scripts = "classpath:database/social/add-sub.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void testDeleteEmailNewsletterSubscriptionWhenServiceReturnsSuccessThenReturn200() throws Exception {
        String email = "test@test.com";
        UserDevMsgRespondDto responseDto
                = new UserDevMsgRespondDto("Email has been deleted from subscription successfully");

        mockMvc.perform(delete("/socials/unsubscribe")
                        .param("email", email))
                .andExpect(status().isOk())
                .andExpect(content().json(new ObjectMapper().writeValueAsString(responseDto)));
    }

    @Test
    @DisplayName("Test delete social sub with invalid email")
    public void testDeleteEmailNewsletterSubscriptionWhenEmailInvalidThenReturn400() throws Exception {
        String email = "invalidEmail";

        mockMvc.perform(delete("/socials/unsubscribe")
                        .param("email", email))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Send a newsletter to all subscribed users")
    @WithMockUser(roles = "ADMIN")
    @Sql(scripts = "classpath:database/social/add-sub.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void testSendNewsletterToAllSubsWhenServiceReturnsSuccessThenReturn200() throws Exception {
        String message = "This is a newsletter";
        UserDevMsgRespondDto responseDto = new UserDevMsgRespondDto("Newsletters has been send successfully");

        doNothing().when(subscriptionEmailSenderImpl).newsletterDistribution(anyString(), anyList());
        mockMvc.perform(post("/socials/newsletter")
                        .content(message)
                        .contentType("application/json")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().json(new ObjectMapper().writeValueAsString(responseDto)));
    }

    private static void executeSqlScripts(DataSource dataSource, List<String> scriptPaths) {
        for (String scriptPath : scriptPaths) {
            try (Connection connection = dataSource.getConnection()) {
                connection.setAutoCommit(true);
                ScriptUtils.executeSqlScript(
                        connection,
                        new ClassPathResource(scriptPath)
                );
            } catch (SQLException | IOException e) {
                throw new RuntimeException("Failed to execute script: " + scriptPath, e);
            }
        }
    }
}
