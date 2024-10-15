package com.hiketrackbackend.hiketrackbackend.controller;

import com.hiketrackbackend.hiketrackbackend.dto.user.UserDevMsgRespondDto;
import com.hiketrackbackend.hiketrackbackend.dto.user.UserRequestDto;
import com.hiketrackbackend.hiketrackbackend.service.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@Validated
@Tag(name = "", description = "")
public class AdminController {
    private final RoleService roleService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    @Operation(summary = "", description = "")
    public UserDevMsgRespondDto addGuideRoleToUser(@RequestBody UserRequestDto request) {
        return roleService.changeUserRoleToGuide(request);
    }
}
