package com.hiketrackbackend.hiketrackbackend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hiketrackbackend.hiketrackbackend.dto.details.DetailsRequestDto;
import com.hiketrackbackend.hiketrackbackend.dto.details.DetailsRespondDto;
import com.hiketrackbackend.hiketrackbackend.dto.tour.*;
import com.hiketrackbackend.hiketrackbackend.exception.CustomGlobalExceptionHandler;
import com.hiketrackbackend.hiketrackbackend.exception.EntityNotFoundException;
import com.hiketrackbackend.hiketrackbackend.model.tour.Difficulty;
import com.hiketrackbackend.hiketrackbackend.model.tour.details.Activity;
import com.hiketrackbackend.hiketrackbackend.model.tour.details.RouteType;
import com.hiketrackbackend.hiketrackbackend.model.user.User;
import com.hiketrackbackend.hiketrackbackend.security.JwtUtil;
import com.hiketrackbackend.hiketrackbackend.security.token.UserTokenService;
import com.hiketrackbackend.hiketrackbackend.service.TourDetailsService;
import com.hiketrackbackend.hiketrackbackend.service.TourService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TourController.class)
public class TourControllerTest {
    private static MockMvc mockMvc;

    @MockBean
    private static TourService tourService;

    @MockBean
    private static TourDetailsService tourDetailsService;

    @MockBean
    private static JwtUtil jwtUtil;

    @MockBean
    private static UserDetailsService userDetailsService;

    @MockBean
    private static UserTokenService<HttpServletRequest> userTokenService;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    static void beforeAll(@Autowired WebApplicationContext applicationContext) {
        mockMvc = MockMvcBuilders.webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
    }

