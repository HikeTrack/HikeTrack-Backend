package com.hiketrackbackend.hiketrackbackend.security;

import com.hiketrackbackend.hiketrackbackend.dto.user.login.UserLoginRequestDto;
import com.hiketrackbackend.hiketrackbackend.dto.user.login.UserLoginResponseDto;
import com.hiketrackbackend.hiketrackbackend.dto.user.update.password.UserForgotPasswordRequestDto;
import com.hiketrackbackend.hiketrackbackend.dto.user.update.password.UserPasswordRespondDto;
import com.hiketrackbackend.hiketrackbackend.dto.user.update.password.UserUpdatePasswordRequestDto;

import com.hiketrackbackend.hiketrackbackend.exception.EntityNotFoundException;
import com.hiketrackbackend.hiketrackbackend.mapper.UserMapper;
import com.hiketrackbackend.hiketrackbackend.model.User;
import com.hiketrackbackend.hiketrackbackend.model.UserToken;
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
    private final JwtTokenServiceImpl jwtTokenService;
    private final UserRepository userRepository;
    private final UserTokenService userTokenService;
    private final MailSender mailSender;
    private final UserMapper userMapper;
    private final UserService userService;

    public UserLoginResponseDto login(UserLoginRequestDto requestDto) {
        final Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(requestDto.email(), requestDto.password())
        );
        String token = jwtUtil.generateToken(authentication.getName());
        return new UserLoginResponseDto(token);
    }

    @Transactional
    public UserPasswordRespondDto createRestoreRequest(UserForgotPasswordRequestDto request) {
        User user = findUserByEmail(request.getEmail());
        UserToken userToken = userTokenService.createToken(user.getId());
        mailSender.sendMessage(user.getEmail(), userToken.getToken());
        return userMapper.toDto("Password reset link sent to email.");
    }

    @Transactional
    public UserPasswordRespondDto restorePassword(String token, UserUpdatePasswordRequestDto request) {
        UserToken userToken = userTokenService.getUserToken(token);
        return userService.updatePassword(request, userToken.getUserId());
    }

//    public void logout(HttpServletRequest request, String email) {
//        addJwtTokenToBlackList(request, email);
//    }

    private void addJwtTokenToBlackList(HttpServletRequest request, String username) {
        jwtTokenService.saveTokenToDB(request, username);
    }

    public Long getUserId(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return user.getId();
    }

    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(
                () -> new EntityNotFoundException("User with email " + email + " not found")
        );
    }

    private User findUserById(Long id) {
        return userRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("User with id " + id + " not found")
        );
    }
}
