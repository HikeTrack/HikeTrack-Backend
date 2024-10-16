package com.hiketrackbackend.hiketrackbackend.service.impl;

import com.hiketrackbackend.hiketrackbackend.dto.user.UserRequestDto;
import com.hiketrackbackend.hiketrackbackend.dto.user.update.UserUpdateRequestDto;
import com.hiketrackbackend.hiketrackbackend.dto.user.UserRespondDto;
import com.hiketrackbackend.hiketrackbackend.dto.user.UserDevMsgRespondDto;
import com.hiketrackbackend.hiketrackbackend.dto.user.registration.UserRegistrationRequestDto;
import com.hiketrackbackend.hiketrackbackend.dto.user.registration.UserRegistrationRespondDto;
import com.hiketrackbackend.hiketrackbackend.dto.user.update.UserUpdatePasswordRequestDto;
import com.hiketrackbackend.hiketrackbackend.exception.EntityNotFoundException;
import com.hiketrackbackend.hiketrackbackend.exception.RegistrationException;
import com.hiketrackbackend.hiketrackbackend.mapper.UserMapper;
import com.hiketrackbackend.hiketrackbackend.model.User;
import com.hiketrackbackend.hiketrackbackend.repository.UserRepository;
import com.hiketrackbackend.hiketrackbackend.security.JwtUtil;
import com.hiketrackbackend.hiketrackbackend.service.RoleService;
import com.hiketrackbackend.hiketrackbackend.service.UserService;
import com.hiketrackbackend.hiketrackbackend.service.notification.EmailSender;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder encoder;
    private final RoleService roleService;
    private final EmailSender promotionRequestEmailSenderImpl;

    @Override
    @Transactional
    public UserRegistrationRespondDto register(UserRegistrationRequestDto request) throws RegistrationException {
        if (userRepository.existsUserByEmail(request.getEmail())) {
            throw new RegistrationException("This email is already used: " + request.getEmail());
        }
        User user = userMapper.toEntity(request);
        setUserPassword(user, request.getPassword());
        roleService.setUserDefaultRole(user);
        userRepository.save(user);
        return userMapper.toDto(user);
    }

    @Override
    @Transactional
    public UserDevMsgRespondDto updatePassword(UserUpdatePasswordRequestDto request, Long id) {
        User user = findUserById(id);
        setUserPassword(user, request.getPassword());
        userRepository.save(user);
        return userMapper.toDto("Password successfully changed.");
    }

    // TODO при апдейте мыла надо его как тообновлять в контексте
    @Override
    @Transactional
    public UserRespondDto updateUser(UserUpdateRequestDto requestDto, Long id) {
        User user = findUserById(id);
        userMapper.updateUserFromDto(requestDto, user);
        userMapper.updateUserProfileFromDto(requestDto.getUserProfileRequestDto(), user.getUserProfile());
        userRepository.save(user);
        return userMapper.toRespondDto(user);
    }

    @Override
    public UserRespondDto getLoggedInUser(HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Authorization header is missing or incorrect");
        }
        String token = authorizationHeader.substring(7);
        String username = jwtUtil.getUsername(token);
        User user = findUserByEmail(username);
        return userMapper.toRespondDto(findUserById(user.getId()));
    }

    @Override
    public void deleteUser(Long id) {
        findUserById(id);
        userRepository.deleteById(id);
    }

    // TODO later add some other info instead of second email
    @Override
    public UserDevMsgRespondDto promoteRequest(UserRequestDto request) {
        promotionRequestEmailSenderImpl.send(request.getEmail(), request.getEmail());
        return new UserDevMsgRespondDto("Request for promotion has been sent");
    }

    private void setUserPassword(User user, String password) {
        user.setPassword(encoder.encode(password));
    }

    private User findUserById(Long id) {
        return userRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("User with id " + id + " not found")
        );
    }

    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(
                () -> new EntityNotFoundException("User with email " + email + " not found")
        );
    }
}
