package com.hiketrackbackend.hiketrackbackend.security;

import com.hiketrackbackend.hiketrackbackend.dto.UserDevMsgRespondDto;
import com.hiketrackbackend.hiketrackbackend.dto.user.UserRequestDto;
import com.hiketrackbackend.hiketrackbackend.dto.user.login.UserLoginRequestDto;
import com.hiketrackbackend.hiketrackbackend.dto.user.login.UserResponseDto;
import com.hiketrackbackend.hiketrackbackend.dto.user.update.UserUpdatePasswordRequestDto;
import com.hiketrackbackend.hiketrackbackend.exception.EntityNotFoundException;
import com.hiketrackbackend.hiketrackbackend.exception.InvalidTokenException;
import com.hiketrackbackend.hiketrackbackend.exception.UserNotConfirmedException;
import com.hiketrackbackend.hiketrackbackend.mapper.UserMapper;
import com.hiketrackbackend.hiketrackbackend.model.user.User;
import com.hiketrackbackend.hiketrackbackend.repository.UserRepository;
import com.hiketrackbackend.hiketrackbackend.security.token.impl.ConfirmationTokenService;
import com.hiketrackbackend.hiketrackbackend.security.token.impl.GoogleLogInTokenService;
import com.hiketrackbackend.hiketrackbackend.security.token.impl.LogoutTokenService;
import com.hiketrackbackend.hiketrackbackend.security.token.impl.PasswordResetUserTokenService;
import com.hiketrackbackend.hiketrackbackend.service.UserService;
import com.hiketrackbackend.hiketrackbackend.service.notification.EmailSender;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthenticationService {
    private final AuthenticationManager authenticationManager;

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final PasswordResetUserTokenService passwordResetTokenService;
    private final LogoutTokenService logoutTokenService;
    private final EmailSender passwordResetEmailSenderImpl;
    private final UserMapper userMapper;
    private final UserService userService;
    private final ConfirmationTokenService confirmationTokenService;
    private final GoogleLogInTokenService googleLogInTokenService;
    private final EmailSender confirmationEmailSenderImpl;

    public AuthenticationService(
            AuthenticationManager authenticationManager,
            JwtUtil jwtUtil,
            UserRepository userRepository,
            PasswordResetUserTokenService passwordResetTokenService,
            LogoutTokenService logoutTokenService,
            @Qualifier("passwordResetEmailSenderImpl")
            EmailSender passwordResetEmailSenderImpl,
            UserMapper userMapper,
            UserService userService,
            ConfirmationTokenService confirmationTokenService,
            GoogleLogInTokenService googleLogInTokenService,
            @Qualifier("confirmationRequestEmailSenderImpl")
            EmailSender confirmationEmailSenderImpl
    ) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        this.passwordResetTokenService = passwordResetTokenService;
        this.logoutTokenService = logoutTokenService;
        this.passwordResetEmailSenderImpl = passwordResetEmailSenderImpl;
        this.userMapper = userMapper;
        this.userService = userService;
        this.confirmationTokenService = confirmationTokenService;
        this.googleLogInTokenService = googleLogInTokenService;
        this.confirmationEmailSenderImpl = confirmationEmailSenderImpl;
    }

    public UserResponseDto login(UserLoginRequestDto requestDto) {
        final Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(requestDto.email(), requestDto.password())
        );
        User user = findUserByEmail(requestDto.email());
        if (!user.isConfirmed()) {
            throw new UserNotConfirmedException("User email is not confirmed");
        }
        String token = jwtUtil.generateToken(authentication.getName());
        return new UserResponseDto(token);
    }

    @Transactional
    public UserDevMsgRespondDto createRestoreRequest(UserRequestDto request) {
        User user = findUserByEmail(request.getEmail());
        String token = passwordResetTokenService.save(user.getEmail());
        passwordResetEmailSenderImpl.send(user.getEmail(), token);
        return userMapper.toDto("Password reset link sent to email.");
    }

    @Transactional
    public UserDevMsgRespondDto restorePassword(
            String token,
            UserUpdatePasswordRequestDto request
    ) {
        String email = passwordResetTokenService.getValue(token);
        User user = findUserByEmail(email);
        passwordResetTokenService.delete(token);
        return userService.updatePassword(request, user.getId());
    }

    public void logout(@NotNull HttpServletRequest request) {
        request.getSession().invalidate();
        logoutTokenService.save(request);
    }

    @Transactional
    public UserDevMsgRespondDto changeConfirmingStatus(String token) {
        boolean exist = confirmationTokenService.isKeyExist(token);
        if (!exist) {
            throw new UserNotConfirmedException("Confirmation link has been expired");
        }

        String email = confirmationTokenService.getValue(token);
        User user = findUserByEmail(email);
        user.setConfirmed(true);
        userRepository.save(user);
        confirmationTokenService.delete(token);
        return new UserDevMsgRespondDto("User has been confirmed.");
    }

    public UserDevMsgRespondDto repeatEmailConfirmation(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new EntityNotFoundException("Can`t find user with email " + email));
        if (user.isConfirmed()) {
            return new UserDevMsgRespondDto("User already confirmed");
        }
        String token = confirmationTokenService.save(user.getEmail());
        confirmationEmailSenderImpl.send(email, token);
        return new UserDevMsgRespondDto("New confirmation request has been sent");
    }

    @Transactional
    public UserResponseDto getToken(String uuidToken) {
        boolean exist = googleLogInTokenService.isKeyExist(uuidToken);
        if (!exist) {
            throw new InvalidTokenException("Token is invalid or expired");
        }

        String email = googleLogInTokenService.getValue(uuidToken);
        String token = jwtUtil.generateToken(email);
        googleLogInTokenService.delete(uuidToken);
        return new UserResponseDto(token);
    }

    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(
                () -> new EntityNotFoundException("User with email " + email + " not found")
        );
    }
}
