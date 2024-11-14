package com.hiketrackbackend.hiketrackbackend.controller;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hiketrackbackend.hiketrackbackend.dto.UserDevMsgRespondDto;
import com.hiketrackbackend.hiketrackbackend.dto.user.UserRequestDto;
import com.hiketrackbackend.hiketrackbackend.dto.user.login.UserLoginRequestDto;
import com.hiketrackbackend.hiketrackbackend.dto.user.registration.UserRegistrationRequestDto;
import com.hiketrackbackend.hiketrackbackend.dto.user.registration.UserRegistrationRespondDto;
import com.hiketrackbackend.hiketrackbackend.dto.user.update.UserUpdatePasswordRequestDto;
import com.hiketrackbackend.hiketrackbackend.security.token.impl.ConfirmationTokenService;
import com.hiketrackbackend.hiketrackbackend.security.token.impl.PasswordResetUserTokenService;
import com.hiketrackbackend.hiketrackbackend.service.notification.ConfirmationRequestEmailSenderImpl;
import com.hiketrackbackend.hiketrackbackend.service.notification.PasswordResetEmailSenderImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AuthenticationControllerTest {
    protected static MockMvc mockMvc;

    @Autowired
    protected TestRestTemplate restTemplate;

    @Autowired
    protected ObjectMapper objectMapper;

    @MockBean
    protected PasswordResetUserTokenService passwordResetUserTokenService;

    @MockBean
    protected ConfirmationTokenService confirmationTokenService;

    @MockBean
    protected ConfirmationRequestEmailSenderImpl confirmationEmailSenderImpl;

    @MockBean
    protected PasswordResetEmailSenderImpl passwordResetEmailSenderImpl;

    @BeforeAll
    static void beforeAll(
            @Autowired WebApplicationContext applicationContext,
            @Autowired DataSource dataSource) throws SQLException {
        mockMvc = MockMvcBuilders.webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database/role/delete-user-role-table.sql")
            );
        }
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database/user/delete-all-user-table.sql")
            );
        }
    }

    @AfterEach
    void afterEach(@Autowired DataSource dataSource) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database/role/delete-user-role-table.sql")
            );
        }
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database/user/delete-all-user-table.sql")
            );
        }
    }

    @Test
    @DisplayName("Successfully registers a new user with valid input")
    public void testSuccessfulRegistration() throws Exception {
        UserRegistrationRequestDto requestDto = new UserRegistrationRequestDto();
        requestDto.setEmail("test@test.com");
        requestDto.setPassword("Password123@");
        requestDto.setRepeatPassword("Password123@");
        requestDto.setFirstName("John");
        requestDto.setLastName("Doe");

        String token = "validToken";
        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        when(confirmationTokenService.save(requestDto.getEmail())).thenReturn(token);
        doNothing().when(confirmationEmailSenderImpl).send(requestDto.getEmail(), token);

        MvcResult result = mockMvc.perform(
                        post("/auth/registration")
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        int actualStatusCode = result.getResponse().getStatus();
        String jsonResponse = result.getResponse().getContentAsString();
        UserRegistrationRespondDto respondDto = objectMapper
                .readValue(jsonResponse, new TypeReference<>() {});

        assertEquals(200, actualStatusCode);
        assertNotNull(result);
        assertEquals(requestDto.getEmail(), respondDto.getEmail());
        assertEquals(requestDto.getFirstName(), respondDto.getFirstName());
        assertEquals(requestDto.getLastName(), respondDto.getLastName());
    }

    @Test
    @DisplayName("Handles registration with incorrect email")
    public void testRegistrationNotValidEmailReturn400() throws Exception {
        UserRegistrationRequestDto requestDto = new UserRegistrationRequestDto();
        requestDto.setEmail("invalid-email");
        requestDto.setPassword("Password123@");
        requestDto.setRepeatPassword("Password123@");
        requestDto.setFirstName("John");
        requestDto.setLastName("Doe");

        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        mockMvc.perform(
                        post("/auth/registration")
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Handles registration with duplicate email fails result")
    @Sql(scripts = "classpath:database/user/add-user.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void testRegistrationDuplicateEmailReturnConflict() throws Exception {
        UserRegistrationRequestDto requestDto = new UserRegistrationRequestDto();
        requestDto.setEmail("test@test.com");
        requestDto.setPassword("Password123@");
        requestDto.setRepeatPassword("Password123@");
        requestDto.setFirstName("Jane");
        requestDto.setLastName("Doe");

        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        mockMvc.perform(
                        post("/auth/registration")
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("Should return 200 OK when authenticating with valid credentials")
    @Sql(scripts = "classpath:database/user/add-user.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void testSuccessfulAuthenticationWithValidCredentials() throws Exception {
        UserLoginRequestDto loginRequestDto = new UserLoginRequestDto("test@test.com", "Random147@");

        String jsonLoginRequest = objectMapper.writeValueAsString(loginRequestDto);
        mockMvc.perform(
                        post("/auth/login")
                                .content(jsonLoginRequest)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Handles authentication failure for unconfirmed users")
    @Sql(scripts = "classpath:database/user/add-users-notconfirmed.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void testAuthenticationFailureForUnconfirmedUsers() throws Exception {
        UserLoginRequestDto requestDto = new UserLoginRequestDto("test@test.com", "Random147@");
        String jsonLoginRequest = objectMapper.writeValueAsString(requestDto);
        mockMvc.perform(
                        post("/auth/login")
                                .content(jsonLoginRequest)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isForbidden());

    }

    @Test
    @DisplayName("Test login with invalid password")
    @Sql(scripts = "classpath:database/user/add-user.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void login_shouldFailWithInvalidPassword() {
        UserLoginRequestDto loginRequestDto
                = new UserLoginRequestDto("test@test.com", "IncorrectPass147@");

        ResponseEntity<String> response = restTemplate.postForEntity(
                "/auth/login",
                new HttpEntity<>(loginRequestDto),
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    @DisplayName("Test forgot password with valid data")
    @Sql(scripts = "classpath:database/user/add-user.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void forgotPassword_shouldInitiateResetRequest() {
        UserRequestDto request = new UserRequestDto();
        request.setEmail("test@test.com");

        String token = "validToken";
        when(passwordResetUserTokenService.save(request.getEmail())).thenReturn(token);
        doNothing().when(passwordResetEmailSenderImpl).send(request.getEmail(), token);

        ResponseEntity<String> response = restTemplate.postForEntity(
                "/auth/forgot-password",
                new HttpEntity<>(request),
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    @DisplayName("Test forgot password for incorrect email")
    public void forgotPassword_shouldFailForUnknownEmail() {
        UserRequestDto request = new UserRequestDto();
        request.setEmail("unknownuser@example.com");

        ResponseEntity<String> response = restTemplate.postForEntity(
                "/auth/forgot-password",
                new HttpEntity<>(request),
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("Test reset password with valid data")
    @Sql(scripts = "classpath:database/user/add-user.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void resetPassword_shouldUpdatePassword() {
        String token = "validToken";

        when(passwordResetUserTokenService.getValue(token)).thenReturn("test@test.com");
        doNothing().when(passwordResetUserTokenService).delete(token);

        UserUpdatePasswordRequestDto request = new UserUpdatePasswordRequestDto();
        request.setPassword("Newpassword123@");
        request.setRepeatPassword("Newpassword123@");

        ResponseEntity<UserDevMsgRespondDto> response = restTemplate.postForEntity(
                "/auth/reset-password?token=" + token,
                new HttpEntity<>(request),
                UserDevMsgRespondDto.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("Test reset password with invalid token")
    public void resetPassword_shouldFailWithInvalidToken() {
        String token = "invalid-reset-token";
        UserUpdatePasswordRequestDto request = new UserUpdatePasswordRequestDto();
        request.setPassword("newpassword123@");
        request.setRepeatPassword("newpassword123@");

        ResponseEntity<String> response = restTemplate.postForEntity(
                "/auth/reset-password?token=" + token,
                new HttpEntity<>(request),
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("Test confirm email with valid email")
    @Sql(scripts = "classpath:database/user/add-user.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void emailConfirmation_shouldConfirmEmail() {
        String token = "valid-confirmation-token";

        when(confirmationTokenService.isKeyExist(token)).thenReturn(true);
        when(confirmationTokenService.getValue(token)).thenReturn("test@test.com");
        doNothing().when(confirmationTokenService).delete(token);

        ResponseEntity<UserDevMsgRespondDto> response = restTemplate.postForEntity(
                "/auth/confirmation?token=" + token,
                null,
                UserDevMsgRespondDto.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("Test email confirm with invalid token")
    public void emailConfirmation_shouldFailWithInvalidToken() {
        String token = "invalid-confirmation-token";

        ResponseEntity<String> response = restTemplate.postForEntity(
                "/auth/confirmation?token=" + token,
                null,
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }
}
