package com.hiketrackbackend.hiketrackbackend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hiketrackbackend.hiketrackbackend.dto.rating.RatingRequestDto;
import com.hiketrackbackend.hiketrackbackend.dto.rating.RatingRespondDto;
import com.hiketrackbackend.hiketrackbackend.security.JwtUtil;
import com.hiketrackbackend.hiketrackbackend.security.token.UserTokenService;
import com.hiketrackbackend.hiketrackbackend.service.RatingService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RatingController.class)
public class RatingControllerTest {
    protected static MockMvc mockMvc;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private UserDetailsService userDetailsService;

    @MockBean
    private UserTokenService<HttpServletRequest> userTokenService;

    @MockBean
    private RatingService ratingService;
    private RatingRequestDto validRatingRequestDto;
    private RatingRespondDto validRatingRespondDto;

    @BeforeAll
    static void beforeAll(@Autowired WebApplicationContext applicationContext) {
        mockMvc = MockMvcBuilders.webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
    }

    @BeforeEach
    public void setUp() {
        validRatingRequestDto = new RatingRequestDto();
        validRatingRequestDto.setRating(5);

        validRatingRespondDto = new RatingRespondDto();
        validRatingRespondDto.setRating(5);

        Mockito.when(ratingService.updateRating(Mockito.any(RatingRequestDto.class), Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(validRatingRespondDto);
    }

    @Test
    @WithMockUser(username = "user1")
    @DisplayName("Test successful rating update returns 200")
    public void testUpdateRatingWhenRequestIsSuccessfulThenReturn200() throws Exception {
        mockMvc.perform(patch("/rating/1/1")
                        .content(new ObjectMapper().writeValueAsString(validRatingRequestDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(new ObjectMapper().writeValueAsString(validRatingRespondDto)));
    }

    @Test
    @WithMockUser(username = "user2")
    public void testUpdateRatingWhenRequestBodyIsInvalidThenReturn400() throws Exception {
        RatingRequestDto requestDto = new RatingRequestDto();
        requestDto.setRating(-1);

        mockMvc.perform(patch("/rating/1/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestDto.toString())
                        .with(csrf()))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "user2")
    @DisplayName("Test unauthorized user returns 403")
    public void testUpdateRatingWhenUserIsNotAuthorizedThenReturn403() throws Exception {
        mockMvc.perform(patch("/rating/1/1")
                        .content(new ObjectMapper().writeValueAsString(validRatingRequestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }
}
