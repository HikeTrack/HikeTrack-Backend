package com.hiketrackbackend.hiketrackbackend.security;

import com.hiketrackbackend.hiketrackbackend.dto.UserDevMsgRespondDto;
import com.hiketrackbackend.hiketrackbackend.dto.user.UserRequestDto;
import com.hiketrackbackend.hiketrackbackend.dto.user.login.UserLoginRequestDto;
import com.hiketrackbackend.hiketrackbackend.dto.user.login.UserResponseDto;
import com.hiketrackbackend.hiketrackbackend.dto.user.update.UserUpdatePasswordRequestDto;
import com.hiketrackbackend.hiketrackbackend.exception.EntityNotFoundException;
import com.hiketrackbackend.hiketrackbackend.exception.UserNotConfirmedException;
import com.hiketrackbackend.hiketrackbackend.mapper.UserMapper;
import com.hiketrackbackend.hiketrackbackend.model.user.User;
import com.hiketrackbackend.hiketrackbackend.repository.UserRepository;
import com.hiketrackbackend.hiketrackbackend.security.token.impl.ConfirmationTokenService;
import com.hiketrackbackend.hiketrackbackend.security.token.impl.LogoutTokenService;
import com.hiketrackbackend.hiketrackbackend.security.token.impl.PasswordResetUserTokenService;
import com.hiketrackbackend.hiketrackbackend.service.UserService;
import com.hiketrackbackend.hiketrackbackend.service.notification.EmailSender;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthenticationServiceTest {
    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordResetUserTokenService passwordResetTokenService;

    @Mock
    private EmailSender passwordResetEmailSenderImpl;

    @Mock
    private UserMapper userMapper;

    @Mock
    private UserService userService;

    @Mock
    private ConfirmationTokenService confirmationTokenService;

    @Mock
    private LogoutTokenService logoutTokenService;

    @InjectMocks
    private AuthenticationService authenticationService;

    @Test
    @DisplayName("Fail to login not confirmed user")
    void testLoginWhenUserNotConfirmedThenThrowUserNotConfirmedException() {
        String email = "test@example.com";
        String password = "password";
        UserLoginRequestDto requestDto = new UserLoginRequestDto(email, password);
        User user = new User();
        user.setEmail(email);
        user.setPassword(password);
        user.setConfirmed(false);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        assertThrows(UserNotConfirmedException.class, () -> {
            authenticationService.login(requestDto);
        });
    }

    @Test
    @DisplayName("Success login confirmed user")
    void testLoginWhenUserConfirmedThenReturnUserResponseDto() {
        String email = "test@example.com";
        String password = "password";
        String token = "generatedToken";
        UserLoginRequestDto requestDto = new UserLoginRequestDto(email, password);
        User user = new User();
        user.setEmail(email);
        user.setPassword(password);
        user.setConfirmed(true);

        Authentication authentication = new UsernamePasswordAuthenticationToken(email, password);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password))).thenReturn(authentication);
        when(jwtUtil.generateToken(email)).thenReturn(token);

        UserResponseDto responseDto = authenticationService.login(requestDto);

        assertEquals(token, responseDto.token());
    }

    @Test
    @DisplayName("Create restore request with all valid data")
    void testCreateRestoreRequestWhenUserFoundThenReturnUserDevMsgRespondDto() {
        String email = "test@example.com";
        String token = "resetToken";
        UserRequestDto requestDto = new UserRequestDto();
        requestDto.setEmail(email);
        User user = new User();
        user.setEmail(email);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(passwordResetTokenService.save(email)).thenReturn(token);
        doNothing().when(passwordResetEmailSenderImpl).send(email, token);
        when(userMapper.toDto("Password reset link sent to email.")).thenReturn(new UserDevMsgRespondDto("Password reset link sent to email."));

        UserDevMsgRespondDto responseDto = authenticationService.createRestoreRequest(requestDto);

        assertEquals("Password reset link sent to email.", responseDto.message());
    }

    @Test
    @DisplayName("Fail to create restore request with not exist user")
    void testCreateRestoreRequestWhenUserNotFoundThenThrowEntityNotFoundException() {
        String email = "test@example.com";
        UserRequestDto requestDto = new UserRequestDto();
        requestDto.setEmail(email);

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            authenticationService.createRestoreRequest(requestDto);
        });
    }

    @Test
    @DisplayName("Fail to restore password with not valid token")
    void testRestorePasswordWhenTokenInvalidThenThrowException() {
        String token = "invalidToken";
        UserUpdatePasswordRequestDto requestDto = new UserUpdatePasswordRequestDto();

        when(passwordResetTokenService.getValue(token)).thenThrow(new EntityNotFoundException("Invalid token"));

        assertThrows(EntityNotFoundException.class, () -> {
            authenticationService.restorePassword(token, requestDto);
        });
    }

    @Test
    @DisplayName("Fail to restore password for not exist user")
    void testRestorePasswordWhenUserNotFoundThenThrowException() {
        String token = "validToken";
        String email = "test@example.com";
        UserUpdatePasswordRequestDto requestDto = new UserUpdatePasswordRequestDto();

        when(passwordResetTokenService.getValue(token)).thenReturn(email);
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            authenticationService.restorePassword(token, requestDto);
        });
    }

    @Test
    @DisplayName("Successfully restore password")
    void testRestorePasswordWhenPasswordRestoredThenReturnUserDevMsgRespondDto() {
        String token = "validToken";
        String email = "test@example.com";
        Long userId = 1L;
        UserUpdatePasswordRequestDto requestDto = new UserUpdatePasswordRequestDto();
        User user = new User();
        user.setEmail(email);
        user.setId(userId);
        UserDevMsgRespondDto expectedResponse = new UserDevMsgRespondDto("Password updated successfully");

        when(passwordResetTokenService.getValue(token)).thenReturn(email);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        doNothing().when(passwordResetTokenService).delete(token);
        when(userService.updatePassword(requestDto, userId)).thenReturn(expectedResponse);

        UserDevMsgRespondDto responseDto = authenticationService.restorePassword(token, requestDto);

        assertEquals(expectedResponse.message(), responseDto.message());
    }

    @Test
    @DisplayName("Successfully logout")
    void testLogoutWhenCalledThenLogoutTokenServiceSaveCalled() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpSession session = mock(HttpSession.class);
        String token = "someToken";

        when(request.getSession()).thenReturn(session);
        doNothing().when(session).invalidate();
        when(logoutTokenService.save(request)).thenReturn(token);

        authenticationService.logout(request);

        verify(session).invalidate();
        verify(logoutTokenService).save(request);
    }

    @Test
    @DisplayName("Change confirm status with all valid data")
    void testChangeConfirmingStatusWhenTokenIsValidThenUserConfirmed() {
        String token = "validToken";
        String email = "test@example.com";
        User user = new User();
        user.setEmail(email);
        user.setConfirmed(false);

        when(confirmationTokenService.isKeyExist(token)).thenReturn(true);
        when(confirmationTokenService.getValue(token)).thenReturn(email);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        UserDevMsgRespondDto responseDto = authenticationService.changeConfirmingStatus(token);

        assertEquals("User has been confirmed.", responseDto.message());
        assertTrue(user.isConfirmed());
    }

    @Test
    @DisplayName("Fail to change status to user with not valid token")
    void testChangeConfirmingStatusWhenTokenIsInvalidThenThrowException() {
        String token = "invalidToken";

        when(confirmationTokenService.isKeyExist(token)).thenReturn(false);

        assertThrows(UserNotConfirmedException.class, () -> {
            authenticationService.changeConfirmingStatus(token);
        });
    }

    @Test
    @DisplayName("Fail to change confirm status to not exist user")
    void testChangeConfirmingStatusWhenUserNotFoundThenThrowException() {
        String token = "validToken";
        String email = "test@example.com";

        when(confirmationTokenService.isKeyExist(token)).thenReturn(true);
        when(confirmationTokenService.getValue(token)).thenReturn(email);
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            authenticationService.changeConfirmingStatus(token);
        });
    }
}