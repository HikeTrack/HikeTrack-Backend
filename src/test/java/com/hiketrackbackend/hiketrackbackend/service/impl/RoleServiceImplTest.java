package com.hiketrackbackend.hiketrackbackend.service.impl;

import com.hiketrackbackend.hiketrackbackend.dto.UserDevMsgRespondDto;
import com.hiketrackbackend.hiketrackbackend.dto.user.UserRequestDto;
import com.hiketrackbackend.hiketrackbackend.exception.EntityNotFoundException;
import com.hiketrackbackend.hiketrackbackend.model.user.Role;
import com.hiketrackbackend.hiketrackbackend.model.user.User;
import com.hiketrackbackend.hiketrackbackend.repository.RoleRepository;
import com.hiketrackbackend.hiketrackbackend.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RoleServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private RoleServiceImpl roleService;

    @Test
    public void testSetUserDefaultRoleWhenUserThenSetRoleToUser() {
        // Arrange
        User user = new User();
        Role roleUser = new Role(Role.RoleName.ROLE_USER);

        when(roleRepository.findByName(Role.RoleName.ROLE_USER)).thenReturn(roleUser);

        // Act
        roleService.setUserDefaultRole(user);

        // Assert
        verify(roleRepository, times(1)).findByName(Role.RoleName.ROLE_USER);
        verify(userRepository, never()).save(user); // Ensure save is not called in this method
        assertTrue(user.getRoles().contains(roleUser));
    }

    @Test
    public void testChangeUserRoleToGuideWhenUserFoundThenRoleChanged() {
        // Arrange
        UserRequestDto request = new UserRequestDto();
        request.setEmail("test@example.com");

        User user = new User();
        user.setEmail("test@example.com");
        Role roleGuide = new Role(Role.RoleName.ROLE_GUIDE);

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(roleRepository.findByName(Role.RoleName.ROLE_GUIDE)).thenReturn(roleGuide);

        // Act
        UserDevMsgRespondDto response = roleService.changeUserRoleToGuide(request);

        // Assert
        verify(userRepository, times(1)).findByEmail("test@example.com");
        verify(roleRepository, times(1)).findByName(Role.RoleName.ROLE_GUIDE);
        verify(userRepository, times(1)).save(user);
        assertTrue(user.getRoles().contains(roleGuide));
        assertEquals("You have successfully changed the role: ROLE_GUIDE", response.message());
    }

    @Test
    public void testChangeUserRoleToGuideWhenUserNotFoundThenThrowException() {
        // Arrange
        UserRequestDto request = new UserRequestDto();
        request.setEmail("nonexistent@example.com");

        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            roleService.changeUserRoleToGuide(request);
        });

        assertEquals("User with email nonexistent@example.com not found", exception.getMessage());
        verify(userRepository, times(1)).findByEmail("nonexistent@example.com");
        verify(roleRepository, never()).findByName(any());
        verify(userRepository, never()).save(any());
    }
}