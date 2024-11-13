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
import com.hiketrackbackend.hiketrackbackend.validation.ValidImageFile;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Validated
@Tag(
        name = "User management",
        description = "Operations related to change user state such as role changing "
                + "or updating his profile"
)
public class UserController {
    private final UserService userService;
    private final AuthenticationService authenticationService;
    private final ObjectMapper objectMapper;
    private final RoleService roleService;

    @PreAuthorize("hasAnyRole('USER', 'GUIDE', 'ADMIN')")
    @GetMapping("/me")
    @Tag(name = "User Management", description = "Endpoints for managing users")
    public UserRespondWithProfileDto getLoggedInUser(HttpServletRequest request) {
        return userService.getLoggedInUser(request);
    }

    @Operation(summary = "Logout user", description = "Logout the currently logged-in user.")
    @PostMapping("/logout")
    public String logout(HttpServletRequest request) {
        authenticationService.logout(request);
        return "Logged out successfully";
    }

    @PreAuthorize("hasAnyRole('USER', 'GUIDE', 'ADMIN') && #id == authentication.principal.id")
    @PatchMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
            summary = "Update user",
            description = "Update the details of the user with the given ID.")
    public UserUpdateRespondDto updateUser(
            @RequestPart("data") String dataString,
            @PathVariable @Positive Long id,
            @RequestPart("photo") @Valid @ValidImageFile MultipartFile photo
    ) {
        UserUpdateRequestDto requestDto;
        try {
            requestDto = objectMapper.readValue(dataString, UserUpdateRequestDto.class);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Invalid data format: " + e.getMessage());
        }
        return userService.updateUser(requestDto, id, photo);
    }

    @PreAuthorize("hasAnyRole('USER', 'GUIDE', 'ADMIN') && #userId == authentication.principal.id")
    @DeleteMapping("/{userId}")
    @Operation(summary = "Delete user", description = "Delete the user with the given ID.")
    public void deleteUser(@PathVariable @Positive Long userId) {
        userService.deleteUser(userId);
    }

    @PostMapping("/request")
    @Operation(
            summary = "Promote request from user",
            description = "Submit a request to promote user to the guide."
    )
    public UserDevMsgRespondDto promoteRequestFromUser(@RequestBody @Valid UserRequestDto request) {
        return userService.promoteRequest(request);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/role_change")
    @Operation(summary = "Promote User to Guide",
            description = "Allows an admin to change a user's role to a guide.")
    public UserDevMsgRespondDto promoteUserToGuide(@RequestBody @Valid UserRequestDto request) {
        return roleService.changeUserRoleToGuide(request);
    }
}
