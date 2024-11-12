package com.hiketrackbackend.hiketrackbackend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hiketrackbackend.hiketrackbackend.dto.rating.RatingRequestDto;
import com.hiketrackbackend.hiketrackbackend.model.user.Role;
import com.hiketrackbackend.hiketrackbackend.model.user.User;
import com.hiketrackbackend.hiketrackbackend.model.user.UserProfile;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Set;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class RatingControllerTest {
    protected static MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @BeforeAll
    static void beforeAll(@Autowired WebApplicationContext applicationContext) {
        mockMvc = MockMvcBuilders.webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Test successful rating update returns 200")
    public void testUpdateRatingWhenRequestIsSuccessfulThenReturn200() throws Exception {
        RatingRequestDto requestDto = new RatingRequestDto();
        requestDto.setRating(4);

        final User user = new User();
        user.setId(1L);
        user.setEmail("test@test.com");
        user.setPassword("test");
        user.setUserProfile(new UserProfile());
        Role role = new Role();
        role.setId(1L);
        role.setName(Role.RoleName.ROLE_USER);
        user.setRoles(Set.of(role));
        user.setLastName("test");
        user.setConfirmed(true);

        final UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        mockMvc.perform(patch("/ratings/1/1")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Test update rating with not valid data")
    @WithMockUser(roles = "USER")
    public void testUpdateRatingWhenRequestBodyIsInvalidThenReturn400() throws Exception {
        RatingRequestDto requestDto = new RatingRequestDto();
        requestDto.setRating(-1);

        final User user = new User();
        user.setId(1L);
        user.setEmail("test@test.com");
        user.setPassword("test");
        user.setUserProfile(new UserProfile());
        Role role = new Role();
        role.setId(1L);
        role.setName(Role.RoleName.ROLE_USER);
        user.setRoles(Set.of(role));
        user.setLastName("test");
        user.setConfirmed(true);

        final UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        mockMvc.perform(patch("/ratings/1/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest)
                        .with(csrf()))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Test unauthorized user")
    public void testUpdateRatingWhenUserIsNotAuthorizedThenReturn401() throws Exception {
        RatingRequestDto requestDto = new RatingRequestDto();
        requestDto.setRating(1);
        mockMvc.perform(patch("/ratings/21/1")
                        .content(new ObjectMapper().writeValueAsString(requestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }
}
