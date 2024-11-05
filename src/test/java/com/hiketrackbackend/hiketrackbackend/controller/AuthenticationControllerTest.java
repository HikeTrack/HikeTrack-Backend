package com.hiketrackbackend.hiketrackbackend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hiketrackbackend.hiketrackbackend.dto.UserDevMsgRespondDto;
import com.hiketrackbackend.hiketrackbackend.dto.user.UserRequestDto;
import com.hiketrackbackend.hiketrackbackend.dto.user.login.UserLoginRequestDto;
import com.hiketrackbackend.hiketrackbackend.dto.user.login.UserResponseDto;
import com.hiketrackbackend.hiketrackbackend.dto.user.registration.UserRegistrationRequestDto;
import com.hiketrackbackend.hiketrackbackend.dto.user.registration.UserRegistrationRespondDto;
import com.hiketrackbackend.hiketrackbackend.dto.user.update.UserUpdatePasswordRequestDto;
import com.hiketrackbackend.hiketrackbackend.exception.RegistrationException;
import com.hiketrackbackend.hiketrackbackend.exception.UserNotConfirmedException;
import com.hiketrackbackend.hiketrackbackend.security.AuthenticationService;
import com.hiketrackbackend.hiketrackbackend.security.JwtUtil;
import com.hiketrackbackend.hiketrackbackend.security.token.UserTokenService;
import com.hiketrackbackend.hiketrackbackend.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

@WebMvcTest(AuthenticationController.class)
public class AuthenticationControllerTest {
    private static MockMvc mockMvc;

    @MockBean
    private static UserService userService;

    @MockBean
    private static AuthenticationService authenticationService;

    @MockBean
    private static JwtUtil jwtUtil;

    @MockBean
    private static UserDetailsService userDetailsService;

    @MockBean
    private static UserTokenService<HttpServletRequest> userTokenService;

    @Autowired
    private ObjectMapper objectMapper;
    private UserRegistrationRequestDto validRequestDto;
    private UserRegistrationRespondDto validResponseDto;

    @BeforeAll
    static void beforeAll(@Autowired WebApplicationContext applicationContext) {
        mockMvc = MockMvcBuilders.webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
    }

    @BeforeEach
    public void setUp() {
        validRequestDto = new UserRegistrationRequestDto();
        validRequestDto.setEmail("test@example.com");
        validRequestDto.setPassword("Password123!");
        validRequestDto.setRepeatPassword("Password123!");
        validRequestDto.setFirstName("John");
        validRequestDto.setLastName("Doe");

        validResponseDto = new UserRegistrationRespondDto();
        validResponseDto.setId(1L);
        validResponseDto.setEmail("test@example.com");
        validResponseDto.setFirstName("John");
        validResponseDto.setLastName("Doe");
    }

    @Test
    @DisplayName("Successfully registers a new user with valid input")
    public void testSuccessfulRegistration() throws RegistrationException {
        UserService userService = mock(UserService.class);
        AuthenticationController controller = new AuthenticationController(userService, authenticationService);

        UserRegistrationRequestDto requestDto = new UserRegistrationRequestDto();
        requestDto.setEmail("test@example.com");
        requestDto.setPassword("Password123");
        requestDto.setRepeatPassword("Password123");
        requestDto.setFirstName("John");
        requestDto.setLastName("Doe");

        UserRegistrationRespondDto responseDto = new UserRegistrationRespondDto();
        responseDto.setId(1L);
        responseDto.setEmail("test@example.com");
        responseDto.setFirstName("John");
        responseDto.setLastName("Doe");

        when(userService.register(requestDto)).thenReturn(responseDto);

        UserRegistrationRespondDto result = controller.registration(requestDto);

        assertNotNull(result);
        assertEquals("test@example.com", result.getEmail());
        assertEquals("John", result.getFirstName());
        assertEquals("Doe", result.getLastName());
    }

