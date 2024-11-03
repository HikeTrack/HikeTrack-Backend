package com.hiketrackbackend.hiketrackbackend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hiketrackbackend.hiketrackbackend.dto.reviews.ReviewRequestDto;
import com.hiketrackbackend.hiketrackbackend.dto.reviews.ReviewsRespondDto;
import com.hiketrackbackend.hiketrackbackend.exception.EntityNotFoundException;
import com.hiketrackbackend.hiketrackbackend.model.user.User;
import com.hiketrackbackend.hiketrackbackend.security.JwtUtil;
import com.hiketrackbackend.hiketrackbackend.security.token.UserTokenService;
import com.hiketrackbackend.hiketrackbackend.service.ReviewService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReviewController.class)
public class ReviewControllerTest {
    private static MockMvc mockMvc;

    @MockBean
    private static ReviewService reviewService;

    @MockBean
    private static JwtUtil jwtUtil;

    @MockBean
    private static UserDetailsService userDetailsService;

    @MockBean
    private static UserTokenService<HttpServletRequest> userTokenService;

    @BeforeAll
    static void beforeAll(@Autowired WebApplicationContext applicationContext) {
        mockMvc = MockMvcBuilders.webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
    }

    @Test
    @DisplayName("Create review with invalid request")
    @WithMockUser(username = "user1")
    public void testCreateReviewWhenInvalidRequestThenReturnBadRequest() throws Exception {
        ReviewRequestDto requestDto = new ReviewRequestDto();
        requestDto.setContent("");

        mockMvc.perform(post("/reviews/1")
                        .content(new ObjectMapper().writeValueAsString(requestDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Create review without authentication")
    public void testCreateReviewWhenNotAuthenticatedThenReturnForbidden() throws Exception {
        ReviewRequestDto requestDto = new ReviewRequestDto();
        requestDto.setContent("Great tour!");

        mockMvc.perform(post("/reviews/1")
                        .content(new ObjectMapper().writeValueAsString(requestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Successfully create a review when valid data is provided")
    public void testCreateReviewWithValidData() {
        ReviewService reviewService = mock(ReviewService.class);
        ReviewRequestDto requestDto = new ReviewRequestDto();
        requestDto.setContent("Great tour!");
        User user = new User();
        user.setId(1L);
        Long tourId = 1L;

        ReviewsRespondDto expectedResponse = new ReviewsRespondDto();
        expectedResponse.setId(1L);
        expectedResponse.setUserId(user.getId());
        expectedResponse.setContent(requestDto.getContent());
        expectedResponse.setTourId(tourId);

        when(reviewService.createReview(requestDto, user, tourId)).thenReturn(expectedResponse);

        ReviewController reviewController = new ReviewController(reviewService);
        ReviewsRespondDto actualResponse = reviewController.createReview(requestDto, tourId, user);

        assertEquals(expectedResponse.getId(), actualResponse.getId());
        assertEquals(expectedResponse.getUserId(), actualResponse.getUserId());
        assertEquals(expectedResponse.getContent(), actualResponse.getContent());
        assertEquals(expectedResponse.getTourId(), actualResponse.getTourId());
    }

    @Test
    @DisplayName("Update review with valid request")
    @WithMockUser(username = "user1")
    public void testUpdateReviewWhenValidRequestThenReturnUpdatedReview() throws Exception {
        ReviewRequestDto requestDto = new ReviewRequestDto();
        requestDto.setContent("Updated review content");

        ReviewsRespondDto expectedResponse = new ReviewsRespondDto();
        expectedResponse.setId(1L);
        expectedResponse.setUserId(1L);
        expectedResponse.setContent("Updated review content");
        expectedResponse.setTourId(1L);
        expectedResponse.setDateCreated(LocalDateTime.now());

        when(reviewService.updateReview(any(ReviewRequestDto.class), eq(1L))).thenReturn(expectedResponse);

        mockMvc.perform(patch("/reviews/1")
                        .content(new ObjectMapper().writeValueAsString(requestDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(expectedResponse.getId()))
                .andExpect(jsonPath("$.userId").value(expectedResponse.getUserId()))
                .andExpect(jsonPath("$.content").value(expectedResponse.getContent()))
                .andExpect(jsonPath("$.tourId").value(expectedResponse.getTourId()));
    }

    @Test
    @DisplayName("Update review with invalid request")
    @WithMockUser(username = "user1")
    public void testUpdateReviewWhenInvalidRequestThenThrowException() throws Exception {
        ReviewRequestDto requestDto = new ReviewRequestDto();
        requestDto.setContent("");

        mockMvc.perform(patch("/reviews/1")
                        .content(new ObjectMapper().writeValueAsString(requestDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Update review without authentication")
    public void testUpdateReviewWhenNotAuthenticatedThenThrowException() throws Exception {
        ReviewRequestDto requestDto = new ReviewRequestDto();
        requestDto.setContent("Updated review content");

        mockMvc.perform(patch("/reviews/1")
                        .content(new ObjectMapper().writeValueAsString(requestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Get all reviews by user when user is authorized and request is valid")
    @WithMockUser(username = "user1")
    public void testGetAllReviewsByUserWhenUserIsAuthorizedAndRequestIsValidThenReturnReviews() throws Exception {
        Long userId = 1L;
        ReviewsRespondDto review1 = new ReviewsRespondDto();
        review1.setId(1L);
        review1.setUserId(userId);
        review1.setContent("Review 1");
        review1.setTourId(1L);
        review1.setDateCreated(LocalDateTime.now());

        ReviewsRespondDto review2 = new ReviewsRespondDto();
        review2.setId(2L);
        review2.setUserId(userId);
        review2.setContent("Review 2");
        review2.setTourId(2L);
        review2.setDateCreated(LocalDateTime.now());

        List<ReviewsRespondDto> reviews = Arrays.asList(review1, review2);

        when(reviewService.getAllByUserId(eq(userId), any(Pageable.class))).thenReturn(reviews);

        mockMvc.perform(get("/reviews/user/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(review1.getId()))
                .andExpect(jsonPath("$[0].userId").value(review1.getUserId()))
                .andExpect(jsonPath("$[0].content").value(review1.getContent()))
                .andExpect(jsonPath("$[0].tourId").value(review1.getTourId()))
                .andExpect(jsonPath("$[1].id").value(review2.getId()))
                .andExpect(jsonPath("$[1].userId").value(review2.getUserId()))
                .andExpect(jsonPath("$[1].content").value(review2.getContent()))
                .andExpect(jsonPath("$[1].tourId").value(review2.getTourId()));
    }

    @Test
    @DisplayName("Get all reviews by user when user is not authorized")
    public void testGetAllReviewsByUserWhenUserIsNotAuthorizedThenReturnRedirection() throws Exception {
        mockMvc.perform(get("/reviews/user/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @DisplayName("Get all reviews by user when userId is invalid")
    @WithMockUser(username = "user1")
    public void testGetAllReviewsByUserWhenUserIdIsInvalidThenReturnBadRequest() throws Exception {
        mockMvc.perform(get("/reviews/user/invalid")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Get all review by tour when called with valid parameters")
    @WithMockUser(username = "user1")
    public void testGetAllReviewsByTourWhenCalledWithValidParametersThenReturnListOfReviewsRespondDto() throws Exception {
        Long tourId = 1L;
        ReviewsRespondDto review1 = new ReviewsRespondDto();
        review1.setId(1L);
        review1.setUserId(1L);
        review1.setContent("Review 1");
        review1.setTourId(tourId);
        review1.setDateCreated(LocalDateTime.now());

        ReviewsRespondDto review2 = new ReviewsRespondDto();
        review2.setId(2L);
        review2.setUserId(2L);
        review2.setContent("Review 2");
        review2.setTourId(tourId);
        review2.setDateCreated(LocalDateTime.now());

        List<ReviewsRespondDto> reviews = Arrays.asList(review1, review2);

        when(reviewService.getAllByTourId(eq(tourId), any(Pageable.class))).thenReturn(reviews);

        mockMvc.perform(get("/reviews/tour/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(review1.getId()))
                .andExpect(jsonPath("$[0].userId").value(review1.getUserId()))
                .andExpect(jsonPath("$[0].content").value(review1.getContent()))
                .andExpect(jsonPath("$[0].tourId").value(review1.getTourId()))
                .andExpect(jsonPath("$[1].id").value(review2.getId()))
                .andExpect(jsonPath("$[1].userId").value(review2.getUserId()))
                .andExpect(jsonPath("$[1].content").value(review2.getContent()))
                .andExpect(jsonPath("$[1].tourId").value(review2.getTourId()));
    }

    @Test
    @DisplayName("Get all review by tour when called with an invalid tourId")
    @WithMockUser(username = "user1")
    public void testGetAllReviewsByTourWhenCalledWithInvalidTourIdThenReturnBadRequest() throws Exception {
        mockMvc.perform(get("/reviews/tour/invalid")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Delete review with valid review ID")
    @WithMockUser(username = "user1")
    public void testDeleteReviewWhenReviewIdIsValidThenReviewIsDeleted() throws Exception {
        Long reviewId = 1L;
        doNothing().when(reviewService).deleteById(reviewId);

        mockMvc.perform(delete("/reviews/1")
                        .with(csrf()))
                .andExpect(status().isOk());

        Mockito.verify(reviewService).deleteById(reviewId);
    }

    @Test
    @DisplayName("Delete review with invalid review ID")
    @WithMockUser(username = "user1")
    public void testDeleteReviewWhenReviewIdIsInvalidThenNotFound() throws Exception {
        Long reviewId = 999L;
        doThrow(new EntityNotFoundException("Review not found")).when(reviewService).deleteById(reviewId);

        mockMvc.perform(delete("/reviews/999")
                        .with(csrf()))
                .andExpect(status().isNotFound());
    }
}
