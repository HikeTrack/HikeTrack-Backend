package com.hiketrackbackend.hiketrackbackend.security;

import com.hiketrackbackend.hiketrackbackend.dto.user.UserLoginRequestDto;
import com.hiketrackbackend.hiketrackbackend.dto.user.UserLoginResponseDto;
import com.hiketrackbackend.hiketrackbackend.model.User;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final JwtTokenServiceImpl jwtTokenService;

    public UserLoginResponseDto login(UserLoginRequestDto requestDto) {
        final Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(requestDto.email(), requestDto.password())
        );
        String token = jwtUtil.generateToken(authentication.getName());
        return new UserLoginResponseDto(token);
    }

    public void logout(HttpServletRequest request, String email) {
        addJwtTokenToBlackList(request, email);
    }

    private void addJwtTokenToBlackList(HttpServletRequest request, String username) {
        jwtTokenService.saveTokenToDB(request, username);
    }

    public Long getUserId(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return user.getId();
    }
}
