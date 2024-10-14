package com.hiketrackbackend.hiketrackbackend.service.impl;

import com.hiketrackbackend.hiketrackbackend.dto.user.update.UserUpdateRequestDto;
import com.hiketrackbackend.hiketrackbackend.dto.user.UserRespondDto;
import com.hiketrackbackend.hiketrackbackend.dto.user.update.password.UserPasswordRespondDto;
import com.hiketrackbackend.hiketrackbackend.dto.user.registration.UserRegistrationRequestDto;
import com.hiketrackbackend.hiketrackbackend.dto.user.registration.UserRegistrationRespondDto;
import com.hiketrackbackend.hiketrackbackend.dto.user.update.password.UserUpdatePasswordRequestDto;
import com.hiketrackbackend.hiketrackbackend.exception.EntityNotFoundException;
import com.hiketrackbackend.hiketrackbackend.exception.RegistrationException;
import com.hiketrackbackend.hiketrackbackend.mapper.UserMapper;
import com.hiketrackbackend.hiketrackbackend.model.Role;
import com.hiketrackbackend.hiketrackbackend.model.User;
import com.hiketrackbackend.hiketrackbackend.repository.RoleRepository;
import com.hiketrackbackend.hiketrackbackend.repository.UserRepository;
import com.hiketrackbackend.hiketrackbackend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final RoleRepository roleRepository;
    private final PasswordEncoder encoder;

    @Override
    @Transactional
    public UserRegistrationRespondDto register(UserRegistrationRequestDto request) throws RegistrationException {
        if (userRepository.existsUserByEmail(request.getEmail())) {
            throw new RegistrationException("This email is already used: " + request.getEmail());
        }
        User user = userMapper.toEntity(request);
        setUserPassword(user, request.getPassword());
        setUserRole(user);
        userRepository.save(user);
        return userMapper.toDto(user);
    }

    @Override
    @Transactional
    public UserPasswordRespondDto updatePassword(UserUpdatePasswordRequestDto request, Long id) {
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
    public UserRespondDto getUserById(Long id) {
        return userMapper.toRespondDto(findUserById(id));
    }

    @Override
    public void deleteUser(Long id) {
        findUserById(id);
        userRepository.deleteById(id);
    }

    private void setUserRole(User user) {
        Set<Role> roles = roleRepository.findByName(Role.RoleName.ROLE_USER);
        user.setRoles(roles);
    }

    private void setUserPassword(User user, String password) {
        user.setPassword(encoder.encode(password));
    }

    private User findUserById(Long id) {
        return userRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("User with id " + id + " not found")
        );
    }
}
