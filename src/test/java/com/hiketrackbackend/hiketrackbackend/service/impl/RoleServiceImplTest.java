package com.hiketrackbackend.hiketrackbackend.service.impl;

import com.hiketrackbackend.hiketrackbackend.dto.UserDevMsgRespondDto;
import com.hiketrackbackend.hiketrackbackend.dto.user.UserRequestDto;
import com.hiketrackbackend.hiketrackbackend.exception.EntityNotFoundException;
import com.hiketrackbackend.hiketrackbackend.model.user.Role;
import com.hiketrackbackend.hiketrackbackend.model.user.User;
import com.hiketrackbackend.hiketrackbackend.repository.RoleRepository;
import com.hiketrackbackend.hiketrackbackend.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RoleServiceImplTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private RoleServiceImpl roleService;

    @Test
    @DisplayName("Set user default role for new user")
    public void testSetUserDefaultRoleWhenNewUserRegisterThenSetRoleToUser() {
        User user = new User();
        Role roleUser = new Role();
        roleUser.setName(Role.RoleName.ROLE_USER);

        when(roleRepository.findByName(Role.RoleName.ROLE_USER)).thenReturn(roleUser);

        roleService.setUserDefaultRole(user);

        verify(roleRepository, times(1)).findByName(Role.RoleName.ROLE_USER);
        verify(userRepository, never()).save(user);
        assertTrue(user.getRoles().contains(roleUser));
    }

    @Test
    @DisplayName("Change user role to guide with valid email")
    public void testChangeUserRoleToGuideWhenUserFoundThenRoleChanged() {
        UserRequestDto request = new UserRequestDto();
        request.setEmail("test@example.com");

        User user = new User();
        user.setEmail("test@example.com");
        Role roleGuide = new Role();
        roleGuide.setName(Role.RoleName.ROLE_GUIDE);

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(roleRepository.findByName(Role.RoleName.ROLE_GUIDE)).thenReturn(roleGuide);

        UserDevMsgRespondDto response = roleService.changeUserRoleToGuide(request);

        verify(userRepository, times(1)).findByEmail("test@example.com");
        verify(roleRepository, times(1)).findByName(Role.RoleName.ROLE_GUIDE);
        verify(userRepository, times(1)).save(user);
        assertTrue(user.getRoles().contains(roleGuide));
        assertEquals("You have successfully changed the role: ROLE_GUIDE", response.message());
    }

    @Test
    @DisplayName("Change user role to guide with not valid email")
    public void testChangeUserRoleToGuideWhenUserNotFoundThenThrowException() {
        UserRequestDto request = new UserRequestDto();
        request.setEmail("nonexistent@example.com");

        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            roleService.changeUserRoleToGuide(request);
        });

        assertEquals("User with email nonexistent@example.com not found", exception.getMessage());
        verify(userRepository, times(1)).findByEmail("nonexistent@example.com");
        verify(roleRepository, never()).findByName(any());
        verify(userRepository, never()).save(any());
    }
}