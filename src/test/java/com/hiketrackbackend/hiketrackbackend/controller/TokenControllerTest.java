package com.hiketrackbackend.hiketrackbackend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hiketrackbackend.hiketrackbackend.dto.user.login.UserResponseDto;
import com.hiketrackbackend.hiketrackbackend.exception.InvalidTokenException;
import com.hiketrackbackend.hiketrackbackend.security.AuthenticationService;
import com.hiketrackbackend.hiketrackbackend.security.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class TokenControllerTest {
    @MockBean
    protected JwtUtil jwtUtil;

    @MockBean
    protected AuthenticationService authenticationService;

    @Autowired
    protected MockMvc mockMvc;

    @Test
    @DisplayName("Refresh JWT Token with a valid token should return new token")
    @WithMockUser(roles = "USER")
    public void testRefreshJwtTokenWithValidToken() throws Exception {
        when(jwtUtil.refreshToken(any(HttpServletRequest.class))).thenReturn(new UserResponseDto("newToken"));

        var result = mockMvc.perform(post("/tokens/refresh"))
                .andExpect(status().isOk())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        UserResponseDto responseDto = new ObjectMapper().readValue(responseBody, UserResponseDto.class);

        assertThat(responseDto.token()).isNotNull();
        assertThat(responseDto.token()).isNotEqualTo("validToken");
    }

    @Test
    @DisplayName("Refresh JWT token when called with an invalid request")
    public void testRefreshJWTTokenWhenCalledWithInvalidRequestThenReturnsUnauthorized() throws Exception {
        mockMvc.perform(post("/tokens/refresh"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Get JWT token with not valid access token")
    public void getJwtToken_InvalidTokenReturn404() throws Exception {
        Mockito.when(authenticationService.getToken("invalid_token"))
                .thenThrow(new InvalidTokenException("Token is invalid or expired"));

        mockMvc.perform(get("/tokens/access_token")
                        .param("token", "invalid_token"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Get JWT token when access token is null")
    public void getJwtToken_MissingTokenReturn400() throws Exception {
        mockMvc.perform(get("/tokens/access_token"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void getJwtToken_Success() throws Exception {
        UserResponseDto userResponseDto = new UserResponseDto("validJWTToken");
        Mockito.when(authenticationService.getToken("valid_token")).thenReturn(userResponseDto);

        mockMvc.perform(get("/tokens/access_token")
                        .param("token", "valid_token"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }
}
