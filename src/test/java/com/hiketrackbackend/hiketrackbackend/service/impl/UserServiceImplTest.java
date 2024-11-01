package com.hiketrackbackend.hiketrackbackend.service.impl;

import com.hiketrackbackend.hiketrackbackend.dto.UserDevMsgRespondDto;
import com.hiketrackbackend.hiketrackbackend.dto.user.UserRequestDto;
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
import com.hiketrackbackend.hiketrackbackend.service.notification.EmailSender;
import com.hiketrackbackend.hiketrackbackend.service.notification.EmailUtils;
import com.hiketrackbackend.hiketrackbackend.service.notification.PromotionRequestEmailSenderImpl;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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

//    @Test
//    void testRegisterWhenUserIsSuccessfullyRegisteredThenReturnUserRegistrationRespondDto() throws RegistrationException {
//        // Arrange
//        when(userRepository.existsUserByEmail(userRegistrationRequestDto.getEmail())).thenReturn(false);
//        when(userMapper.toEntity(userRegistrationRequestDto)).thenReturn(user);
//        when(encoder.encode(userRegistrationRequestDto.getPassword())).thenReturn("encodedPassword");
//        when(userRepository.save(any(User.class))).thenReturn(user);
//        when(userMapper.toDto(user)).thenReturn(userRegistrationRespondDto);
//        when(confirmationTokenService.save(user.getEmail())).thenReturn("token");
//
//        // Act
//        UserRegistrationRespondDto result = userService.register(userRegistrationRequestDto);
//
//        // Assert
//        assertThat(result).isNotNull();
//        assertThat(result.getEmail()).isEqualTo(userRegistrationRequestDto.getEmail());
//        assertThat(result.getFirstName()).isEqualTo(userRegistrationRequestDto.getFirstName());
//        assertThat(result.getLastName()).isEqualTo(userRegistrationRequestDto.getLastName());
//
//        verify(userRepository).existsUserByEmail(userRegistrationRequestDto.getEmail());
//        verify(userMapper).toEntity(userRegistrationRequestDto);
//        verify(encoder).encode(userRegistrationRequestDto.getPassword());
//        verify(roleService).setUserDefaultRole(user);
//        verify(userRepository).save(user);
//        verify(confirmationTokenService).save(user.getEmail());
//        verify(confirmationEmailSenderImpl).send(userRegistrationRequestDto.getEmail(), "token");
//    }

    @Test
    void testRegisterWhenUserAlreadyRegisteredThenThrowRegistrationException() {
        // Arrange
        when(userRepository.existsUserByEmail(userRegistrationRequestDto.getEmail())).thenReturn(true);

        // Act & Assert
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

//    @Test
//    void testRegisterWhenUserNotRegisteredThenReturnUserRegistrationRespondDto() throws RegistrationException {
//        // Arrange
//        when(userRepository.existsUserByEmail(userRegistrationRequestDto.getEmail())).thenReturn(false);
//        when(userMapper.toEntity(userRegistrationRequestDto)).thenReturn(user);
//        when(encoder.encode(userRegistrationRequestDto.getPassword())).thenReturn("encodedPassword");
//        when(userRepository.save(any(User.class))).thenReturn(user);
//        when(userMapper.toDto(user)).thenReturn(userRegistrationRespondDto);
//        when(confirmationTokenService.save(user.getEmail())).thenReturn("token");
//
//        // Act
//        UserRegistrationRespondDto result = userService.register(userRegistrationRequestDto);
//
//        // Assert
//        assertThat(result).isNotNull();
//        assertThat(result.getEmail()).isEqualTo(userRegistrationRequestDto.getEmail());
//        assertThat(result.getFirstName()).isEqualTo(userRegistrationRequestDto.getFirstName());
//        assertThat(result.getLastName()).isEqualTo(userRegistrationRequestDto.getLastName());
//
//        verify(userRepository).existsUserByEmail(userRegistrationRequestDto.getEmail());
//        verify(userMapper).toEntity(userRegistrationRequestDto);
//        verify(encoder).encode(userRegistrationRequestDto.getPassword());
//        verify(roleService).setUserDefaultRole(user);
//        verify(userRepository).save(user);
//        verify(confirmationTokenService).save(user.getEmail());
//        verify(confirmationEmailSenderImpl).send(userRegistrationRequestDto.getEmail(), "token");
//    }

    @Test
    void testRegisterWhenEmailIsAlreadyUsedThenThrowRegistrationException() {
        // Arrange
        when(userRepository.existsUserByEmail(userRegistrationRequestDto.getEmail())).thenReturn(true);

        // Act & Assert
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
    void testUpdatePasswordWhenUserFoundAndPasswordUpdated() {
        // Arrange
        UserUpdatePasswordRequestDto request = new UserUpdatePasswordRequestDto();
        request.setPassword("newPassword");
        request.setRepeatPassword("newPassword");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(encoder.encode(request.getPassword())).thenReturn("encodedNewPassword");

        // Act
        userService.updatePassword(request, 1L);

        // Assert
        verify(userRepository).findById(1L);
        verify(userRepository).save(user);
        assertThat(user.getPassword()).isEqualTo("encodedNewPassword");
    }

    @Test
    void testUpdatePasswordWhenUserNotFound() {
        // Arrange
        UserUpdatePasswordRequestDto request = new UserUpdatePasswordRequestDto();
        request.setPassword("newPassword");
        request.setRepeatPassword("newPassword");

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> userService.updatePassword(request, 1L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("User with id 1 not found");

        verify(userRepository).findById(1L);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testUpdateUserWhenUserNotFoundThenThrowEntityNotFoundException() {
        // Arrange
        UserUpdateRequestDto requestDto = new UserUpdateRequestDto();
        MultipartFile file = mock(MultipartFile.class);

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> userService.updateUser(requestDto, 1L, file))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("User with id 1 not found");

        verify(userRepository).findById(1L);
        verify(userMapper, never()).updateUserFromDto(any(UserUpdateRequestDto.class), any(User.class));
        verify(userMapper, never()).updateUserProfileFromDto(any(), any());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testUpdateUserWhenUserFoundAndEmailNotChangedThenReturnUpdatedUser() {
        // Arrange
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

        // Mock the behavior of s3Service
        List<String> urls = Collections.singletonList("http://example.com/photo.jpg");
        when(s3Service.uploadFileToS3(anyString(), anyList())).thenReturn(urls);

        // Act
        UserUpdateRespondDto result = userService.updateUser(requestDto, 1L, file);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo(requestDto.getEmail());
        assertThat(result.getFirstName()).isEqualTo(requestDto.getFirstName());
        assertThat(result.getLastName()).isEqualTo(requestDto.getLastName());

        verify(userRepository).findById(1L);
        verify(userMapper).updateUserFromDto(requestDto, user);
        verify(userMapper).updateUserProfileFromDto(requestDto.getUserProfileRequestDto(), userProfile);
        verify(userRepository).save(user);
        verify(s3Service).deleteFileFromS3(anyString()); // Убедитесь, что метод был вызван
        verify(s3Service).uploadFileToS3(anyString(), anyList()); // Убедитесь, что метод был вызван
        verify(emailUtils, never()).sendEmail(anyString(), anyString(), anyString());
    }

//    @Test
//    void testUpdateUserWhenUserFoundAndEmailChangedThenReturnUpdatedUserWithNewToken() {
//        // Arrange
//        UserUpdateRequestDto requestDto = new UserUpdateRequestDto();
//        requestDto.setEmail("new@example.com");
//        requestDto.setFirstName("John");
//        requestDto.setLastName("Doe");
//        UserProfileRequestDto userProfileRequestDto = new UserProfileRequestDto();
//        requestDto.setUserProfileRequestDto(userProfileRequestDto);
//        MultipartFile file = mock(MultipartFile.class);
//
//        User user = new User();
//        user.setEmail("test@example.com");
//        user.setFirstName("John");
//        user.setLastName("Doe");
//        UserProfile userProfile = new UserProfile();
//        user.setUserProfile(userProfile);
//
//        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
//        doNothing().when(userMapper).updateUserFromDto(requestDto, user);
//        doNothing().when(userMapper).updateUserProfileFromDto(requestDto.getUserProfileRequestDto(), userProfile);
//        when(userRepository.save(user)).thenReturn(user);
//        UserUpdateRespondDto responseDto = new UserUpdateRespondDto();
//        responseDto.setEmail("new@example.com");
//        responseDto.setFirstName("John");
//        responseDto.setLastName("Doe");
//        when(userMapper.toUpdateRespondDto(user)).thenReturn(responseDto);
//        when(jwtUtil.generateToken(eq("new@example.com"))).thenReturn("newToken");
//
//        // Mock the behavior of s3Service
//        List<String> urls = Collections.singletonList("http://example.com/photo.jpg");
//        when(s3Service.uploadFileToS3(anyString(), anyList())).thenReturn(urls);
//
//        // Mock the behavior of userDetailsService
//        UserDetails userDetails = mock(UserDetails.class);
//        when(userDetailsService.loadUserByUsername(anyString())).thenReturn(userDetails);
//
//        // Act
//        UserUpdateRespondDto result = userService.updateUser(requestDto, 1L, file);
//
//        // Assert
//        assertThat(result).isNotNull();
//        assertThat(result.getEmail()).isEqualTo(requestDto.getEmail());
//        assertThat(result.getFirstName()).isEqualTo(requestDto.getFirstName());
//        assertThat(result.getLastName()).isEqualTo(requestDto.getLastName());
//        assertThat(result.getToken()).isEqualTo("newToken");
//
//        verify(userRepository).findById(1L);
//        verify(userMapper).updateUserFromDto(requestDto, user);
//        verify(userMapper).updateUserProfileFromDto(requestDto.getUserProfileRequestDto(), userProfile);
//        verify(userRepository).save(user);
//        verify(jwtUtil).generateToken(eq("new@example.com"));
//        verify(emailUtils).sendEmail(eq("test@example.com"), eq("Email Change Notification"), anyString());
//        verify(confirmationEmailSenderImpl).send(eq("new@example.com"), anyString());
//        verify(s3Service).deleteFileFromS3(anyString());
//        verify(s3Service).uploadFileToS3(anyString(), anyList());
//        verify(userDetailsService).loadUserByUsername(anyString());
//    }

    @Test
    void testUpdatePasswordWhenUserFoundAndPasswordUpdatedSuccessfully() {
        // Arrange
        UserUpdatePasswordRequestDto request = new UserUpdatePasswordRequestDto();
        request.setPassword("newPassword");
        request.setRepeatPassword("newPassword");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(encoder.encode(request.getPassword())).thenReturn("encodedNewPassword");

        // Act
        userService.updatePassword(request, 1L);

        // Assert
        verify(userRepository).findById(1L);
        verify(userRepository).save(user);
        assertThat(user.getPassword()).isEqualTo("encodedNewPassword");
    }

    @Test
    void testGetLoggedInUserWhenAuthorizationHeaderIsMissingThenThrowIllegalArgumentException() {
        // Arrange
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("Authorization")).thenReturn(null);

        // Act & Assert
        assertThatThrownBy(() -> userService.getLoggedInUser(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Authorization header is missing or incorrect");

        verify(request).getHeader("Authorization");
    }

    @Test
    void testGetLoggedInUserWhenAuthorizationHeaderDoesNotStartWithBearerThenThrowIllegalArgumentException() {
        // Arrange
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("Authorization")).thenReturn("InvalidToken");

        // Act & Assert
        assertThatThrownBy(() -> userService.getLoggedInUser(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Authorization header is missing or incorrect");

        verify(request).getHeader("Authorization");
    }

    @Test
    void testGetLoggedInUserWhenAuthorizationHeaderIsValidThenReturnUserRespondDto() {
        // Arrange
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("Authorization")).thenReturn("Bearer ValidToken");
        when(jwtUtil.getUsername("ValidToken")).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        UserRespondDto userRespondDto = new UserRespondDto();
        userRespondDto.setEmail("test@example.com");
        when(userMapper.toRespondDto(user)).thenReturn(userRespondDto);

        // Act
        UserRespondDto result = userService.getLoggedInUser(request);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo("test@example.com");

        verify(request).getHeader("Authorization");
        verify(jwtUtil).getUsername("ValidToken");
        verify(userRepository).findByEmail("test@example.com");
        verify(userMapper).toRespondDto(user);
    }

    @Test
    void testDeleteUserWhenUserFoundThenUserDeleted() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        doNothing().when(userRepository).deleteById(1L);

        // Act
        userService.deleteUser(1L);

        // Assert
        verify(userRepository).findById(1L);
        verify(userRepository).deleteById(1L);
    }

    @Test
    void testDeleteUserWhenUserNotFoundThenExceptionThrown() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> userService.deleteUser(1L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("User with id 1 not found");

        verify(userRepository).findById(1L);
        verify(userRepository, never()).deleteById(1L);
    }

//    @Test
//    void testPromoteRequestWhenValidUserRequestDtoThenSendEmailAndReturnMessage() {
//        // Arrange
//        UserRequestDto requestDto = new UserRequestDto();
//        requestDto.setEmail("test@example.com");
//        doNothing().when(promotionRequestEmailSenderImpl).send(anyString(), anyString());
//
//        // Act
//        UserDevMsgRespondDto result = userService.promoteRequest(requestDto);
//
//        // Assert
//        assertThat(result).isNotNull();
//        assertThat(result.message()).isEqualTo("Request for promotion has been sent");
//
//        verify(promotionRequestEmailSenderImpl, times(1)).send(requestDto.getEmail(), requestDto.getEmail());
//    }

//    @Test
//    void testPromoteRequestWhenCalledWithValidUserRequestDtoThenEmailSentAndMessageReturned() {
//        // Arrange
//        UserRequestDto requestDto = new UserRequestDto();
//        requestDto.setEmail("test@example.com");
//
//        // Act
//        UserDevMsgRespondDto result = userService.promoteRequest(requestDto);
//
//        // Assert
//        assertThat(result).isNotNull();
//        assertThat(result.message()).isEqualTo("Request for promotion has been sent");
//
//        verify(promotionRequestEmailSenderImpl).send(requestDto.getEmail(), requestDto.getEmail());
//    }
}