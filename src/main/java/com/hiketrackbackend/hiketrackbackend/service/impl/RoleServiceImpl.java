package com.hiketrackbackend.hiketrackbackend.service.impl;

import com.hiketrackbackend.hiketrackbackend.dto.user.UserDevMsgRespondDto;
import com.hiketrackbackend.hiketrackbackend.dto.user.UserRequestDto;
import com.hiketrackbackend.hiketrackbackend.exception.EntityNotFoundException;
import com.hiketrackbackend.hiketrackbackend.model.Role;
import com.hiketrackbackend.hiketrackbackend.model.User;
import com.hiketrackbackend.hiketrackbackend.repository.UserRepository;
import com.hiketrackbackend.hiketrackbackend.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {
    private final UserRepository userRepository;

    @Override
    public Role createUserDefaultRole() {
        return new Role(Role.RoleName.ROLE_USER);
    }

    @Override
    @Transactional
    public UserDevMsgRespondDto changeUserRoleToGuide(UserRequestDto request) {
        User user = findUserByEmail(request.getEmail());
        Set<Role> roles = user.getRoles();
        roles.add(new Role(Role.RoleName.ROLE_GUIDE));
        userRepository.save(user);
        return new UserDevMsgRespondDto("You have successfully changed the role: " + Role.RoleName.ROLE_GUIDE);
    }

    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(
                () -> new EntityNotFoundException("User with email " + email + " not found")
        );
    }
}