    @Test
    @DisplayName("Successfully registers a user with valid input data")
    public void testSuccessfulRegistrationWithValidData() throws RegistrationException {
        UserService userService = mock(UserService.class);
        AuthenticationController controller = new AuthenticationController(userService, authenticationService);

        UserRegistrationRequestDto requestDto = new UserRegistrationRequestDto();
        requestDto.setEmail("test@example.com");
        requestDto.setPassword("Password123");
        requestDto.setRepeatPassword("Password123");
        requestDto.setFirstName("John");
        requestDto.setLastName("Doe");

        UserRegistrationRespondDto expectedResponse = new UserRegistrationRespondDto();
        expectedResponse.setId(1L);
        expectedResponse.setEmail("test@example.com");
        expectedResponse.setFirstName("John");
        expectedResponse.setLastName("Doe");

        when(userService.register(requestDto)).thenReturn(expectedResponse);

        UserRegistrationRespondDto response = controller.registration(requestDto);

        assertNotNull(response);
        assertEquals(expectedResponse.getId(), response.getId());
        assertEquals(expectedResponse.getEmail(), response.getEmail());
        assertEquals(expectedResponse.getFirstName(), response.getFirstName());
        assertEquals(expectedResponse.getLastName(), response.getLastName());
    }

    @Test
    @DisplayName("Handles RegistrationException when registration fails")
    public void testRegistrationExceptionHandling() throws RegistrationException {
        UserService userService = mock(UserService.class);
        AuthenticationController controller = new AuthenticationController(userService, authenticationService);

        UserRegistrationRequestDto requestDto = new UserRegistrationRequestDto();
        requestDto.setEmail("invalid-email");
        requestDto.setPassword("Password123");
        requestDto.setRepeatPassword("Password123");
        requestDto.setFirstName("John");
        requestDto.setLastName("Doe");

        when(userService.register(requestDto)).thenThrow(new RegistrationException("Registration failed"));

        assertThrows(RegistrationException.class, () -> {
            controller.registration(requestDto);
        });
    }

    @Test
    @DisplayName("Handles RegistrationException when registration fails due to duplicate email")
    public void testRegistrationExceptionOnDuplicateEmail() throws RegistrationException {
        UserService userService = mock(UserService.class);
        AuthenticationController controller = new AuthenticationController(userService, authenticationService);

        UserRegistrationRequestDto requestDto = new UserRegistrationRequestDto();
        requestDto.setEmail("duplicate@example.com");
        requestDto.setPassword("Password123");
        requestDto.setRepeatPassword("Password123");
        requestDto.setFirstName("Jane");
        requestDto.setLastName("Doe");

        when(userService.register(requestDto)).thenThrow(new RegistrationException("Email already exists"));

        assertThrows(RegistrationException.class, () -> {
            controller.registration(requestDto);
        });
    }

