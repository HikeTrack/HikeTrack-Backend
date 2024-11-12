package com.hiketrackbackend.hiketrackbackend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hiketrackbackend.hiketrackbackend.dto.user.login.UserResponseDto;
import com.hiketrackbackend.hiketrackbackend.exception.InvalidTokenException;
import com.hiketrackbackend.hiketrackbackend.model.user.Role;
import com.hiketrackbackend.hiketrackbackend.model.user.User;
import com.hiketrackbackend.hiketrackbackend.model.user.UserProfile;
import com.hiketrackbackend.hiketrackbackend.security.AuthenticationService;
import com.hiketrackbackend.hiketrackbackend.security.JwtUtil;
import io.jsonwebtoken.io.IOException;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
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

        var result = mockMvc.perform(post("/tokens/refresh")
                        .header("Authorization", "Bearer " + "validToken")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        UserResponseDto responseDto = new ObjectMapper().readValue(responseBody, UserResponseDto.class);

        assertThat(responseDto.Token()).isNotNull();
        assertThat(responseDto.Token()).isNotEqualTo("validToken");
    }

    @Test
    @DisplayName("Refresh JWT token when called with an invalid request")
    public void testRefreshJWTTokenWhenCalledWithInvalidRequestThenReturnsUnauthorized() throws Exception {
        mockMvc.perform(post("/tokens/refresh")
                        .header("Authorization", "Bearer " + "notValidToken")
                        .with(csrf()))
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










    private void setUserToContext() {
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