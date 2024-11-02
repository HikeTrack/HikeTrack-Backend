package com.hiketrackbackend.hiketrackbackend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hiketrackbackend.hiketrackbackend.dto.UserDevMsgRespondDto;
import com.hiketrackbackend.hiketrackbackend.dto.user.UserRequestDto;
import com.hiketrackbackend.hiketrackbackend.dto.user.UserRespondDto;
import com.hiketrackbackend.hiketrackbackend.security.AuthenticationService;
import com.hiketrackbackend.hiketrackbackend.security.JwtUtil;
import com.hiketrackbackend.hiketrackbackend.security.token.UserTokenService;
import com.hiketrackbackend.hiketrackbackend.service.RoleService;
import com.hiketrackbackend.hiketrackbackend.service.UserService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import jakarta.servlet.http.HttpServletRequest;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
public class UserControllerTest {
    private static MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private AuthenticationService authenticationService;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private UserDetailsService userDetailsService;

    @MockBean
    private UserTokenService<HttpServletRequest> userTokenService;

    @MockBean
    private RoleService roleService;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    static void beforeAll(@Autowired WebApplicationContext applicationContext) {
        mockMvc = MockMvcBuilders.webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
    }

    @Test
    @DisplayName("Get log in user when authenticated then return user")
    @WithMockUser(username = "user1")
    public void testGetLoggedInUserWhenAuthenticatedThenReturnUser() throws Exception {
        UserRespondDto userRespondDto = new UserRespondDto();
        userRespondDto.setEmail("user1@example.com");

        Mockito.when(userService.getLoggedInUser(Mockito.any(HttpServletRequest.class))).thenReturn(userRespondDto);

        mockMvc.perform(get("/user/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("user1@example.com"));
    }

    @Test
    @DisplayName("Get log in user when not authenticated then return 302")
    public void testGetLoggedInUserWhenNotAuthenticatedThenReturn302() throws Exception {
        mockMvc.perform(get("/user/me"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @DisplayName("Logout when authenticated then return success message")
    @WithMockUser(username = "user1")
    public void testLogoutWhenAuthenticatedThenReturnSuccessMessage() throws Exception {
        Mockito.doNothing().when(authenticationService).logout(Mockito.any(HttpServletRequest.class));

        mockMvc.perform(post("/user/logout")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string("Logged out successfully"));
    }

    @Test
    @DisplayName("Logout when not authenticated then return 403")
    public void testLogoutWhenNotAuthenticatedThenReturn403() throws Exception {
        mockMvc.perform(post("/user/logout"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Update user when authenticated and request is invalid then return 403")
    @WithMockUser(username = "user1")
    public void testUpdateUserWhenAuthenticatedAndRequestIsInvalidThenReturn403() throws Exception {
        String invalidDataString = "invalid data";

        mockMvc.perform(patch("/user/1")
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                        .param("data", invalidDataString)
                        .param("photo", "photo"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Delete user when authenticated and userId is valid then return success")
    @WithMockUser(username = "user1")
    public void testDeleteUserWhenAuthenticatedAndUserIdIsValidThenReturnSuccess() throws Exception {
        Mockito.doNothing().when(userService).deleteUser(Mockito.anyLong());

        mockMvc.perform(delete("/user/1")
                        .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Delete user when not authenticated then return 302")
    public void testDeleteUserWhenNotAuthenticatedThenReturn302() throws Exception {
        mockMvc.perform(delete("/user/1")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @DisplayName("Promote request when request is valid then return success message")
    @WithMockUser(username = "user1")
    public void testPromoteRequestFromUserWhenRequestIsValidThenReturnSuccessMessage() throws Exception {
        UserDevMsgRespondDto userDevMsgRespondDto = new UserDevMsgRespondDto("Promotion request sent");

        UserRequestDto userRequestDto = new UserRequestDto();
        userRequestDto.setEmail("user1@example.com");

        Mockito.when(userService.promoteRequest(Mockito.any(UserRequestDto.class))).thenReturn(userDevMsgRespondDto);

        mockMvc.perform(post("/user/request")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequestDto))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Promotion request sent"));
    }

    @Test
    @DisplayName("Promote request when user is admin and request is valid then return success message")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void testPromoteUserToGuideWhenUserIsAdminAndRequestIsValidThenReturnSuccessMessage() throws Exception {
        UserDevMsgRespondDto userDevMsgRespondDto = new UserDevMsgRespondDto("User promoted to guide");

        UserRequestDto userRequestDto = new UserRequestDto();
        userRequestDto.setEmail("user1@example.com");

        Mockito.when(roleService.changeUserRoleToGuide(Mockito.any(UserRequestDto.class))).thenReturn(userDevMsgRespondDto);

        mockMvc.perform(post("/user/role_change")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequestDto))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User promoted to guide"));
    }

    @Test
    @DisplayName("Promote user when user is not admin then return 403")
    @WithMockUser(username = "user1", roles = {"USER"})
    public void testPromoteUserToGuideWhenUserIsNotAdminThenReturn403() throws Exception {
        UserRequestDto userRequestDto = new UserRequestDto();
        userRequestDto.setEmail("user1@example.com");

        mockMvc.perform(post("/user/role_change")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequestDto)))
                .andExpect(status().isForbidden());
    }
}
