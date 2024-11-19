package com.hiketrackbackend.hiketrackbackend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hiketrackbackend.hiketrackbackend.dto.tour.*;
import com.hiketrackbackend.hiketrackbackend.dto.tour.details.DetailsRequestDto;
import com.hiketrackbackend.hiketrackbackend.model.tour.Difficulty;
import com.hiketrackbackend.hiketrackbackend.model.tour.details.Activity;
import com.hiketrackbackend.hiketrackbackend.model.tour.details.RouteType;
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
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Set;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TourControllerTest {
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
    @DisplayName("Create tour with valid request")
    @WithMockUser(roles = "GUIDE")
    public void testCreateTourWhenRequestIsValidThenReturnTourRespondWithoutReviews() throws Exception {
        TourRequestDto requestDto = setRequest();

        setUserToContext();
        MockMultipartFile data = new MockMultipartFile("data", "",
                "application/json",
                objectMapper.writeValueAsBytes(requestDto));
        MockMultipartFile mainPhoto = new MockMultipartFile("mainPhoto", "mainPhoto.jpg",
                "image/jpeg", "test image".getBytes());
        MockMultipartFile additionalPhotos = new MockMultipartFile("additionalPhotos",
                "additionalPhoto.jpg", "image/jpeg", "test image".getBytes());

        mockMvc.perform(multipart("/tours")
                        .file(data)
                        .file(mainPhoto)
                        .file(additionalPhotos)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Create tour with invalid dataString")
    @WithMockUser(roles = "GUIDE")
    public void testCreateTourWhenDataStringIsInvalidThenReturnBadRequest() throws Exception {
        String invalidDataString = "invalid data";

        setUserToContext();
        MockMultipartFile data = new MockMultipartFile("data", "",
                "application/json", invalidDataString.getBytes());
        MockMultipartFile mainPhoto = new MockMultipartFile("mainPhoto", "mainPhoto.jpeg",
                "image/jpeg", "test image".getBytes());
        MockMultipartFile additionalPhotos = new MockMultipartFile("additionalPhotos",
                "additionalPhoto.jpeg", "image/jpeg", "test image".getBytes());

        mockMvc.perform(multipart("/tours")
                        .file(data)
                        .file(mainPhoto)
                        .file(additionalPhotos)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Invalid data format")));
    }

    @Test
    @DisplayName("Create tour with not authorized user")
    public void createTour_Unauthorized_ShouldReturnUnauthorized() throws Exception {
        TourRequestDto requestDto = setRequest();

        MockMultipartFile data = new MockMultipartFile("data", "",
                "application/json", objectMapper.writeValueAsBytes(requestDto));
        MockMultipartFile mainPhoto = new MockMultipartFile("mainPhoto", "mainPhoto.jpg",
                "image/jpeg", "test image".getBytes());
        MockMultipartFile additionalPhotos = new MockMultipartFile("additionalPhotos",
                "additionalPhoto.jpg", "image/jpeg", "test image".getBytes());

        mockMvc.perform(multipart("/tours")
                        .file(data)
                        .file(mainPhoto)
                        .file(additionalPhotos)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .with(csrf()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Handles empty photo input appropriately")
    public void testHandlesNullMainPhotoInputAppropriately() throws Exception {
        TourRequestDto requestDto = setRequest();

        MockMultipartFile data = new MockMultipartFile("data", "",
                "application/json", objectMapper.writeValueAsBytes(requestDto));
        MockMultipartFile mainPhoto = new MockMultipartFile("mainPhoto", "",
                "", "".getBytes());
        MockMultipartFile additionalPhotos = new MockMultipartFile("additionalPhotos",
                "additionalPhoto.jpg", "image/jpeg", "test image".getBytes());
        setUserToContext();

        mockMvc.perform(multipart("/tours")
                        .file(data)
                        .file(mainPhoto)
                        .file(additionalPhotos)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .with(csrf()))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "GUIDE")
    @DisplayName("Successfully updates tour information when valid data is provided")
    @Sql(scripts = "classpath:database/tour/add-tour.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void updateGeneralInfoAboutTour_ShouldUpdateTour() throws Exception {
        setUserToContext();
        TourUpdateRequestDto requestDto = setUpdateRequest();

        mockMvc.perform(patch("/tours/{tourId}/{userId}", 1, 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Title"));
    }

    @Test
    @WithMockUser(roles = "GUIDE")
    @DisplayName("Update tour with invalid request")
    public void updateGeneralInfoAboutTour_InvalidData_ShouldReturnBadRequest() throws Exception {
        TourUpdateRequestDto updateRequest = new TourUpdateRequestDto();
        setUserToContext();

        mockMvc.perform(patch("/tours/{tourId}/{userId}", 1, 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "GUIDE")
    @DisplayName("Another user tried to update current users tour")
    public void updateGeneralInfoAboutTour_WrongUser_ShouldReturnForbidden() throws Exception {
        TourUpdateRequestDto updateRequest = setUpdateRequest();
        setUserToContext();

        Long anotherUser = 2L;
        mockMvc.perform(patch("/tours/{tourId}/{userId}", 1, anotherUser)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Handles invalid tourId gracefully")
    @WithMockUser(roles = "GUIDE")
    public void test_update_tour_invalid_ids() throws Exception {
        TourUpdateRequestDto updateRequest = setUpdateRequest();
        setUserToContext();
        Long invalidTourId = -1L;

        mockMvc.perform(patch("/tours/{tourId}/{userId}", invalidTourId, 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Update tour photo with valid request")
    @WithMockUser(roles = "GUIDE")
    @Sql(scripts = "classpath:database/tour/add-tour.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void testUpdateTourPhotoWhenRequestIsValidThenReturnTourRespondWithoutReviews() throws Exception {
        MockMultipartFile mainPhoto = new MockMultipartFile(
                "mainPhoto",
                "mainPhoto.jpg",
                "image/jpeg",
                "test image".getBytes());

        setUserToContext();
        mockMvc.perform(MockMvcRequestBuilders
                        .multipart("/tours/{tourId}/photo/{userId}", 1, 1)
                        .file(mainPhoto)
                        .with(request -> {
                            request.setMethod("PATCH");
                            return request;
                        })
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Update main photo with invalid format should return 400")
    @WithMockUser(roles = "GUIDE")
    public void testUpdateMainPhotoInvalidFormat() throws Exception {
        MockMultipartFile invalidMainPhoto = new MockMultipartFile(
                "mainPhoto",
                "filename.txt",
                "image/txt",
                "invalid content".getBytes());

        setUserToContext();
        mockMvc.perform(MockMvcRequestBuilders
                        .multipart("/tours/{tourId}/photo/{userId}", 1, 1)
                        .file(invalidMainPhoto)
                        .with(request -> {
                            request.setMethod("PATCH");
                            return request;
                        })
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .with(csrf()))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Get tour by valid ID")
    @Sql(scripts = "classpath:database/tour/add-tour.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void testRetrieveTourDetailsByValidId() throws Exception {
        mockMvc.perform(get("/tours/{id}", 1)
                        .param("page", "0")
                        .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Tiger Leaping Gorge"));
    }

    @Test
    @DisplayName("Handle non-existent tour ID gracefully")
    public void testHandleNonExistentTourIdGracefully() throws Exception {
        mockMvc.perform(get("/tours/{id}", -1)
                        .param("page", "0")
                        .param("size", "5"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Retrieves a list of tours sorted by highest ratings")
    @Sql(scripts = "classpath:database/tour/add-tour.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void testRetrievesSortedToursByHighestRatings() throws Exception {
        mockMvc.perform(get("/tours/popular"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(7)));
    }

    @Test
    @DisplayName("Retrieve all tours for a valid guide ID")
    @Sql(scripts = "classpath:database/tour/add-tour.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void testRetrieveAllToursForValidGuideId() throws Exception {
        mockMvc.perform(get("/tours/guide/{guideId}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[1].name").value("Tiger Leaping Gorge3"));
    }

    @Test
    @DisplayName("Handle non-existent guide ID gracefully")
    public void testHandleNonExistentGuideIdGracefully() throws Exception {
        mockMvc.perform(get("/tours/guide/{guideId}", -1))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Retrieve a paginated list of all tours successfully")
    @Sql(scripts = "classpath:database/tour/add-tour.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void testRetrievePaginatedListOfAllToursSuccessfully() throws Exception {
        mockMvc.perform(get("/tours")
                    .param("page", "0")
                    .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(5)))
                .andExpect(jsonPath("$[4].name").value("Tiger Leaping Gorge5"));
    }

    @Test
    @DisplayName("Search tours with few valid parameters returns a list of tours")
    @Sql(scripts = "classpath:database/tour/add-tour.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void testSearchToursWithValidParameters() throws Exception {
       mockMvc.perform(get("/tours/search")
                .param("difficulty", "Easy")
                .param("length", "10000"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].difficulty").value("Easy"))
                .andExpect(jsonPath("$[1].difficulty").value("Easy"));
    }

    @Test
    @DisplayName("Delete tour with valid IDs")
    @WithMockUser(roles = "GUIDE")
    @Sql(scripts = "classpath:database/tour/add-tour.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void testDeleteTourWhenIdsAreValidThenReturnOK() throws Exception {
        setUserToContext();
        mockMvc.perform(MockMvcRequestBuilders.delete("/tours/{tourId}/{userId}", 1, 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(status().isOk());

        // check that tour not exist anymore
        mockMvc.perform(MockMvcRequestBuilders.get("/tours/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Delete tour with not valid IDs")
    @WithMockUser(roles = "GUIDE")
    @Sql(scripts = "classpath:database/tour/add-tour.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void testDeleteTourWhenIdsAreNotValidThenReturn404() throws Exception {
        setUserToContext();
        Long notExistTour = 20L;
        mockMvc.perform(MockMvcRequestBuilders.delete("/tours/{tourId}/{userId}", notExistTour, 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(status().isNotFound());
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

    private TourRequestDto setRequest() {
        DetailsRequestDto detailsRequestDto = new DetailsRequestDto();
        detailsRequestDto.setElevationGain(22222);
        detailsRequestDto.setRouteType(RouteType.MULTI_DESTINATION);
        detailsRequestDto.setDuration(10000);
        detailsRequestDto.setMap("link");
        detailsRequestDto.setActivity(Activity.BIKING);
        detailsRequestDto.setDescription("test");

        TourRequestDto requestDto = new TourRequestDto();
        requestDto.setName("Test Tour");
        requestDto.setLength(10);
        requestDto.setPrice(BigDecimal.valueOf(100.00));
        requestDto.setDate(ZonedDateTime.now());
        requestDto.setDifficulty(Difficulty.MEDIUM);
        requestDto.setCountryId(1L);
        requestDto.setDetailsRequestDto(detailsRequestDto);
        return requestDto;
    }

    private TourUpdateRequestDto setUpdateRequest() {
        TourUpdateRequestDto requestDto = new TourUpdateRequestDto();
        requestDto.setName("Updated Title");
        requestDto.setLength(10);
        requestDto.setPrice(BigDecimal.valueOf(100.0));
        requestDto.setDate(ZonedDateTime.now());
        requestDto.setDifficulty(Difficulty.MEDIUM);
        requestDto.setCountryId(1L);

        DetailsRequestDto detailsRequestDto = new DetailsRequestDto();
        requestDto.setDetailsRequestDto(detailsRequestDto);
        return requestDto;
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
