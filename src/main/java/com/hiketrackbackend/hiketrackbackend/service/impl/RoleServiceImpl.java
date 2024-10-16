package com.hiketrackbackend.hiketrackbackend.service.impl;

import com.hiketrackbackend.hiketrackbackend.dto.user.UserDevMsgRespondDto;
import com.hiketrackbackend.hiketrackbackend.dto.user.UserRequestDto;
import com.hiketrackbackend.hiketrackbackend.exception.EntityNotFoundException;
import com.hiketrackbackend.hiketrackbackend.model.Role;
import com.hiketrackbackend.hiketrackbackend.model.User;
import com.hiketrackbackend.hiketrackbackend.repository.RoleRepository;
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
    private final RoleRepository roleRepository;

    @Override
    public void setUserDefaultRole(User user) {
        Role roleUser = findByName(Role.RoleName.ROLE_USER);
        user.setRoles(Set.of(roleUser));
    }

    @Override
    @Transactional
    public UserDevMsgRespondDto changeUserRoleToGuide(UserRequestDto request) {
        User user = findUserByEmail(request.getEmail());
        Set<Role> roles = user.getRoles();
        Role roleGuide = findByName(Role.RoleName.ROLE_GUIDE);
        roles.add(roleGuide);
        user.setRoles(roles);
        userRepository.save(user);
        return new UserDevMsgRespondDto("You have successfully changed the role: " + Role.RoleName.ROLE_GUIDE);
    }

    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(
                () -> new EntityNotFoundException("User with email " + email + " not found")
        );
    }

    private Role findByName(Role.RoleName roleName) {
        return roleRepository.findByName(roleName);
    }
}
