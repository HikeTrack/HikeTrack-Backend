package com.hiketrackbackend.hiketrackbackend.service.impl;

import com.hiketrackbackend.hiketrackbackend.dto.user.*;
import com.hiketrackbackend.hiketrackbackend.exception.EntityNotFoundException;
import com.hiketrackbackend.hiketrackbackend.exception.RegistrationException;
import com.hiketrackbackend.hiketrackbackend.mapper.UserMapper;
import com.hiketrackbackend.hiketrackbackend.model.Role;
import com.hiketrackbackend.hiketrackbackend.model.User;
import com.hiketrackbackend.hiketrackbackend.repository.RoleRepository;
import com.hiketrackbackend.hiketrackbackend.repository.UserRepository;
import com.hiketrackbackend.hiketrackbackend.security.JwtTokenServiceImpl;
import com.hiketrackbackend.hiketrackbackend.security.JwtUtil;
import com.hiketrackbackend.hiketrackbackend.security.UUIDTokenServiceImpl;
import com.hiketrackbackend.hiketrackbackend.service.MailSender;

import com.hiketrackbackend.hiketrackbackend.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final RoleRepository roleRepository;
    private final PasswordEncoder encoder;
    private final UUIDTokenServiceImpl UUIDTokenService;;
    private final MailSender mailSender;
    private final JwtUtil jwtUtil;

    @Override
    @Transactional
    public UserRegistrationRespondDto register(UserRegistrationRequestDto request) throws RegistrationException {
        if (userRepository.existsUserByEmail(request.getEmail())) {
            throw new RegistrationException("This email is already used: " + request.getEmail());
        }
        User user = userMapper.toEntity(request);
        setUserPassword(user, request);
        setUserRole(user);
        userRepository.save(user);
        return userMapper.toDto(user);
    }

    @Override
    @Transactional
    public UserForgotRespondDto createRestoreRequest(UserForgotRequestDto request) {
        User user = findUserByEmail(request.getEmail());
        String token = UUID.randomUUID().toString();
        UUIDTokenService.saveTokenToDB(token, user.getEmail());
        mailSender.sendResetPasswordMailToGMail(user.getEmail(), token);
        return userMapper.toDto(user.getEmail());
    }

    @Override
    @Transactional
    public UserLoginResponseDto updatePassword(UserRestoreRequestDto request, String email) {
        User user = findUserByEmail(email);
        user.setPassword(encoder.encode(request.getPassword()));
        userRepository.save(user);
        String token = jwtUtil.generateToken(email);
        return new UserLoginResponseDto(token);
    }

    @Override
    public UserRegistrationRespondDto getById(Long id) {
        return userMapper.toDto(findUserById(id));
    }

    private void setUserRole(User user) {
        Set<Role> roles = roleRepository.findByName(Role.RoleName.ROLE_USER);
        user.setRoles(roles);
    }

    private void setUserPassword(User user, UserRegistrationRequestDto request) {
        user.setPassword(encoder.encode(request.getPassword()));
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
