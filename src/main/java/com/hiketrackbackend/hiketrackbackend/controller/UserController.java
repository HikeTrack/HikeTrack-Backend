package com.hiketrackbackend.hiketrackbackend.controller;

import com.hiketrackbackend.hiketrackbackend.dto.user.registration.UserRegistrationRespondDto;
import com.hiketrackbackend.hiketrackbackend.dto.user.update.UserUpdateRequestDto;
import com.hiketrackbackend.hiketrackbackend.dto.user.update.UserUpdateRespondDto;
import com.hiketrackbackend.hiketrackbackend.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Validated
@Tag(name = "", description = "")
public class UserController {
    private final UserService userService;

    @PreAuthorize("hasAnyRole('USER', 'GUIDE', 'ADMIN')")
    @GetMapping("/{id}")
    @Operation(summary = "", description = "")
    public UserRegistrationRespondDto getUser(@PathVariable @Positive Long id) {
        return userService.getById(id);
    }

    // TODO закрыть сесию when logout

    // TODO послать линк на востановление пароля повторно( точно так же сделать и на регистрацию)
//    @Operation(summary = "",
//            description = "")
//    @PostMapping("/")
//    public String logout(HttpServletRequest request,
//                         Authentication authentication) {
//        String email = authentication.getName();
//        authenticationService.logout(request, email);
//        return "Logged out successfully";
//    }

    @PreAuthorize("hasAnyRole('USER', 'GUIDE', 'ADMIN')")
    @PatchMapping
    @Operation(summary = "", description = "")
    public UserUpdateRespondDto updateUser(@RequestBody @Valid UserUpdateRequestDto requestDto) {
        return userService.;
    }

    @PreAuthorize("hasAnyRole('USER', 'GUIDE', 'ADMIN')")
    @DeleteMapping("/{id}")
    @Operation(summary = "", description = "")
    public UserRegistrationRespondDto deleteUser(@PathVariable @Positive Long id) {
        return userService.getById(id);
    }


}
