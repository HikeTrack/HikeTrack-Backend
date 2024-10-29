package com.hiketrackbackend.hiketrackbackend.service.impl;

import com.hiketrackbackend.hiketrackbackend.dto.user.UserRequestDto;
import com.hiketrackbackend.hiketrackbackend.dto.user.update.UserUpdateRequestDto;
import com.hiketrackbackend.hiketrackbackend.dto.user.UserRespondDto;
import com.hiketrackbackend.hiketrackbackend.dto.user.UserDevMsgRespondDto;
import com.hiketrackbackend.hiketrackbackend.dto.user.registration.UserRegistrationRequestDto;
import com.hiketrackbackend.hiketrackbackend.dto.user.registration.UserRegistrationRespondDto;
import com.hiketrackbackend.hiketrackbackend.dto.user.update.UserUpdatePasswordRequestDto;
import com.hiketrackbackend.hiketrackbackend.dto.user.update.UserUpdateRespondDto;
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
import com.hiketrackbackend.hiketrackbackend.service.UserService;
import com.hiketrackbackend.hiketrackbackend.service.files.FileStorageService;
import com.hiketrackbackend.hiketrackbackend.service.notification.EmailSender;
import com.hiketrackbackend.hiketrackbackend.service.notification.EmailUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {
    private static final String FOLDER_NAME = "user_profile";
    private static final String SUBJECT = "Email Change Notification";
    private static final int FIRST_ELEMENT = 0;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder encoder;
    private final RoleService roleService;
    private final FileStorageService s3Service;
    private final ConfirmationTokenService confirmationTokenService;
    private final EmailSender confirmationEmailSenderImpl;
    private final EmailSender promotionRequestEmailSenderImpl;
    private final CustomUserDetailsService userDetailsService;
    private final EmailUtils emailUtils;

    public UserServiceImpl(
            JwtUtil jwtUtil,
            UserRepository userRepository,
            UserMapper userMapper,
            PasswordEncoder encoder,
            RoleService roleService,
            FileStorageService s3Service,
            ConfirmationTokenService confirmationTokenService,
            @Qualifier("confirmationRequestEmailSenderImpl") EmailSender confirmationEmailSenderImpl,
            @Qualifier("promotionRequestEmailSenderImpl") EmailSender promotionRequestEmailSenderImpl, CustomUserDetailsService userDetailsService, EmailUtils emailUtils) {

        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.encoder = encoder;
        this.roleService = roleService;
        this.s3Service = s3Service;
        this.confirmationTokenService = confirmationTokenService;
        this.confirmationEmailSenderImpl = confirmationEmailSenderImpl;
        this.promotionRequestEmailSenderImpl = promotionRequestEmailSenderImpl;
        this.userDetailsService = userDetailsService;
        this.emailUtils = emailUtils;
    }

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

        String token = confirmationTokenService.save(user.getEmail());
        confirmationEmailSenderImpl.send(request.getEmail(), token);
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

    @Override
    @Transactional
    public UserUpdateRespondDto updateUser(UserUpdateRequestDto requestDto, Long id, MultipartFile file) {
        User user = findUserById(id);
        String oldEmail = user.getEmail();
        userMapper.updateUserFromDto(requestDto, user);

        UserProfile userProfile = user.getUserProfile();
        userMapper.updateUserProfileFromDto(requestDto.getUserProfileRequestDto(), user.getUserProfile());
        if (file != null) {
            updateUserProfilePhoto(userProfile, file);
        }

        userRepository.save(user);
        UserUpdateRespondDto responseDto = userMapper.toUpdateRespondDto(user);

        if (!oldEmail.equals(user.getEmail())) {
            setNewToken(responseDto, user);
        }

        generateEmailChangeNotification(oldEmail, user.getEmail());
        return responseDto;
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
        return userMapper.toRespondDto(user);
    }

    @Override
    public void deleteUser(Long id) {
        findUserById(id);
        userRepository.deleteById(id);
    }

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

    private void setNewToken(UserUpdateRespondDto responseDto, User user) {
        String newToken = jwtUtil.generateToken(user.getEmail());
        responseDto.setToken(newToken);
        updateSecurityContext(user);
    }

    private void updateSecurityContext(User user) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private void updateUserProfilePhoto(UserProfile userProfile, MultipartFile newPhoto) {
        s3Service.deleteFileFromS3(userProfile.getPhoto());
        List<String> urls = s3Service.uploadFileToS3(FOLDER_NAME, Collections.singletonList(newPhoto));
        userProfile.setPhoto(urls.get(FIRST_ELEMENT));
    }

    private void generateEmailChangeNotification(String oldEmail, String newEmail) {
        String notification = String.format(
                "Dear user,\n\n" +
                        "Your email has been successfully changed from %s to %s.\n\n" +
                        "If you did not initiate this change, please contact our support team immediately.\n\n" +
                        "Best regards,\n" +
                        "Hike Track Team",
                oldEmail, newEmail
        );
        emailUtils.sendEmail(oldEmail, SUBJECT, notification);

        String token = UUID.randomUUID().toString();
        confirmationEmailSenderImpl.send(newEmail, token);
    }
}
