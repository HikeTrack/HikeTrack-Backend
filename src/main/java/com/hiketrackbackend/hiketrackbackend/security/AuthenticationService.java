package com.hiketrackbackend.hiketrackbackend.security;

import com.hiketrackbackend.hiketrackbackend.dto.user.login.UserLoginRequestDto;
import com.hiketrackbackend.hiketrackbackend.dto.user.login.UserResponseDto;
import com.hiketrackbackend.hiketrackbackend.dto.user.UserRequestDto;
import com.hiketrackbackend.hiketrackbackend.dto.user.UserDevMsgRespondDto;
import com.hiketrackbackend.hiketrackbackend.dto.user.update.UserUpdatePasswordRequestDto;
import com.hiketrackbackend.hiketrackbackend.exception.EntityNotFoundException;
import com.hiketrackbackend.hiketrackbackend.mapper.UserMapper;
import com.hiketrackbackend.hiketrackbackend.model.User;
import com.hiketrackbackend.hiketrackbackend.repository.UserRepository;
import com.hiketrackbackend.hiketrackbackend.security.token.UserTokenService;
import com.hiketrackbackend.hiketrackbackend.service.UserService;
import com.hiketrackbackend.hiketrackbackend.service.notification.MailSender;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final UserTokenService<String> PasswordResetTokenService;
    private final UserTokenService<HttpServletRequest> LogoutTokenService;
    private final MailSender mailSender;
    private final UserMapper userMapper;
    private final UserService userService;

    public UserResponseDto login(UserLoginRequestDto requestDto) {
        final Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(requestDto.email(), requestDto.password())
        );
        String token = jwtUtil.generateToken(authentication.getName());
        return new UserResponseDto(token);
    }

    @Transactional
    public UserDevMsgRespondDto createRestoreRequest(UserRequestDto request) {
        User user = findUserByEmail(request.getEmail());
        String token = PasswordResetTokenService.save(user.getEmail());
        mailSender.sendMessage(user.getEmail(), token);
        return userMapper.toDto("Password reset link sent to email.");
    }

    @Transactional
    public UserDevMsgRespondDto restorePassword(String token, UserUpdatePasswordRequestDto request) {
        String email = PasswordResetTokenService.getValue(token);
        User user = findUserByEmail(email);
        return userService.updatePassword(request, user.getId());
    }

    public void logout(HttpServletRequest request) {
        request.getSession().invalidate();
        LogoutTokenService.save(request);
    }

    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(
                () -> new EntityNotFoundException("User with email " + email + " not found")
        );
    }
}
