package com.hiketrackbackend.hiketrackbackend.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hiketrackbackend.hiketrackbackend.dto.UserDevMsgRespondDto;
import com.hiketrackbackend.hiketrackbackend.dto.user.UserRequestDto;
import com.hiketrackbackend.hiketrackbackend.dto.user.UserRespondWithProfileDto;
import com.hiketrackbackend.hiketrackbackend.dto.user.update.UserUpdateRequestDto;
import com.hiketrackbackend.hiketrackbackend.dto.user.update.UserUpdateRespondDto;
import com.hiketrackbackend.hiketrackbackend.security.AuthenticationService;
import com.hiketrackbackend.hiketrackbackend.service.RoleService;
import com.hiketrackbackend.hiketrackbackend.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {
    @Mock
    private UserService userService;

    @Mock
    private AuthenticationService authenticationService;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private RoleService roleService;

    @InjectMocks
    private UserController userController;

    @Mock
    private HttpServletRequest httpServletRequest;

    @Mock
    private MultipartFile photo;

    @BeforeEach
    void setUp() {
        Mockito.reset(userService, authenticationService, objectMapper, roleService, httpServletRequest, photo);
    }

    @Test
    @DisplayName("Successfully get loggedIn user")
    void testGetLoggedInUser_Success() {
        UserRespondWithProfileDto expectedUser = new UserRespondWithProfileDto();
        when(userService.getLoggedInUser(httpServletRequest)).thenReturn(expectedUser);

        UserRespondWithProfileDto actualUser = userController.getLoggedInUser(httpServletRequest);

        assertEquals(expectedUser, actualUser);
        verify(userService, times(1)).getLoggedInUser(httpServletRequest);
    }

    @Test
    @DisplayName("Get loggedIn user with invalid request")
    void testGetLoggedInUser_NullRequest() {
        when(userService.getLoggedInUser(null)).thenThrow(new IllegalArgumentException("Request cannot be null"));

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            userController.getLoggedInUser(null);
        });

        assertEquals("Request cannot be null", exception.getMessage());
    }

    @Test
    @DisplayName("Get loggedIn user with server error")
    void testGetLoggedInUser_ServiceThrowsException() {
        when(userService.getLoggedInUser(httpServletRequest)).thenThrow(new RuntimeException("Service error"));

        Exception exception = assertThrows(RuntimeException.class, () -> {
            userController.getLoggedInUser(httpServletRequest);
        });

        assertEquals("Service error", exception.getMessage());
    }

    @Test
    @DisplayName("Test successful logout")
    void testLogout_Success() {
        doNothing().when(authenticationService).logout(httpServletRequest);

        String response = userController.logout(httpServletRequest);

        assertEquals("Logged out successfully", response);
        verify(authenticationService, times(1)).logout(httpServletRequest);
    }

    @Test
    @DisplayName("Test logout when HttpRequest null")
    void testLogout_NullRequest() {
        doThrow(new IllegalArgumentException("Request cannot be null")).when(authenticationService).logout(null);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            userController.logout(null);
        });

        assertEquals("Request cannot be null", exception.getMessage());
    }

    @Test
    @DisplayName("Test update user with valid data")
    void testUpdateUser_Success() throws JsonProcessingException {
        String dataString = "{\"name\":\"John\"}";
        UserUpdateRequestDto requestDto = new UserUpdateRequestDto();
        UserUpdateRespondDto expectedResponse = new UserUpdateRespondDto();
        Long id = 1L;

        when(objectMapper.readValue(dataString, UserUpdateRequestDto.class)).thenReturn(requestDto);
        when(userService.updateUser(requestDto, id, photo)).thenReturn(expectedResponse);

        UserUpdateRespondDto actualResponse = userController.updateUser(dataString, id, photo);

        assertEquals(expectedResponse, actualResponse);
        verify(objectMapper, times(1)).readValue(dataString, UserUpdateRequestDto.class);
        verify(userService, times(1)).updateUser(requestDto, id, photo);
    }

    @Test
    @DisplayName("Update user with invalid request")
    void testUpdateUser_InvalidDataString() throws JsonProcessingException {
        String dataString = "invalid json";
        Long id = 1L;

        when(objectMapper.readValue(dataString, UserUpdateRequestDto.class)).thenThrow(new JsonProcessingException("Invalid JSON") {});

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            userController.updateUser(dataString, id, photo);
        });

        assertTrue(exception.getMessage().contains("Invalid data format"));
    }

    @Test
    @DisplayName("Delete user with not valid id")
    void testDeleteUser_UserNotFound() {
        Long userId = 999L;
        doThrow(new IllegalArgumentException("User not found")).when(userService).deleteUser(userId);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userController.deleteUser(userId);
        });
        assertEquals("User not found", exception.getMessage());
        verify(userService, times(1)).deleteUser(userId);
    }

    @Test
    @DisplayName("Delete user with valid id")
    void testDeleteUser_Success() {
        Long userId = 1L;
        doNothing().when(userService).deleteUser(userId);

        assertDoesNotThrow(() -> userController.deleteUser(userId));
        verify(userService, times(1)).deleteUser(userId);
    }

    @Test
    @DisplayName("Promote user when request is empty")
    void testPromoteRequestFromUser_MissingFields() {
        UserRequestDto requestDto = new UserRequestDto();

        doThrow(new ConstraintViolationException("Fields missing", null)).when(userService).promoteRequest(requestDto);

        ConstraintViolationException exception = assertThrows(ConstraintViolationException.class, () -> {
            userController.promoteRequestFromUser(requestDto);
        });
        assertEquals("Fields missing", exception.getMessage());
        verify(userService, times(1)).promoteRequest(requestDto);
    }

    @Test
    @DisplayName("Promote user request with valid data")
    void testPromoteRequestFromUser_Success() {
        UserRequestDto requestDto = new UserRequestDto();
        UserDevMsgRespondDto expectedResponse = new UserDevMsgRespondDto("");

        when(userService.promoteRequest(requestDto)).thenReturn(expectedResponse);

        UserDevMsgRespondDto actualResponse = userController.promoteRequestFromUser(requestDto);

        assertEquals(expectedResponse, actualResponse);
        verify(userService, times(1)).promoteRequest(requestDto);
    }

    @Test
    @DisplayName("Promote user to guide successfully")
    void testPromoteUserToGuide_Success() {
        UserRequestDto requestDto = new UserRequestDto();
        UserDevMsgRespondDto expectedResponse = new UserDevMsgRespondDto("");

        when(roleService.changeUserRoleToGuide(requestDto)).thenReturn(expectedResponse);

        UserDevMsgRespondDto actualResponse = userController.promoteUserToGuide(requestDto);

        assertEquals(expectedResponse, actualResponse);
        verify(roleService, times(1)).changeUserRoleToGuide(requestDto);
    }

    @Test
    @DisplayName("User promotion with unauthorized user")
    void testPromoteUserToGuide_UnauthorizedAccess() {
        UserRequestDto requestDto = new UserRequestDto();

        when(roleService.changeUserRoleToGuide(requestDto)).thenThrow(new SecurityException("Access denied"));

        SecurityException exception = assertThrows(SecurityException.class, () -> {
            userController.promoteUserToGuide(requestDto);
        });
        assertEquals("Access denied", exception.getMessage());
        verify(roleService, times(1)).changeUserRoleToGuide(requestDto);
    }
}
