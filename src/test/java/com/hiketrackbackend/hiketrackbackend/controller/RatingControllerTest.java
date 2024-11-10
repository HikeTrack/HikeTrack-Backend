//package com.hiketrackbackend.hiketrackbackend.controller;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.hiketrackbackend.hiketrackbackend.dto.rating.RatingRequestDto;
//import com.hiketrackbackend.hiketrackbackend.dto.rating.RatingRespondDto;
//import com.hiketrackbackend.hiketrackbackend.security.JwtUtil;
//import com.hiketrackbackend.hiketrackbackend.security.token.UserTokenService;
//import com.hiketrackbackend.hiketrackbackend.service.RatingService;
//import jakarta.servlet.http.HttpServletRequest;
//import org.junit.jupiter.api.BeforeAll;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.Incubating;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.Mockito;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.http.MediaType;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.test.context.support.WithMockUser;
//import org.springframework.test.context.jdbc.Sql;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
//import org.springframework.test.web.servlet.setup.MockMvcBuilders;
//import org.springframework.web.context.WebApplicationContext;
//
//import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
//import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//public class RatingControllerTest {
////    @InjectMocks
////    private RatingController ratingController;
//    protected MockMvc mockMvc;
//    @MockBean
//    private RatingService ratingService;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//
////    @Mock
////    private static RatingService ratingService;
//
//    @BeforeEach
//    public void setUp(@Autowired WebApplicationContext applicationContext) {
//        mockMvc = MockMvcBuilders.webAppContextSetup(applicationContext)
//                .apply(springSecurity())
//                .build();
//
//
//
//
//    }
//
//    @Test
//    @WithMockUser(username = "user", roles = "USER")
//    @DisplayName("Test successful rating update returns 200")
//    public void testUpdateRatingWhenRequestIsSuccessfulThenReturn200() throws Exception {
//        RatingRequestDto requestDto = new RatingRequestDto();
//        Integer i = 4;
//        requestDto.setRating(i);
//        mockMvc.perform(patch("/ratings/1/1")
//                        .content(objectMapper.writeValueAsString(requestDto))
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .with(csrf()))
//                .andExpect(status().isOk());
//    }
//
//    @Test
//    @WithMockUser(username = "user2")
//    public void testUpdateRatingWhenRequestBodyIsInvalidThenReturn400() throws Exception {
//        RatingRequestDto requestDto = new RatingRequestDto();
//        requestDto.setRating(-1);
//
//        mockMvc.perform(patch("/rating/1/1")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(requestDto.toString())
//                        .with(csrf()))
//                .andExpect(status().isBadRequest());
//    }
//
//    @Test
//    @WithMockUser(username = "user2")
//    @DisplayName("Test unauthorized user returns 403")
//    public void testUpdateRatingWhenUserIsNotAuthorizedThenReturn403() throws Exception {
//        RatingRequestDto requestDto = new RatingRequestDto();
//        requestDto.setRating(1);
//        mockMvc.perform(patch("/rating/1/1")
//                        .content(new ObjectMapper().writeValueAsString(requestDto))
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isForbidden());
//    }
//}
