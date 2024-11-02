package com.hiketrackbackend.hiketrackbackend.service.impl;

import com.hiketrackbackend.hiketrackbackend.dto.user.UserRespondDto;
import com.hiketrackbackend.hiketrackbackend.dto.user.registration.UserRegistrationRequestDto;
import com.hiketrackbackend.hiketrackbackend.dto.user.registration.UserRegistrationRespondDto;
import com.hiketrackbackend.hiketrackbackend.dto.user.update.UserUpdatePasswordRequestDto;
import com.hiketrackbackend.hiketrackbackend.dto.user.update.UserUpdateRequestDto;
import com.hiketrackbackend.hiketrackbackend.dto.user.update.UserUpdateRespondDto;
import com.hiketrackbackend.hiketrackbackend.dto.userProfile.UserProfileRequestDto;
import com.hiketrackbackend.hiketrackbackend.exception.EntityNotFoundException;
import com.hiketrackbackend.hiketrackbackend.exception.RegistrationException;
import com.hiketrackbackend.hiketrackbackend.mapper.UserMapper;
import com.hiketrackbackend.hiketrackbackend.model.user.User;
import com.hiketrackbackend.hiketrackbackend.model.user.UserProfile;
import com.hiketrackbackend.hiketrackbackend.repository.UserRepository;
import com.hiketrackbackend.hiketrackbackend.security.CustomUserDetailsService;
import com.hiketrackbackend.hiketrackbackend.security.JwtUtil;
import com.hiketrackbackend.hiketrackbackend.security.token.impl.ConfirmationTokenService;
import com.hiketrackbackend.hiketrackbackend.service.RoleService;
import com.hiketrackbackend.hiketrackbackend.service.files.FileStorageService;
import com.hiketrackbackend.hiketrackbackend.service.notification.ConfirmationRequestEmailSenderImpl;
import com.hiketrackbackend.hiketrackbackend.service.notification.EmailUtils;
import com.hiketrackbackend.hiketrackbackend.service.notification.PromotionRequestEmailSenderImpl;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {
    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder encoder;

    @Mock
    private RoleService roleService;

    @Mock
    private FileStorageService s3Service;

    @Mock
    private ConfirmationTokenService confirmationTokenService;

    @Mock
    private CustomUserDetailsService userDetailsService;

    @Mock
    private ConfirmationRequestEmailSenderImpl confirmationEmailSenderImpl;

    @Mock
    private PromotionRequestEmailSenderImpl promotionRequestEmailSenderImpl;

    @Mock
    private EmailUtils emailUtils;

    @InjectMocks
    private UserServiceImpl userService;

    private UserRegistrationRequestDto userRegistrationRequestDto;
    private User user;
    private UserRegistrationRespondDto userRegistrationRespondDto;

    @BeforeEach
    void setUp() {
        userRegistrationRequestDto = new UserRegistrationRequestDto();
        userRegistrationRequestDto.setEmail("test@example.com");
        userRegistrationRequestDto.setPassword("password");
        userRegistrationRequestDto.setRepeatPassword("password");
        userRegistrationRequestDto.setFirstName("John");
        userRegistrationRequestDto.setLastName("Doe");

        user = new User();
        user.setEmail("test@example.com");
        user.setPassword("encodedPassword");
        user.setFirstName("John");
        user.setLastName("Doe");

        userRegistrationRespondDto = new UserRegistrationRespondDto();
        userRegistrationRespondDto.setId(1L);
        userRegistrationRespondDto.setEmail("test@example.com");
        userRegistrationRespondDto.setFirstName("John");
        userRegistrationRespondDto.setLastName("Doe");
    }

    @Test
    @DisplayName("Register already registered user")
    void testRegisterWhenUserAlreadyRegisteredThenThrowRegistrationException() {
        when(userRepository.existsUserByEmail(userRegistrationRequestDto.getEmail())).thenReturn(true);

        assertThatThrownBy(() -> userService.register(userRegistrationRequestDto))
                .isInstanceOf(RegistrationException.class)
                .hasMessageContaining("This email is already used: " + userRegistrationRequestDto.getEmail());

        verify(userRepository).existsUserByEmail(userRegistrationRequestDto.getEmail());
        verify(userMapper, never()).toEntity(any(UserRegistrationRequestDto.class));
        verify(encoder, never()).encode(anyString());
        verify(roleService, never()).setUserDefaultRole(any(User.class));
        verify(userRepository, never()).save(any(User.class));
        verify(confirmationTokenService, never()).save(anyString());
        verify(confirmationEmailSenderImpl, never()).send(anyString(), anyString());
    }

    @Test
    @DisplayName("Register user when email is used")
    void testRegisterWhenEmailIsAlreadyUsedThenThrowRegistrationException() {
        when(userRepository.existsUserByEmail(userRegistrationRequestDto.getEmail())).thenReturn(true);

        assertThatThrownBy(() -> userService.register(userRegistrationRequestDto))
                .isInstanceOf(RegistrationException.class)
                .hasMessageContaining("This email is already used: " + userRegistrationRequestDto.getEmail());

        verify(userRepository).existsUserByEmail(userRegistrationRequestDto.getEmail());
        verify(userMapper, never()).toEntity(any(UserRegistrationRequestDto.class));
        verify(encoder, never()).encode(anyString());
        verify(roleService, never()).setUserDefaultRole(any(User.class));
        verify(userRepository, never()).save(any(User.class));
        verify(confirmationTokenService, never()).save(anyString());
        verify(confirmationEmailSenderImpl, never()).send(anyString(), anyString());
    }

    @Test
    @DisplayName("Update password when user is found and password is successfully updated")
    void testUpdatePasswordWhenUserFoundAndPasswordUpdated() {
        UserUpdatePasswordRequestDto request = new UserUpdatePasswordRequestDto();
        request.setPassword("newPassword");
        request.setRepeatPassword("newPassword");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(encoder.encode(request.getPassword())).thenReturn("encodedNewPassword");

        userService.updatePassword(request, 1L);

        verify(userRepository).findById(1L);
        verify(userRepository).save(user);
        assertThat(user.getPassword()).isEqualTo("encodedNewPassword");
    }

    @Test
    @DisplayName("Update password when user not found")
    void testUpdatePasswordWhenUserNotFound() {
        UserUpdatePasswordRequestDto request = new UserUpdatePasswordRequestDto();
        request.setPassword("newPassword");
        request.setRepeatPassword("newPassword");

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.updatePassword(request, 1L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("User with id 1 not found");

        verify(userRepository).findById(1L);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Update user when user is not found, should throw EntityNotFoundException")
    void testUpdateUserWhenUserNotFoundThenThrowEntityNotFoundException() {
        UserUpdateRequestDto requestDto = new UserUpdateRequestDto();
        MultipartFile file = mock(MultipartFile.class);

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.updateUser(requestDto, 1L, file))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("User with id 1 not found");

        verify(userRepository).findById(1L);
        verify(userMapper, never()).updateUserFromDto(any(UserUpdateRequestDto.class), any(User.class));
        verify(userMapper, never()).updateUserProfileFromDto(any(), any());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Update user and do not update email")
    void testUpdateUserWhenUserFoundAndEmailNotChangedThenReturnUpdatedUser() {
        UserUpdateRequestDto requestDto = new UserUpdateRequestDto();
        requestDto.setEmail("test@example.com");
        requestDto.setFirstName("John");
        requestDto.setLastName("Doe");
        UserProfileRequestDto userProfileRequestDto = new UserProfileRequestDto();
        requestDto.setUserProfileRequestDto(userProfileRequestDto);
        MultipartFile file = mock(MultipartFile.class);

        User user = new User();
        user.setEmail("test@example.com");
        user.setFirstName("John");
        user.setLastName("Doe");
        UserProfile userProfile = new UserProfile();
        user.setUserProfile(userProfile);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        doNothing().when(userMapper).updateUserFromDto(requestDto, user);
        doNothing().when(userMapper).updateUserProfileFromDto(requestDto.getUserProfileRequestDto(), userProfile);
        when(userRepository.save(user)).thenReturn(user);
        UserUpdateRespondDto responseDto = new UserUpdateRespondDto();
        responseDto.setEmail("test@example.com");
        responseDto.setFirstName("John");
        responseDto.setLastName("Doe");
        when(userMapper.toUpdateRespondDto(user)).thenReturn(responseDto);

        List<String> urls = Collections.singletonList("http://example.com/photo.jpg");
        when(s3Service.uploadFileToS3(anyString(), anyList())).thenReturn(urls);

        UserUpdateRespondDto result = userService.updateUser(requestDto, 1L, file);

        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo(requestDto.getEmail());
        assertThat(result.getFirstName()).isEqualTo(requestDto.getFirstName());
        assertThat(result.getLastName()).isEqualTo(requestDto.getLastName());

        verify(userRepository).findById(1L);
        verify(userMapper).updateUserFromDto(requestDto, user);
        verify(userMapper).updateUserProfileFromDto(requestDto.getUserProfileRequestDto(), userProfile);
        verify(userRepository).save(user);
        verify(s3Service).deleteFileFromS3(anyString());
        verify(s3Service).uploadFileToS3(anyString(), anyList());
        verify(emailUtils, never()).sendEmail(anyString(), anyString(), anyString());
    }

    @Test
    @DisplayName("Get log in user with missing Authorization")
    void testGetLoggedInUserWhenAuthorizationHeaderIsMissingThenThrowIllegalArgumentException() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("Authorization")).thenReturn(null);

        assertThatThrownBy(() -> userService.getLoggedInUser(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Authorization header is missing or incorrect");

        verify(request).getHeader("Authorization");
    }

    @Test
    @DisplayName("Get log in user with not started authorization")
    void testGetLoggedInUserWhenAuthorizationHeaderDoesNotStartWithBearerThenThrowIllegalArgumentException() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("Authorization")).thenReturn("InvalidToken");

        assertThatThrownBy(() -> userService.getLoggedInUser(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Authorization header is missing or incorrect");

        verify(request).getHeader("Authorization");
    }

    @Test
    @DisplayName("Get log in user")
    void testGetLoggedInUserWhenAuthorizationHeaderIsValidThenReturnUserRespondDto() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("Authorization")).thenReturn("Bearer ValidToken");
        when(jwtUtil.getUsername("ValidToken")).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        UserRespondDto userRespondDto = new UserRespondDto();
        userRespondDto.setEmail("test@example.com");
        when(userMapper.toRespondDto(user)).thenReturn(userRespondDto);

        UserRespondDto result = userService.getLoggedInUser(request);

        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo("test@example.com");

        verify(request).getHeader("Authorization");
        verify(jwtUtil).getUsername("ValidToken");
        verify(userRepository).findByEmail("test@example.com");
        verify(userMapper).toRespondDto(user);
    }

    @Test
    @DisplayName("Delete user")
    void testDeleteUserWhenUserFoundThenUserDeleted() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        doNothing().when(userRepository).deleteById(1L);

        userService.deleteUser(1L);

        verify(userRepository).findById(1L);
        verify(userRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Delete not found user")
    void testDeleteUserWhenUserNotFoundThenExceptionThrown() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.deleteUser(1L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("User with id 1 not found");

        verify(userRepository).findById(1L);
        verify(userRepository, never()).deleteById(1L);
    }
}