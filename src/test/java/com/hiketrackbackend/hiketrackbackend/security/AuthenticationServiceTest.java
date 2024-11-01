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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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
    void testLoginWhenUserNotConfirmedThenThrowUserNotConfirmedException() {
        // Arrange
        String email = "test@example.com";
        String password = "password";
        UserLoginRequestDto requestDto = new UserLoginRequestDto(email, password);
        User user = new User();
        user.setEmail(email);
        user.setPassword(password);
        user.setConfirmed(false);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        // Act & Assert
        assertThrows(UserNotConfirmedException.class, () -> {
            authenticationService.login(requestDto);
        });
    }

    @Test
    void testLoginWhenUserConfirmedThenReturnUserResponseDto() {
        // Arrange
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

        // Act
        UserResponseDto responseDto = authenticationService.login(requestDto);

        // Assert
        assertEquals(token, responseDto.Token());
    }

    @Test
    void testCreateRestoreRequestWhenUserFoundThenReturnUserDevMsgRespondDto() {
        // Arrange
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

        // Act
        UserDevMsgRespondDto responseDto = authenticationService.createRestoreRequest(requestDto);

        // Assert
        assertEquals("Password reset link sent to email.", responseDto.message());
    }

    @Test
    void testCreateRestoreRequestWhenUserNotFoundThenThrowEntityNotFoundException() {
        // Arrange
        String email = "test@example.com";
        UserRequestDto requestDto = new UserRequestDto();
        requestDto.setEmail(email);

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> {
            authenticationService.createRestoreRequest(requestDto);
        });
    }

    @Test
    void testRestorePasswordWhenTokenInvalidThenThrowException() {
        // Arrange
        String token = "invalidToken";
        UserUpdatePasswordRequestDto requestDto = new UserUpdatePasswordRequestDto();

        when(passwordResetTokenService.getValue(token)).thenThrow(new EntityNotFoundException("Invalid token"));

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> {
            authenticationService.restorePassword(token, requestDto);
        });
    }

    @Test
    void testRestorePasswordWhenUserNotFoundThenThrowException() {
        // Arrange
        String token = "validToken";
        String email = "test@example.com";
        UserUpdatePasswordRequestDto requestDto = new UserUpdatePasswordRequestDto();

        when(passwordResetTokenService.getValue(token)).thenReturn(email);
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> {
            authenticationService.restorePassword(token, requestDto);
        });
    }

    @Test
    void testRestorePasswordWhenPasswordRestoredThenReturnUserDevMsgRespondDto() {
        // Arrange
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

        // Act
        UserDevMsgRespondDto responseDto = authenticationService.restorePassword(token, requestDto);

        // Assert
        assertEquals(expectedResponse.message(), responseDto.message());
    }

    @Test
    void testLogoutWhenCalledThenLogoutTokenServiceSaveCalled() {
        // Arrange
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpSession session = mock(HttpSession.class);
        String token = "someToken";

        when(request.getSession()).thenReturn(session);
        doNothing().when(session).invalidate();
        when(logoutTokenService.save(request)).thenReturn(token);

        // Act
        authenticationService.logout(request);

        // Assert
        verify(session).invalidate();
        verify(logoutTokenService).save(request);
    }

    @Test
    void testChangeConfirmingStatusWhenTokenIsValidThenUserConfirmed() {
        // Arrange
        String token = "validToken";
        String email = "test@example.com";
        User user = new User();
        user.setEmail(email);
        user.setConfirmed(false);

        when(confirmationTokenService.isKeyExist(token)).thenReturn(true);
        when(confirmationTokenService.getValue(token)).thenReturn(email);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        // Act
        UserDevMsgRespondDto responseDto = authenticationService.changeConfirmingStatus(token);

        // Assert
        assertEquals("User has been confirmed.", responseDto.message());
        assertTrue(user.isConfirmed());
    }

    @Test
    void testChangeConfirmingStatusWhenTokenIsInvalidThenThrowException() {
        // Arrange
        String token = "invalidToken";

        when(confirmationTokenService.isKeyExist(token)).thenReturn(false);

        // Act & Assert
        assertThrows(UserNotConfirmedException.class, () -> {
            authenticationService.changeConfirmingStatus(token);
        });
    }

    @Test
    void testChangeConfirmingStatusWhenUserNotFoundThenThrowException() {
        // Arrange
        String token = "validToken";
        String email = "test@example.com";

        when(confirmationTokenService.isKeyExist(token)).thenReturn(true);
        when(confirmationTokenService.getValue(token)).thenReturn(email);
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> {
            authenticationService.changeConfirmingStatus(token);
        });
    }
}