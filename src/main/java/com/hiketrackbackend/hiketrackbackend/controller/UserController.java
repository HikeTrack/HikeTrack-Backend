package com.hiketrackbackend.hiketrackbackend.controller;

import com.hiketrackbackend.hiketrackbackend.dto.user.UserDevMsgRespondDto;
import com.hiketrackbackend.hiketrackbackend.dto.user.UserRequestDto;
import com.hiketrackbackend.hiketrackbackend.dto.user.update.UserUpdateRequestDto;
import com.hiketrackbackend.hiketrackbackend.dto.user.UserRespondDto;
import com.hiketrackbackend.hiketrackbackend.security.AuthenticationService;
import com.hiketrackbackend.hiketrackbackend.service.RoleService;
import com.hiketrackbackend.hiketrackbackend.service.UserService;
import com.hiketrackbackend.hiketrackbackend.validation.ValidImageFileList;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
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
@RequestMapping("/user")
@RequiredArgsConstructor
@Validated
@Tag(name = "", description = "")
public class UserController {
    private final UserService userService;
    private final AuthenticationService authenticationService;
    private final RoleService roleService;

    @PreAuthorize("hasAnyRole('USER', 'GUIDE', 'ADMIN')")
    @GetMapping("/me")
    @Operation(summary = "", description = "")
    public UserRespondDto getLoggedInUser(HttpServletRequest request) {
        return userService.getLoggedInUser(request);
    }

    // TODO послать линк на востановление пароля повторно( точно так же сделать и на регистрацию)
    @Operation(summary = "",
            description = "")
    @PostMapping("/logout")
    public String logout(HttpServletRequest request) {
        authenticationService.logout(request);
        return "Logged out successfully";
    }

    @PreAuthorize("hasAnyRole('USER', 'GUIDE', 'ADMIN')")
    @PatchMapping("/{id}")
    @Operation(summary = "", description = "")
    public UserRespondDto updateUser(
            @RequestPart("requestDto") @Valid UserUpdateRequestDto requestDto,
            @PathVariable @Positive Long id,
            //TODO  исправить аннотацию с файл лист на файл или может и не надо
            @RequestPart("photo") @Valid @ValidImageFileList MultipartFile photo
    ) {
        return userService.updateUser(requestDto, id, photo);
    }

    @PreAuthorize("hasAnyRole('USER', 'GUIDE', 'ADMIN')")
    @DeleteMapping("/{userId}")
    @Operation(summary = "", description = "")
    public void deleteUser(@PathVariable @Positive Long userId) {
        userService.deleteUser(userId);
    }

    // for admin profile
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/role_change")
    @Operation(summary = "", description = "")
    public UserDevMsgRespondDto promoteUserToGuide(@RequestBody UserRequestDto request) {
        return roleService.changeUserRoleToGuide(request);
    }

    // TODO temporary decision to sent only email for promote.
    //  Next feat accept a FULL form with data and send it to admins mail.
    //TODO когда придет линка при регистрации надо проверить подтвердил ли чел имейл
    @PostMapping("/request")
    @Operation(summary = "", description = "")
    public UserDevMsgRespondDto promoteRequestFromUser(@RequestBody UserRequestDto request) {
        return userService.promoteRequest(request);
    }
}
