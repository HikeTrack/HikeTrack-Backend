//package com.hiketrackbackend.hiketrackbackend.controller;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.hiketrackbackend.hiketrackbackend.dto.reviews.ReviewRequestDto;
//import com.hiketrackbackend.hiketrackbackend.dto.reviews.ReviewsRespondDto;
//import com.hiketrackbackend.hiketrackbackend.exception.EntityNotFoundException;
//import com.hiketrackbackend.hiketrackbackend.model.user.User;
//import com.hiketrackbackend.hiketrackbackend.security.JwtUtil;
//import com.hiketrackbackend.hiketrackbackend.security.token.UserTokenService;
//import com.hiketrackbackend.hiketrackbackend.service.ReviewService;
//import jakarta.servlet.http.HttpServletRequest;
//import lombok.SneakyThrows;
//import org.junit.jupiter.api.BeforeAll;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mockito;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.core.io.ClassPathResource;
//import org.springframework.data.domain.Pageable;
//import org.springframework.http.MediaType;
//import org.springframework.jdbc.datasource.init.ScriptUtils;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.test.context.support.WithMockUser;
//import org.springframework.test.context.jdbc.Sql;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.setup.MockMvcBuilders;
//import org.springframework.web.context.WebApplicationContext;
//import org.testcontainers.shaded.org.checkerframework.checker.units.qual.A;
//
//import javax.sql.DataSource;
//import java.sql.Connection;
//import java.sql.SQLException;
//import java.time.LocalDateTime;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.eq;
//import static org.mockito.Mockito.*;
//import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
//import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//public class ReviewControllerTest {
//    private static MockMvc mockMvc;
//
////    @MockBean
////    private static ReviewService reviewService;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    @BeforeAll
//    static void beforeAll(@Autowired WebApplicationContext applicationContext) {
//        mockMvc = MockMvcBuilders.webAppContextSetup(applicationContext)
//                .apply(springSecurity())
//                .build();
//
//    }
//
//    @BeforeEach
//    void setUp(@Autowired DataSource dataSource) throws SQLException {
//        try (Connection connection = dataSource.getConnection()) {
//            connection.setAutoCommit(true);
//            ScriptUtils.executeSqlScript(
//                    connection,
//                    new ClassPathResource("database/review/drop-review-table.sql")
//            );
//        }
//    }
//
//
////    static void tearDown(DataSource dataSource) throws SQLException {
////
////        try (Connection connection = dataSource.getConnection()) {
////            connection.setAutoCommit(true);
////            ScriptUtils.executeSqlScript(
////                    connection,
////                    new ClassPathResource("database/book/delete-books.sql")
////            );
////        }
////    }
//
//    @Test
//    @DisplayName("Create review with invalid request")
//    @WithMockUser(username = "user", roles = "USER")
//    public void testCreateReviewWhenInvalidRequestThenReturnBadRequest() throws Exception {
//        ReviewRequestDto requestDto = new ReviewRequestDto();
//        requestDto.setContent("");
//
//        mockMvc.perform(post("/reviews/1")
//                        .content(objectMapper.writeValueAsString(requestDto))
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .with(csrf()))
//                .andExpect(status().isBadRequest());
//    }
//
//    @Test
//    @DisplayName("Create review without authentication")
//    public void testCreateReviewWhenNotAuthenticatedThenReturnForbidden() throws Exception {
//        ReviewRequestDto requestDto = new ReviewRequestDto();
//        requestDto.setContent("Great tour!");
//
//        mockMvc.perform(post("/reviews/1")
//                        .content(new ObjectMapper().writeValueAsString(requestDto))
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isForbidden());
//    }
//
//    @Test
//    @DisplayName("Successfully create a review when valid data is provided")
//    public void testCreateReviewWithValidData() {
//        ReviewService reviewService = mock(ReviewService.class);
//        ReviewRequestDto requestDto = new ReviewRequestDto();
//        requestDto.setContent("Great tour!");
//        User user = new User();
//        user.setId(1L);
//        Long tourId = 1L;
//
//        ReviewsRespondDto expectedResponse = new ReviewsRespondDto();
//        expectedResponse.setId(1L);
//        expectedResponse.setUserId(user.getId());
//        expectedResponse.setContent(requestDto.getContent());
//        expectedResponse.setTourId(tourId);
//
//        when(reviewService.createReview(requestDto, user, tourId)).thenReturn(expectedResponse);
//
//        ReviewController reviewController = new ReviewController(reviewService);
//        ReviewsRespondDto actualResponse = reviewController.createReview(requestDto, tourId, user);
//
//        assertEquals(expectedResponse.getId(), actualResponse.getId());
//        assertEquals(expectedResponse.getUserId(), actualResponse.getUserId());
//        assertEquals(expectedResponse.getContent(), actualResponse.getContent());
//        assertEquals(expectedResponse.getTourId(), actualResponse.getTourId());
//    }
//
//    @Test
//    @DisplayName("Update review with valid request")
//    @WithMockUser(username = "user1")
//    public void testUpdateReviewWhenValidRequestThenReturnUpdatedReview() throws Exception {
//        ReviewRequestDto requestDto = new ReviewRequestDto();
//        requestDto.setContent("Updated review content");
//
//        ReviewsRespondDto expectedResponse = new ReviewsRespondDto();
//        expectedResponse.setId(1L);
//        expectedResponse.setUserId(1L);
//        expectedResponse.setContent("Updated review content");
//        expectedResponse.setTourId(1L);
//        expectedResponse.setDateCreated(LocalDateTime.now());
//
////        when(reviewService.updateReview(any(ReviewRequestDto.class), eq(1L))).thenReturn(expectedResponse);
//
//        mockMvc.perform(patch("/reviews/1")
//                        .content(new ObjectMapper().writeValueAsString(requestDto))
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .with(csrf()))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.id").value(expectedResponse.getId()))
//                .andExpect(jsonPath("$.userId").value(expectedResponse.getUserId()))
//                .andExpect(jsonPath("$.content").value(expectedResponse.getContent()))
//                .andExpect(jsonPath("$.tourId").value(expectedResponse.getTourId()));
//    }
//
//    @Test
//    @DisplayName("Update review with invalid request")
//    @WithMockUser(username = "user1")
//    public void testUpdateReviewWhenInvalidRequestThenThrowException() throws Exception {
//        ReviewRequestDto requestDto = new ReviewRequestDto();
//        requestDto.setContent("");
//
//        mockMvc.perform(patch("/reviews/1")
//                        .content(new ObjectMapper().writeValueAsString(requestDto))
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .with(csrf()))
//                .andExpect(status().isBadRequest());
//    }
//
//    @Test
//    @DisplayName("Update review without authentication")
//    public void testUpdateReviewWhenNotAuthenticatedThenThrowException() throws Exception {
//        ReviewRequestDto requestDto = new ReviewRequestDto();
//        requestDto.setContent("Updated review content");
//
//        mockMvc.perform(patch("/reviews/1")
//                        .content(new ObjectMapper().writeValueAsString(requestDto))
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isForbidden());
//    }
//
//    @Test
//    @DisplayName("Get all reviews by user when user is authorized and request is valid")
//    @WithMockUser(username = "user1")
//    public void testGetAllReviewsByUserWhenUserIsAuthorizedAndRequestIsValidThenReturnReviews() throws Exception {
//        Long userId = 1L;
//        ReviewsRespondDto review1 = new ReviewsRespondDto();
//        review1.setId(1L);
//        review1.setUserId(userId);
//        review1.setContent("Review 1");
//        review1.setTourId(1L);
//        review1.setDateCreated(LocalDateTime.now());
//
//        ReviewsRespondDto review2 = new ReviewsRespondDto();
//        review2.setId(2L);
//        review2.setUserId(userId);
//        review2.setContent("Review 2");
//        review2.setTourId(2L);
//        review2.setDateCreated(LocalDateTime.now());
//
//        List<ReviewsRespondDto> reviews = Arrays.asList(review1, review2);
//
////        when(reviewService.getAllByUserId(eq(userId), any(Pageable.class))).thenReturn(reviews);
//
//        mockMvc.perform(get("/reviews/user/1")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .with(csrf()))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$[0].id").value(review1.getId()))
//                .andExpect(jsonPath("$[0].userId").value(review1.getUserId()))
//                .andExpect(jsonPath("$[0].content").value(review1.getContent()))
//                .andExpect(jsonPath("$[0].tourId").value(review1.getTourId()))
//                .andExpect(jsonPath("$[1].id").value(review2.getId()))
//                .andExpect(jsonPath("$[1].userId").value(review2.getUserId()))
//                .andExpect(jsonPath("$[1].content").value(review2.getContent()))
//                .andExpect(jsonPath("$[1].tourId").value(review2.getTourId()));
//    }
//
//    @Test
//    @DisplayName("Get all reviews by user when user is not authorized")
//    public void testGetAllReviewsByUserWhenUserIsNotAuthorizedThenReturnRedirection() throws Exception {
//        mockMvc.perform(get("/reviews/user/1")
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().is3xxRedirection());
//    }
//
//    @Test
//    @DisplayName("Get all reviews by user when userId is invalid")
//    @WithMockUser(username = "user1")
//    public void testGetAllReviewsByUserWhenUserIdIsInvalidThenReturnBadRequest() throws Exception {
//        mockMvc.perform(get("/reviews/user/invalid")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .with(csrf()))
//                .andExpect(status().isBadRequest());
//    }
//
//    @Test
//    @DisplayName("Get all review by tour when called with valid parameters")
//    @Sql(scripts = "classpath:database/review/add-reviews.sql",
//            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
//    public void testGetAllReviewsByTourWhenCalledWithValidParametersThenReturnListOfReviewsRespondDto() throws Exception {
//        Long tourId = 1L;
//
//        mockMvc.perform(get("/reviews/tour/{tourId}", tourId)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .with(csrf()))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$[0].id").value(1))
//                .andExpect(jsonPath("$[0].userId").value(1))
//                .andExpect(jsonPath("$[0].content").value("test"))
//                .andExpect(jsonPath("$[0].tourId").value(1))
//                .andExpect(jsonPath("$[1].id").value(2))
//                .andExpect(jsonPath("$[1].userId").value(2))
//                .andExpect(jsonPath("$[1].content").value("test1"))
//                .andExpect(jsonPath("$[1].tourId").value(1));
//    }
//
//    @Test
//    @DisplayName("Get all review by tour when called with an invalid tourId")
//    @WithMockUser(username = "user", roles = "USER")
//    public void testGetAllReviewsByTourWhenCalledWithInvalidTourIdThenReturnBadRequest() throws Exception {
//        Long tourId = -1L;
//
//        mockMvc.perform(get("/reviews/tour/{id}", tourId)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .with(csrf()))
//                .andExpect(status().isBadRequest());
//    }
//
//    @Test
//    @DisplayName("Delete review with valid review ID")
//    @WithMockUser(username = "user", roles = "USER")
//    @Sql(scripts = "classpath:database/review/add-reviews.sql",
//            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
//    public void testDeleteReviewWhenReviewIdIsValidThenReviewIsDeleted() throws Exception {
//        Long reviewId = 100L;
//
//        mockMvc.perform(delete("/reviews/{id}", reviewId)
//                        .with(csrf()))
//                .andExpect(status().isOk());
//
//    }
//
//    @Test
//    @DisplayName("Delete review with invalid review ID")
//    @WithMockUser(username = "user", roles = "USER")
//    public void testDeleteReviewWhenReviewIdIsInvalidThenNotFound() throws Exception {
//        Long reviewId = 999L;
//
//        mockMvc.perform(delete("/reviews/{id}", reviewId)
//                        .with(csrf()))
//                .andExpect(status().isNotFound());
//    }
//}
