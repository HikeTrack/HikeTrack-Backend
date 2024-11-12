package com.hiketrackbackend.hiketrackbackend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hiketrackbackend.hiketrackbackend.dto.reviews.ReviewRequestDto;
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
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ReviewControllerTest {
    private static MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @BeforeAll
    static void beforeAll(@Autowired WebApplicationContext applicationContext,
                          @Autowired DataSource dataSource) {
        mockMvc = MockMvcBuilders.webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
        List<String> scripts = List.of(
                "database/review/delete-review.sql",
                "database/tour/delete-all-tour-table.sql"
        );

        executeSqlScripts(dataSource, scripts);

    }

    @AfterEach
    void afterEach(@Autowired DataSource dataSource) {
        List<String> scripts = List.of(
                "database/review/delete-review.sql",
                "database/tour/delete-all-tour-table.sql"
        );

        executeSqlScripts(dataSource, scripts);
    }

    @Test
    @DisplayName("Create review with valid data")
    @WithMockUser(roles = "USER")
    @Sql(scripts = "classpath:database/tour/add-tour.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void createReview_shouldCreateReviewSuccessfully() throws Exception {
        ReviewRequestDto requestDto = new ReviewRequestDto();
        requestDto.setContent("Great tour!");

        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        setUserToContext();

        mockMvc.perform(post("/reviews/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("Great tour!"));
    }

    @Test
    @DisplayName("Create review with invalid request")
    @WithMockUser(roles = "USER")
    @Sql(scripts = "classpath:database/tour/add-tour.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void testCreateReviewWhenInvalidRequestThenReturnBadRequest() throws Exception {
        ReviewRequestDto requestDto = new ReviewRequestDto();
        requestDto.setContent("");

        setUserToContext();
        mockMvc.perform(post("/reviews/1")
                        .content(objectMapper.writeValueAsString(requestDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Create review with not authenticated user")
    public void createReview_shouldFailWithoutAuthentication() throws Exception {
        ReviewRequestDto requestDto = new ReviewRequestDto();
        requestDto.setContent("Unauthorized review!");

        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        mockMvc.perform(post("/reviews/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Update review with valid data")
    @Sql(scripts = "classpath:database/tour/add-tour.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/review/add-reviews.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void updateReview_shouldUpdateReviewSuccessfully() throws Exception {
        ReviewRequestDto requestDto = new ReviewRequestDto();
        requestDto.setContent("Updated review content");

        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        setUserToContext();
        mockMvc.perform(patch("/reviews/1/1/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("Updated review content"));
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Test review with invalid review ID")
    @Sql(scripts = "classpath:database/tour/add-tour.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/review/add-reviews.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void updateReview_shouldFailWithInvalidReviewId() throws Exception {
        ReviewRequestDto requestDto = new ReviewRequestDto();
        requestDto.setContent("Invalid ID");

        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        setUserToContext();

        mockMvc.perform(patch("/reviews/1/-1/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest)
                        .with(csrf()))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Test review with invalid tour ID")
    @Sql(scripts = "classpath:database/tour/add-tour.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/review/add-reviews.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void updateReview_shouldFailWithoutAuthentication() throws Exception {
        ReviewRequestDto requestDto = new ReviewRequestDto();
        requestDto.setContent("Unauthorized update!");

        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        mockMvc.perform(patch("/reviews/1/1/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Get all reviews by user with valid data")
    public void getAllReviewsByUser_shouldReturnReviews() throws Exception {
        setUserToContext();
        mockMvc.perform(get("/reviews/user/1")
                        .param("page", "0")
                        .param("size", "10")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").isNotEmpty());
    }

    @Test
    @DisplayName("Get reviews of user which not currently logged in")
    @WithMockUser(roles = "USER")
    public void getAllReviewsByUser_shouldFailIfNotUserWhichAreCurrentlyLogin() throws Exception {
        setUserToContext();

        // logged-in user with id 1, but trying to get excess to reviews of other user
        mockMvc.perform(get("/reviews/user/2")
                        .param("page", "0")
                        .param("size", "10")
                        .with(csrf()))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Get reviews by not authorized user")
    public void getAllReviewsByUser_shouldFailWithoutAuthentication() throws Exception {
        mockMvc.perform(get("/reviews/user/1")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Get all reviews by specific tour")
    public void getAllReviewsByTour_shouldReturnReviews() throws Exception {
        mockMvc.perform(get("/reviews/tour/1")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").isNotEmpty());
    }

    @Test
    @DisplayName("Get reviews by invalid tour ID")
    public void getAllReviewsByTour_shouldFailWithInvalidTourId() throws Exception {
        mockMvc.perform(get("/reviews/tour/-1")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Delete review for current logged in user")
    @WithMockUser(roles = "USER")
    @Sql(scripts = "classpath:database/review/add-reviews.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void deleteReview_shouldDeleteReviewSuccessfully() throws Exception {
        setUserToContext();
        mockMvc.perform(delete("/reviews/1/1/1")
                        .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Delete review with invalid ID")
    @WithMockUser(roles = "USER")
    public void deleteReview_shouldFailWithInvalidReviewId() throws Exception {
        setUserToContext();
        mockMvc.perform(delete("/reviews/-1/1/1").with(csrf()))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Delete review with not authorized user")
    public void deleteReview_shouldFailWithoutAuthentication() throws Exception {
        mockMvc.perform(delete("/reviews/1/1/1"))
                .andExpect(status().isUnauthorized());
    }

    private void setUserToContext() {
        final User user = new User();
        user.setId(1L);
        user.setEmail("test@test.com");
        user.setPassword("test");
        user.setUserProfile(new UserProfile());
        Role role = new Role();
        role.setId(1L);
        role.setName(Role.RoleName.ROLE_USER);
        user.setRoles(Set.of(role));
        user.setLastName("test");
        user.setConfirmed(true);

        final UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
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
}