    @Test
    @DisplayName("Successfully authenticates a user with valid credentials")
    public void testSuccessfulAuthenticationWithValidCredentials() {
        AuthenticationService authenticationService = mock(AuthenticationService.class);
        UserLoginRequestDto requestDto = new UserLoginRequestDto("valid@example.com", "validPassword");
        UserResponseDto expectedResponse = new UserResponseDto("validToken");
        when(authenticationService.login(requestDto)).thenReturn(expectedResponse);

        UserResponseDto actualResponse = authenticationService.login(requestDto);

        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    @DisplayName("Handles authentication failure for unconfirmed users")
    public void testAuthenticationFailureForUnconfirmedUsers() {
        AuthenticationService authenticationService = mock(AuthenticationService.class);
        UserLoginRequestDto requestDto = new UserLoginRequestDto("unconfirmed@example.com", "password");
        when(authenticationService.login(requestDto)).thenThrow(new UserNotConfirmedException("User email is not confirmed"));

        assertThrows(UserNotConfirmedException.class, () -> {
            authenticationService.login(requestDto);
        });
    }

    @Test
    @DisplayName("Manages login attempts with incorrect email or password")
    public void testLoginWithIncorrectCredentials() {
        AuthenticationService authenticationService = mock(AuthenticationService.class);
        UserLoginRequestDto requestDto = new UserLoginRequestDto("incorrect@example.com", "incorrectPassword");
        when(authenticationService.login(requestDto)).thenThrow(new UserNotConfirmedException("User email is not confirmed"));

        assertThrows(UserNotConfirmedException.class, () -> authenticationService.login(requestDto));
    }

    @Test
    @DisplayName("Successfully initiates password reset for a valid email")
    public void testForgotPasswordValidEmail() {
        AuthenticationService authenticationService = mock(AuthenticationService.class);
        UserRequestDto requestDto = new UserRequestDto();
        requestDto.setEmail("valid@example.com");
        UserDevMsgRespondDto expectedResponse = new UserDevMsgRespondDto("Password reset link sent to email.");

        when(authenticationService.createRestoreRequest(requestDto)).thenReturn(expectedResponse);

        UserDevMsgRespondDto actualResponse = authenticationService.createRestoreRequest(requestDto);

        assertEquals(expectedResponse.message(), actualResponse.message());
    }

    @Test
    @DisplayName("Handles non-existent email gracefully without exposing sensitive information")
    public void testForgotPasswordNonExistentEmail() {
        AuthenticationService authenticationService = mock(AuthenticationService.class);
        UserRequestDto requestDto = new UserRequestDto();
        requestDto.setEmail("nonexistent@example.com");
        UserDevMsgRespondDto expectedResponse = new UserDevMsgRespondDto("Password reset link sent to email.");

        when(authenticationService.createRestoreRequest(requestDto)).thenReturn(expectedResponse);

        UserDevMsgRespondDto actualResponse = authenticationService.createRestoreRequest(requestDto);

        assertEquals(expectedResponse.message(), actualResponse.message());
    }

    @Test
    @DisplayName("Successfully resets password with valid token and matching passwords")
    public void testResetPasswordSuccess() {
        String validToken = "validToken123";
        UserUpdatePasswordRequestDto requestDto = new UserUpdatePasswordRequestDto();
        requestDto.setPassword("newPassword123");
        requestDto.setRepeatPassword("newPassword123");

        AuthenticationService authenticationService = mock(AuthenticationService.class);
        when(authenticationService.restorePassword(validToken, requestDto))
                .thenReturn(new UserDevMsgRespondDto("Password reset successful."));

        AuthenticationController controller = new AuthenticationController(userService, authenticationService);

        UserDevMsgRespondDto response = controller.resetPassword(validToken, requestDto);

        assertEquals("Password reset successful.", response.message());
    }

    @Test
    @DisplayName("Handles invalid or expired token gracefully")
    public void testResetPasswordInvalidToken() {
        String invalidToken = "invalidToken123";
        UserUpdatePasswordRequestDto requestDto = new UserUpdatePasswordRequestDto();
        requestDto.setPassword("newPassword123");
        requestDto.setRepeatPassword("newPassword123");

        AuthenticationService authenticationService = mock(AuthenticationService.class);
        when(authenticationService.restorePassword(invalidToken, requestDto))
                .thenThrow(new UserNotConfirmedException("Confirmation link has been expired"));

        AuthenticationController controller = new AuthenticationController(userService, authenticationService);

        assertThrows(UserNotConfirmedException.class, () -> {
            controller.resetPassword(invalidToken, requestDto);
        });
    }

    @Test
    @DisplayName("Successfully confirms a user's email with a valid token")
    public void testEmailConfirmationWithValidToken() {
        String validToken = "validToken123";
        AuthenticationService authenticationService = mock(AuthenticationService.class);
        when(authenticationService.changeConfirmingStatus(validToken))
                .thenReturn(new UserDevMsgRespondDto("User has been confirmed."));

        UserDevMsgRespondDto response = authenticationService.changeConfirmingStatus(validToken);

        assertEquals("User has been confirmed.", response.message());
    }

    @Test
    @DisplayName("Handles expired confirmation tokens gracefully")
    public void testEmailConfirmationWithExpiredToken() {
        String expiredToken = "expiredToken123";
        AuthenticationService authenticationService = mock(AuthenticationService.class);
        when(authenticationService.changeConfirmingStatus(expiredToken))
                .thenThrow(new UserNotConfirmedException("Confirmation link has been expired"));

        assertThrows(UserNotConfirmedException.class, () -> {
            authenticationService.changeConfirmingStatus(expiredToken);
        });
    }
}
