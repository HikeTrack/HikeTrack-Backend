package com.hiketrackbackend.hiketrackbackend.controller;

import com.hiketrackbackend.hiketrackbackend.dto.AdminRequestDto;
import com.hiketrackbackend.hiketrackbackend.dto.UserDevMsgRespondDto;
import com.hiketrackbackend.hiketrackbackend.dto.user.UserRequestDto;
import com.hiketrackbackend.hiketrackbackend.service.AdministrationService;
import com.hiketrackbackend.hiketrackbackend.service.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/administration")
@RequiredArgsConstructor
@Tag(name = "", description = "")
public class AdministrationController {
    private final RoleService roleService;
    private final AdministrationService administrationService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/role_change")
    @Operation(summary = "", description = "")
    public UserDevMsgRespondDto promoteUserToGuide(@RequestBody UserRequestDto request) {
        return roleService.changeUserRoleToGuide(request);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/newsletter")
    @Operation(summary = "", description = "")
    public UserDevMsgRespondDto sendNewsletterToAllSubs(@RequestBody AdminRequestDto request) {
        return administrationService.createNewsletter(request);
    }
}
