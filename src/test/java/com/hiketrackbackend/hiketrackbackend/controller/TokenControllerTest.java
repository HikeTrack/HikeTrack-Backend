package com.hiketrackbackend.hiketrackbackend.controller;

import com.hiketrackbackend.hiketrackbackend.dto.user.login.UserResponseDto;
import com.hiketrackbackend.hiketrackbackend.exception.JwtException;
import com.hiketrackbackend.hiketrackbackend.security.JwtUtil;
import com.hiketrackbackend.hiketrackbackend.security.token.UserTokenService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TokenController.class)
@ExtendWith({SpringExtension.class, MockitoExtension.class})
public class TokenControllerTest {
    private static MockMvc mockMvc;

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
    @DisplayName("Refresh JWT token when called with a valid request")
    @WithMockUser(username = "user1")
    public void testRefreshJWTTokenWhenCalledWithValidRequestThenReturnsExpectedUserResponseDto() throws Exception {
        UserResponseDto expectedResponse = new UserResponseDto("newToken");
        when(jwtUtil.refreshToken(any(HttpServletRequest.class))).thenReturn(expectedResponse);

        var result = mockMvc.perform(post("/token")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andReturn();

        String actualResponse = result.getResponse().getContentAsString();
        assertThat(actualResponse).isEqualTo("{\"Token\":\"newToken\"}");
    }

    @Test
    @DisplayName("Refresh JWT token when called with an invalid request")
    @WithMockUser(username = "user1")
    public void testRefreshJWTTokenWhenCalledWithInvalidRequestThenReturnsUnauthorized() throws Exception {
        when(jwtUtil.refreshToken(any(HttpServletRequest.class))).thenThrow(new JwtException("Expired or invalid token"));

        mockMvc.perform(post("/token")
                        .with(csrf()))
                .andExpect(status().isUnauthorized());
    }
}