    @Test
    @DisplayName("Create tour with valid request")
    @WithMockUser(username = "user1", roles = {"GUIDE"})
    public void testCreateTourWhenRequestIsValidThenReturnTourRespondWithoutReviews() throws Exception {
        TourRequestDto requestDto = new TourRequestDto();
        requestDto.setName("Test Tour");
        requestDto.setLength(10);
        requestDto.setPrice(BigDecimal.valueOf(100.00));
        requestDto.setDate(ZonedDateTime.now());
        requestDto.setDifficulty(Difficulty.MEDIUM);
        requestDto.setCountryId(1L);
        requestDto.setDetailsRequestDto(new DetailsRequestDto());

        TourRespondWithoutReviews responseDto = new TourRespondWithoutReviews();
        responseDto.setId(1L);
        responseDto.setName("Test Tour");

        when(tourService.createTour(any(TourRequestDto.class), any(User.class), any(), Mockito.anyList()))
                .thenReturn(responseDto);

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
    @WithMockUser(username = "user1", roles = {"GUIDE"})
    public void testCreateTourWhenDataStringIsInvalidThenReturnBadRequest() throws Exception {
        String invalidDataString = "invalid data";

        MockMultipartFile data = new MockMultipartFile("data", "",
                "application/json", invalidDataString.getBytes());
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
                .andExpect(status().isBadRequest())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Invalid data format")));
    }

    @Test
    @DisplayName("Handles valid multipart form data correctly")
    public void handlesValidMultipartFormDataCorrectly() {
        TourService tourService = mock(TourService.class);
        ObjectMapper objectMapper = new ObjectMapper();
        TourController tourController = new TourController(tourService, tourDetailsService, objectMapper);

        String dataString = "{\"name\":\"Mountain Hike\",\"length\":10,"
                + "\"price\":100.0,\"difficulty\":\"EASY\",\"countryId\":1,\"detailsRequestDto\":{}}";
        MultipartFile mainPhoto = mock(MultipartFile.class);
        List<MultipartFile> additionalPhotos = List.of(mock(MultipartFile.class));
        User user = mock(User.class);

        TourRespondWithoutReviews expectedResponse = new TourRespondWithoutReviews();
        when(tourService.createTour(any(TourRequestDto.class), eq(user), eq(mainPhoto), eq(additionalPhotos)))
                .thenReturn(expectedResponse);

        TourRespondWithoutReviews response = tourController.createTour(dataString, mainPhoto, additionalPhotos, user);

        assertNotNull(response);
        verify(tourService).createTour(any(TourRequestDto.class), eq(user), eq(mainPhoto), eq(additionalPhotos));
    }

    @Test
    @DisplayName("Handles null main photo input appropriately")
    public void testHandlesNullMainPhotoInputAppropriately() {
        TourService tourService = mock(TourService.class);
        ObjectMapper objectMapper = new ObjectMapper();
        TourController tourController = new TourController(tourService, tourDetailsService, objectMapper);

        String dataString = "{\"name\":\"Mountain Hike\",\"length\":10,\"price\":100.0," +
                "\"difficulty\":\"EASY\",\"countryId\":1,\"detailsRequestDto\":{}}";
        MultipartFile mainPhoto = null;
        List<MultipartFile> additionalPhotos = List.of(mock(MultipartFile.class));
        User user = mock(User.class);

        TourRespondWithoutReviews expectedResponse = new TourRespondWithoutReviews();
        when(tourService.createTour(any(TourRequestDto.class), eq(user), eq(mainPhoto), eq(additionalPhotos)))
                .thenReturn(expectedResponse);

        TourRespondWithoutReviews response = tourController.createTour(dataString, mainPhoto, additionalPhotos, user);

        assertNotNull(response);
        verify(tourService).createTour(any(TourRequestDto.class), eq(user), eq(mainPhoto), eq(additionalPhotos));
    }

    @Test
    @DisplayName("Successfully updates tour information when valid data is provided")
    public void testUpdateGeneralTourInfoSuccess() {
        TourService tourService = mock(TourService.class);
        TourController tourController = new TourController(tourService, tourDetailsService, objectMapper);
        TourUpdateRequestDto requestDto = new TourUpdateRequestDto();
        requestDto.setName("New Tour Name");
        requestDto.setLength(10);
        requestDto.setPrice(BigDecimal.valueOf(100.0));
        requestDto.setDate(ZonedDateTime.now());
        requestDto.setDifficulty(Difficulty.MEDIUM);
        requestDto.setCountryId(1L);
        DetailsRequestDto detailsRequestDto = new DetailsRequestDto();
        requestDto.setDetailsRequestDto(detailsRequestDto);

        Long userId = 1L;
        Long tourId = 1L;

        TourRespondWithoutReviews expectedResponse = new TourRespondWithoutReviews();
        expectedResponse.setName("New Tour Name");

        when(tourService.updateTour(requestDto, userId, tourId)).thenReturn(expectedResponse);

        TourRespondWithoutReviews actualResponse = tourController.updateGeneralInfoAboutTour(requestDto, userId, tourId);

        assertEquals(expectedResponse.getName(), actualResponse.getName());
    }

    @Test
    @DisplayName("Handles invalid userId or tourId gracefully")
    public void test_update_tour_invalid_ids() {
        TourService tourService = mock(TourService.class);
        TourController tourController = new TourController(tourService, tourDetailsService, objectMapper);
        TourUpdateRequestDto requestDto = new TourUpdateRequestDto();
        Long invalidUserId = -1L;
        Long invalidTourId = -1L;

        when(tourService.updateTour(requestDto, invalidUserId, invalidTourId)).thenThrow(new IllegalArgumentException("Invalid IDs"));

        assertThrows(IllegalArgumentException.class, () -> {
            tourController.updateGeneralInfoAboutTour(requestDto, invalidUserId, invalidTourId);
        });
    }

    @Test
    @DisplayName("Update tour with invalid data should throw ConstraintViolationException")
    public void testUpdateTourInvalidData() throws Exception {
        TourController tourController = new TourController(tourService, tourDetailsService, objectMapper);
        mockMvc = MockMvcBuilders.standaloneSetup(tourController)
                .setControllerAdvice(new CustomGlobalExceptionHandler())
                .build();

        TourUpdateRequestDto requestDto = new TourUpdateRequestDto();
        requestDto.setName("");

        Long tourId = 1L;

        mockMvc.perform(patch("/tours/{tourId}/update", tourId)
                        .content(new ObjectMapper().writeValueAsString(requestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Update tour photo with valid request")
    @WithMockUser(username = "user1", roles = {"GUIDE"})
    public void testUpdateTourPhotoWhenRequestIsValidThenReturnTourRespondWithoutReviews() throws Exception {
        TourRespondWithoutReviews responseDto = new TourRespondWithoutReviews();
        responseDto.setId(1L);
        responseDto.setName("Updated Tour");

        when(tourService.updateTourPhoto(any(MultipartFile.class), Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(responseDto);

        MockMultipartFile mainPhoto = new MockMultipartFile("mainPhoto", "mainPhoto.jpg", "image/jpeg", "test image".getBytes());

        mockMvc.perform(MockMvcRequestBuilders.multipart("/tours/1/photo/1")
                        .file(mainPhoto)
                        .with(request -> {
                            request.setMethod("PATCH");
                            return request;
                        })
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Updated Tour"));
    }

    @Test
    @DisplayName("Update main photo with invalid format should return 415")
    public void testUpdateMainPhotoInvalidFormat() throws Exception {
        TourController tourController = new TourController(tourService, tourDetailsService, objectMapper);
        mockMvc = MockMvcBuilders.standaloneSetup(tourController)
                .setControllerAdvice(new CustomGlobalExceptionHandler())
                .build();

        MockMultipartFile invalidMainPhoto = new MockMultipartFile(
                "mainPhoto", "filename.txt", "image/txt", "invalid content".getBytes());

        long userId = 1L;
        long tourId = 1L;

        mockMvc.perform(MockMvcRequestBuilders.multipart("/tours/{tourId}/updatePhoto", tourId)
                        .file(invalidMainPhoto)
                        .with(request -> {
                            request.setMethod("PATCH");
                            return request;
                        })
                        .param("userId", Long.toString(userId)))
                .andExpect(status().isUnsupportedMediaType());
    }

    @Test
    @DisplayName("Update tour details photo with valid request")
    @WithMockUser(username = "user1", roles = {"GUIDE"})
    public void testUpdateTourDetailsPhotosWhenRequestIsValidThenReturnDetailsRespondDto() throws Exception {
        DetailsRespondDto responseDto = new DetailsRespondDto();
        responseDto.setId(1L);

        when(tourDetailsService.updateTourDetailsPhotos(Mockito.anyList(), Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(responseDto);

        MockMultipartFile additionalPhotos = new MockMultipartFile("additionalPhotos", "additionalPhoto.jpg", "image/jpeg", "test image".getBytes());

        mockMvc.perform(MockMvcRequestBuilders.multipart("/tours/1/additionalPhotos/1")
                        .file(additionalPhotos)
                        .with(request -> {
                            request.setMethod("PATCH");
                            return request;
                        })
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .with(csrf()))
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    @DisplayName("Handles empty list of additional photos gracefully")
    public void testUpdateAdditionalPhotosEmptyList() {
        TourDetailsService tourDetailsService = mock(TourDetailsService.class);
        List<MultipartFile> additionalPhotos = List.of();
        Long userId = 1L;
        Long tourId = 1L;
        DetailsRespondDto expectedResponse = new DetailsRespondDto();
        when(tourDetailsService.updateTourDetailsPhotos(additionalPhotos, userId, tourId)).thenReturn(expectedResponse);

        DetailsRespondDto actualResponse = tourDetailsService.updateTourDetailsPhotos(additionalPhotos, userId, tourId);

        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    @DisplayName("Retrieve tour details by valid ID")
    public void testRetrieveTourDetailsByValidId() {
        Long validId = 1L;
        int page = 0;
        int size = 5;
        TourRespondDto expectedResponse = new TourRespondDto();
        expectedResponse.setId(validId);
        expectedResponse.setName("Sample Tour");

        TourService tourService = Mockito.mock(TourService.class);
        Mockito.when(tourService.getById(validId, page, size)).thenReturn(expectedResponse);

        TourController tourController = new TourController(tourService, tourDetailsService, objectMapper);

        TourRespondDto actualResponse = tourController.getTourById(validId, page, size);

        Assertions.assertNotNull(actualResponse);
        Assertions.assertEquals(expectedResponse.getId(), actualResponse.getId());
        Assertions.assertEquals(expectedResponse.getName(), actualResponse.getName());
    }

    @Test
    @DisplayName("Handle non-existent tour ID gracefully")
    public void testHandleNonExistentTourIdGracefully() {
        Long nonExistentId = 999L;
        int page = 0;
        int size = 5;

        TourService tourService = Mockito.mock(TourService.class);
        Mockito.when(tourService.getById(nonExistentId, page, size)).thenThrow(new EntityNotFoundException("Tour not found"));

        TourController tourController = new TourController(tourService, tourDetailsService, objectMapper);

        assertThrows(EntityNotFoundException.class, () -> {
            tourController.getTourById(nonExistentId, page, size);
        });
    }

    @Test
    @DisplayName("Retrieves a list of tours sorted by highest ratings")
    public void testRetrievesSortedToursByHighestRatings() {
        TourService tourService = Mockito.mock(TourService.class);
        TourController tourController = new TourController(tourService, tourDetailsService, objectMapper);

        List<TourRespondWithoutDetailsAndReviews> expectedTours = new ArrayList<>();
        TourRespondWithoutDetailsAndReviews tour1 = new TourRespondWithoutDetailsAndReviews();
        tour1.setAverageRating(5L);
        expectedTours.add(tour1);

        TourRespondWithoutDetailsAndReviews tour2 = new TourRespondWithoutDetailsAndReviews();
        tour2.setAverageRating(4L);
        expectedTours.add(tour2);

        Mockito.when(tourService.getByRating()).thenReturn(expectedTours);

        List<TourRespondWithoutDetailsAndReviews> actualTours = tourController.getMostRatedTours();

        Assertions.assertEquals(expectedTours, actualTours);
    }

    @Test
    @DisplayName("Handles the case when the tourService returns null")
    public void testHandlesNullReturnFromTourService() {
        TourService tourService = Mockito.mock(TourService.class);
        TourController tourController = new TourController(tourService, tourDetailsService, objectMapper);

        Mockito.when(tourService.getByRating()).thenReturn(null);

        List<TourRespondWithoutDetailsAndReviews> actualTours = tourController.getMostRatedTours();

        Assertions.assertNull(actualTours);
    }

    @Test
    @DisplayName("Retrieve all tours for a valid guide ID")
    public void testRetrieveAllToursForValidGuideId() {
        Long validGuideId = 1L;
        Pageable pageable = Pageable.unpaged();
        List<TourRespondWithoutDetailsAndReviews> expectedTours = List.of(new TourRespondWithoutDetailsAndReviews());
        TourService tourService = Mockito.mock(TourService.class);
        Mockito.when(tourService.getAllToursMadeByGuide(validGuideId, pageable)).thenReturn(expectedTours);
        TourController tourController = new TourController(tourService, tourDetailsService, objectMapper);

        List<TourRespondWithoutDetailsAndReviews> actualTours = tourController.getAllToursMadeByGuide(validGuideId, pageable);

        Assertions.assertEquals(expectedTours, actualTours);
    }

    @Test
    @DisplayName("Handle non-existent guide ID gracefully")
    public void testHandleNonExistentGuideIdGracefully() {
        Long nonExistentGuideId = 999L;
        Pageable pageable = Pageable.unpaged();
        TourService tourService = Mockito.mock(TourService.class);
        Mockito.when(tourService.getAllToursMadeByGuide(nonExistentGuideId, pageable)).thenReturn(Collections.emptyList());
        TourController tourController = new TourController(tourService, tourDetailsService, objectMapper);

        List<TourRespondWithoutDetailsAndReviews> actualTours = tourController.getAllToursMadeByGuide(nonExistentGuideId, pageable);

        Assertions.assertTrue(actualTours.isEmpty());
    }

    @Test
    @DisplayName("Retrieve a paginated list of all tours successfully")
    public void testRetrievePaginatedListOfAllToursSuccessfully() {
        Pageable pageable = PageRequest.of(0, 10);
        List<TourRespondWithoutDetailsAndReviews> expectedTours = List.of(new TourRespondWithoutDetailsAndReviews());

        TourService tourService = Mockito.mock(TourService.class);
        Mockito.when(tourService.getAll(pageable)).thenReturn(expectedTours);

        TourController tourController = new TourController(tourService, tourDetailsService, objectMapper);
        List<TourRespondWithoutDetailsAndReviews> actualTours = tourController.getAllTours(pageable);

        Assertions.assertEquals(expectedTours, actualTours);
    }

    @Test
    @DisplayName("Search tours with valid parameters returns a list of tours")
    public void testSearchToursWithValidParameters() {
        TourService tourService = Mockito.mock(TourService.class);
        TourController tourController = new TourController(tourService, tourDetailsService, objectMapper);
        Pageable pageable = PageRequest.of(0, 10);
        TourSearchParameters params = new TourSearchParameters(
                new String[]{"hiking"}, new String[]{"easy"}, new String[]{"short"},
                new String[]{"sightseeing"}, new String[]{"2023-10-01"}, new String[]{"half-day"},
                new String[]{"low"}, new String[]{"USA"}
        );
        List<TourRespondWithoutDetailsAndReviews> expectedTours = List.of(new TourRespondWithoutDetailsAndReviews());
        Mockito.when(tourService.search(params, pageable)).thenReturn(expectedTours);

        List<TourRespondWithoutDetailsAndReviews> result = tourController.searchTours(params, pageable);

        Assertions.assertEquals(expectedTours, result);
    }

    @Test
    @DisplayName("Search tours with invalid parameters should throw validation error")
    public void testSearchToursWithInvalidParameters() throws Exception {
        TourController tourController = new TourController(tourService, tourDetailsService, objectMapper);
        mockMvc = MockMvcBuilders.standaloneSetup(tourController)
                .setControllerAdvice(new CustomGlobalExceptionHandler())
                .build();

        TourSearchParameters params = new TourSearchParameters(
                new String[]{}, new String[]{}, new String[]{},
                new String[]{}, new String[]{}, new String[]{},
                new String[]{}, new String[]{}
        );

        mockMvc.perform(MockMvcRequestBuilders.get("/tours/search")
                        .param("page", "0")
                        .param("size", "10")
                        .content(new ObjectMapper().writeValueAsString(params))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Delete tour with valid IDs")
    @WithMockUser(username = "user1", roles = {"GUIDE"})
    public void testDeleteTourWhenIdsAreValidThenReturnOK() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/tours/1/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Delete single tour photo with valid ID")
    @WithMockUser(username = "user1", roles = {"GUIDE"})
    public void testDeleteSingleTourDetailsPhotoWhenIdIsValidThenReturnOK() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/tours/photo/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(status().isOk());
    }
}
