package com.hiketrackbackend.hiketrackbackend.controller;

import com.hiketrackbackend.hiketrackbackend.dto.tour.details.file.TourDetailFileRespondDto;
import com.hiketrackbackend.hiketrackbackend.exception.EntityNotFoundException;
import com.hiketrackbackend.hiketrackbackend.model.user.Role;
import com.hiketrackbackend.hiketrackbackend.model.user.User;
import com.hiketrackbackend.hiketrackbackend.model.user.UserProfile;
import io.jsonwebtoken.io.IOException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.*;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TourDetailsControllerTest {
    private static MockMvc mockMvc;

    @Autowired
    protected TestRestTemplate restTemplate;

    @BeforeAll
    static void beforeAll(@Autowired WebApplicationContext applicationContext,
                          @Autowired DataSource dataSource) {
        mockMvc = MockMvcBuilders.webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
        List<String> scripts = List.of(
                "database/tour/delete-all-tour-table.sql",
                "database/tour/delete-all-tour-detail-table.sql",
                "database/tour/delete-all-tour-details-files-table.sql"
        );
        executeSqlScripts(dataSource, scripts);
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void afterEach(@Autowired DataSource dataSource) {
        List<String> scripts = List.of(
                "database/tour/delete-all-tour-table.sql",
                "database/tour/delete-all-tour-detail-table.sql",
                "database/tour/delete-all-tour-details-files-table.sql"
        );
        executeSqlScripts(dataSource, scripts);
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("Update tour details photo with valid request")
    @WithMockUser(roles = "GUIDE")
    @Sql(scripts = "classpath:database/tour/add-tour.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/tour/add-tour-details.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void testUpdateTourDetailsPhotosWhenRequestIsValidThenReturnDetailsRespondDto() throws Exception {
        MockMultipartFile additionalPhoto = new MockMultipartFile(
                "additionalPhotos",
                "additionalPhoto.jpg",
                "image/jpg",
                "test image".getBytes());

        setUserToContext();
        mockMvc.perform(MockMvcRequestBuilders
                        .multipart("/tour_details/{tourId}/additionalPhotos/{userId}", 1, 1)
                        .file(additionalPhoto)
                        .with(request -> {
                            request.setMethod("PATCH");
                            return request;
                        })
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Handles empty invalid format of additional photos gracefully")
    public void testUpdateAdditionalPhotosInvalidFormatReturn400() throws Exception {
        MockMultipartFile additionalPhoto = new MockMultipartFile(
                "additionalPhotos",
                "additionalPhoto.txt",
                "image/txt",
                "test image".getBytes());

        setUserToContext();
        mockMvc.perform(MockMvcRequestBuilders.multipart("/tour_details/{tourId}/additionalPhotos/{userId}", 1, 1)
                        .file(additionalPhoto)
                        .with(request -> {
                            request.setMethod("PATCH");
                            return request;
                        })
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .with(csrf()))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Sql(scripts = "classpath:database/tour/add-single-tour-details-file.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @DisplayName("Get tour detail photo successfully")
    void testGetTourDetailPhotoSuccess() {
        long validPhotoId = 1L;
        TourDetailFileRespondDto expectedResponse = new TourDetailFileRespondDto();
        expectedResponse.setFileUrl("link1");

        ResponseEntity<TourDetailFileRespondDto> response = restTemplate.getForEntity(
                "/tour_details/photo/" + validPhotoId, TourDetailFileRespondDto.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(expectedResponse.getFileUrl(), response.getBody().getFileUrl());
    }

    @Test
    @DisplayName("Get single tour detail photo with not valid ID")
    void testGetTourDetailPhotoValidationFailure() {
        long invalidPhotoId = -1L;

        ResponseEntity<Exception> response = restTemplate.getForEntity(
                "/tour_details/photo/" + invalidPhotoId, Exception.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @DisplayName("Get not exist photo with valid id")
    void testGetTourDetailPhotoNotFound() {
        long nonExistentPhotoId = 99999L;

        ResponseEntity<EntityNotFoundException> response = restTemplate.getForEntity(
                "/tour_details/photo/" + nonExistentPhotoId, EntityNotFoundException.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @Sql(scripts = "classpath:database/tour/add-tour-details-file.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @DisplayName("Get all additional photos by tour details ID")
    void testGetAllTourDetailPhotosSuccess() {
        long validTourDetailId = 20L;

        ResponseEntity<Set<TourDetailFileRespondDto>> response = restTemplate.exchange(
                "/tour_details/all_detail_photos/" + validTourDetailId,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
    }

    @Test
    @DisplayName("Get all additional photos by invalid tour details ID")
    void testGetAllTourDetailPhotosValidationFailure() {
        long invalidTourDetailId = -1L;

        ResponseEntity<Exception> response = restTemplate.getForEntity(
                "/tour_details/all_detail_photos/" + invalidTourDetailId, Exception.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @DisplayName("Get all additional photos by tour details ID when its not exist")
    void testGetAllTourDetailPhotosNotFound() {
        long nonExistentTourDetailId = 9999L;

        ResponseEntity<Set<TourDetailFileRespondDto>> response = restTemplate.exchange(
                "/tour_details/all_detail_photos/" + nonExistentTourDetailId,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());
    }

    @Test
    @DisplayName("Delete single tour photo with valid ID")
    @WithMockUser(roles = "GUIDE")
    @Sql(scripts = "classpath:database/tour/add-single-tour-details-file.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void testDeleteSingleTourDetailsPhotoWhenIdIsValidThenReturnOK() throws Exception {
        setUserToContext();
        mockMvc.perform(MockMvcRequestBuilders.delete("/tour_details/additional_photo/{additionalPhotoId}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(status().isOk());
    }

    private static void executeSqlScripts(DataSource dataSource, List<String> scriptPaths) {
        for (String scriptPath : scriptPaths) {
            try (Connection connection = dataSource.getConnection()) {
                connection.setAutoCommit(true);
                ScriptUtils.executeSqlScript(
                        connection,
                        new ClassPathResource(scriptPath)
                );
            } catch (SQLException | IOException e) {
                throw new RuntimeException("Failed to execute script: " + scriptPath, e);
            }
        }
    }

    private void setUserToContext() {
        final User user = new User();
        user.setId(1L);
        user.setEmail("test@test.com");
        user.setPassword("test");
        user.setUserProfile(new UserProfile());
        Role role = new Role();
        role.setId(1L);
        role.setName(Role.RoleName.ROLE_GUIDE);
        user.setRoles(Set.of(role));
        user.setLastName("test");
        user.setConfirmed(true);

        final UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
