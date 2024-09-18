package com.hiketrackbackend.hiketrackbackend.service.impl;

import com.hiketrackbackend.hiketrackbackend.dto.user.UserRegistrationRequestDto;
import com.hiketrackbackend.hiketrackbackend.dto.user.UserRegistrationRespondDto;
import com.hiketrackbackend.hiketrackbackend.exception.RegistrationException;
import com.hiketrackbackend.hiketrackbackend.mapper.UserMapper;
import com.hiketrackbackend.hiketrackbackend.model.Role;
import com.hiketrackbackend.hiketrackbackend.model.User;
import com.hiketrackbackend.hiketrackbackend.model.UserProfile;
import com.hiketrackbackend.hiketrackbackend.repository.RoleRepository;
import com.hiketrackbackend.hiketrackbackend.repository.UserRepository;
import com.hiketrackbackend.hiketrackbackend.service.UserProfileService;
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
    private final UserProfileService userProfileService;

    @Override
    @Transactional
    public UserRegistrationRespondDto register(UserRegistrationRequestDto request) throws RegistrationException {
        if (userRepository.existsUserByEmail(request.getEmail())) {
            throw new RegistrationException("This email is already used: " + request.getEmail());
        }
        User user = userMapper.toEntity(request);
        setUserPassword(user, request);
        setUserRole(user);
        setUserProfile(user);
        userRepository.save(user);
        return userMapper.toDto(user);
    }

    private void setUserProfile(User user) {
        UserProfile userProfile = userProfileService.createUserProfile(user);
        user.setUserProfile(userProfile);
    }

    private void setUserRole(User user) {
        Set<Role> roles = roleRepository.findByName(Role.RoleName.ROLE_USER);
        user.setRoles(roles);
    }

    private void setUserPassword(User user, UserRegistrationRequestDto request) {
        user.setPassword(encoder.encode(request.getPassword()));
    }
}
