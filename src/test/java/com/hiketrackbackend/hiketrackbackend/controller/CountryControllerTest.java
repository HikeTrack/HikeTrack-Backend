package com.hiketrackbackend.hiketrackbackend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hiketrackbackend.hiketrackbackend.dto.country.*;
import com.hiketrackbackend.hiketrackbackend.exception.EntityNotFoundException;
import com.hiketrackbackend.hiketrackbackend.model.country.Continent;
import com.hiketrackbackend.hiketrackbackend.security.JwtUtil;
import com.hiketrackbackend.hiketrackbackend.security.token.UserTokenService;
import com.hiketrackbackend.hiketrackbackend.service.CountryService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.multipart.MultipartFile;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

@WebMvcTest(CountryController.class)
public class CountryControllerTest {

    protected static MockMvc mockMvc;

    @MockBean
    private CountryService countryService;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private UserDetailsService userDetailsService;

    @MockBean
    private UserTokenService<HttpServletRequest> userTokenService;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    static void beforeAll(@Autowired WebApplicationContext applicationContext){
        mockMvc = MockMvcBuilders.webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
    }

    @DisplayName("Successfully create a country with valid data and image file")
    @Test
    public void test_create_country_with_valid_data_and_image() {
        countryService = mock(CountryService.class);
        CountryController countryController = new CountryController(countryService, objectMapper);

        String dataString = "{\"name\":\"TestCountry\",\"continent\":\"EUROPE\"}";
        MultipartFile file = mock(MultipartFile.class);
        CountryRequestDto requestDto = new CountryRequestDto();
        requestDto.setName("TestCountry");
        requestDto.setContinent(Continent.EUROPE);
        CountryRespondDto expectedResponse = new CountryRespondDto();

        when(countryService.createCountry(any(CountryRequestDto.class), any(MultipartFile.class)))
                .thenReturn(expectedResponse);

        CountryRespondDto response = countryController.createCountry(dataString, file);

        assertNotNull(response);
        // Verify that the service is called with the correct requestDto
        verify(countryService).createCountry(argThat(countryRequestDto ->
                "TestCountry".equals(countryRequestDto.getName()) &&
                        Continent.EUROPE.equals(countryRequestDto.getContinent())
        ), eq(file));
    }


    @Test
    @DisplayName("Handle invalid JSON format in dataString gracefully")
    public void test_create_country_with_invalid_json_format() {
        countryService = mock(CountryService.class);
        CountryController countryController = new CountryController(countryService, objectMapper);
        String invalidDataString = "invalid json";
        MultipartFile file = mock(MultipartFile.class);

        assertThrows(IllegalArgumentException.class, () -> {
            countryController.createCountry(invalidDataString, file);
        });
    }

    @Test
    @DisplayName("Handle valid image file uploads correctly")
    public void handle_create_valid_image_file_uploads_correctly() {
        countryService = mock(CountryService.class);
        CountryController countryController = new CountryController(countryService, objectMapper);
        String dataString = "{\"name\":\"TestCountry\",\"continent\":\"EUROPE\"}";
        MultipartFile file = mock(MultipartFile.class);
        CountryRespondDto expectedResponse = new CountryRespondDto();

        when(countryService.createCountry(any(CountryRequestDto.class), any(MultipartFile.class)))
                .thenReturn(expectedResponse);

        CountryRespondDto response = countryController.createCountry(dataString, file);

        assertNotNull(response);
        verify(countryService).createCountry(argThat(countryRequestDto ->
                "TestCountry".equals(countryRequestDto.getName()) &&
                        Continent.EUROPE.equals(countryRequestDto.getContinent())
        ), eq(file));
    }

    @Test
    @DisplayName("Handle missing or null dataString parameter")
    public void test_create_handle_missing_or_null_dataString_parameter() {
        countryService = mock(CountryService.class);
        CountryController countryController = new CountryController(countryService, objectMapper);
        MultipartFile file = mock(MultipartFile.class);

        assertThrows(IllegalArgumentException.class, () -> {
            countryController.createCountry(null, file);
        });
    }

    @Test
    @DisplayName("Update country with valid data")
    public void test_update_country_with_valid_data_and_image() {
        countryService = mock(CountryService.class);
        CountryController countryController = new CountryController(countryService, objectMapper);
        Long id = 1L;
        String dataString = "{\"name\":\"CountryName\",\"continent\":\"EUROPE\"}";
        MultipartFile file = mock(MultipartFile.class);
        CountryRequestDto requestDto = new CountryRequestDto();
        requestDto.setName("CountryName");
        requestDto.setContinent(Continent.valueOf("EUROPE"));
        CountryRespondDto expectedResponse = new CountryRespondDto();

        when(countryService.updateCountry(any(CountryRequestDto.class), eq(file), eq(id)))
                .thenReturn(expectedResponse);

        CountryRespondDto actualResponse = countryController.updateCountry(id, dataString, file);

        assertEquals(expectedResponse, actualResponse);

        verify(countryService).updateCountry(argThat(dto ->
                        "CountryName".equals(dto.getName()) &&
                                Continent.valueOf("EUROPE").equals(dto.getContinent())),
                eq(file),
                eq(id)
        );
    }

    @Test
    @DisplayName("Handle invalid JSON data string gracefully")
    public void test_update_country_with_invalid_json_data() {
        countryService = mock(CountryService.class);
        CountryController countryController = new CountryController(countryService, objectMapper);
        Long id = 1L;
        String invalidDataString = "invalid json";
        MultipartFile file = mock(MultipartFile.class);

        assertThrows(IllegalArgumentException.class, () -> {
            countryController.updateCountry(id, invalidDataString, file);
        });
    }

    @Test
    @DisplayName("Handle non-existent country ID gracefully")
    public void test_handle_update_non_existent_country_id_gracefully() {
        countryService = mock(CountryService.class);
        CountryController countryController = new CountryController(countryService, objectMapper);
        Long id = 999L;
        String dataString = "{\"name\":\"CountryName\",\"continent\":\"EUROPE\"}";
        MultipartFile file = mock(MultipartFile.class);
        CountryRequestDto requestDto = new CountryRequestDto();
        requestDto.setName("CountryName");
        requestDto.setContinent(Continent.valueOf("EUROPE"));

        when(countryService.updateCountry(any(CountryRequestDto.class), eq(file), eq(id)))
                .thenThrow(new IllegalArgumentException("Invalid data format: Cannot find country with ID " + id));

        assertThrows(IllegalArgumentException.class, () -> countryController.updateCountry(id, dataString, file));
    }

    @Test
    @DisplayName("Handle valid JSON data string conversion to CountryRequestDto")
    public void test_handle_update_valid_json_data_conversion() {
        countryService = mock(CountryService.class);
        CountryController countryController = new CountryController(countryService, objectMapper);
        Long id = 1L;
        String dataString = "{\"name\":\"CountryName\",\"continent\":\"EUROPE\"}";
        MultipartFile file = mock(MultipartFile.class);
        CountryRequestDto requestDto = new CountryRequestDto();
        requestDto.setName("CountryName");
        requestDto.setContinent(Continent.valueOf("EUROPE"));
        CountryRespondDto expectedResponse = new CountryRespondDto();

        when(countryService.updateCountry(any(CountryRequestDto.class), eq(file), eq(id)))
                .thenReturn(expectedResponse);

        CountryRespondDto actualResponse = countryController.updateCountry(id, dataString, file);

        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    @DisplayName("Search returns a list of countries matching valid parameters")
    public void test_search_with_valid_parameters() {
        countryService = mock(CountryService.class);
        CountryController countryController = new CountryController(countryService, objectMapper);
        CountrySearchParameters params = new CountrySearchParameters(new String[]{"Europe"}, new String[]{"France"});
        Pageable pageable = PageRequest.of(0, 10);
        List<CountryRespondDto> expectedCountries = List.of(new CountryRespondDto());
        when(countryService.search(params, pageable)).thenReturn(expectedCountries);

        List<CountryRespondDto> result = countryController.search(params, pageable);

        assertEquals(expectedCountries, result);
    }
    @Test
    @DisplayName("Search with invalid parameters returns an empty list")
    public void test_search_with_invalid_parameters() {
        countryService = mock(CountryService.class);
        CountryController countryController = new CountryController(countryService, objectMapper);
        CountrySearchParameters params = new CountrySearchParameters(new String[]{"InvalidContinent"}, new String[]{"InvalidCountry"});
        Pageable pageable = PageRequest.of(0, 10);
        when(countryService.search(params, pageable)).thenReturn(Collections.emptyList());

        List<CountryRespondDto> result = countryController.search(params, pageable);

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Search handles empty parameters and returns all countries")
    public void test_search_with_empty_parameters() {
        CountryService countryService = mock(CountryService.class);
        CountryController countryController = new CountryController(countryService, objectMapper);
        CountrySearchParameters params = new CountrySearchParameters(new String[]{}, new String[]{});
        Pageable pageable = PageRequest.of(0, 10);
        List<CountryRespondDto> expectedCountries = List.of(new CountryRespondDto());
        when(countryService.search(params, pageable)).thenReturn(expectedCountries);

        List<CountryRespondDto> result = countryController.search(params, pageable);

        assertEquals(expectedCountries, result);
    }

    @Test
    @DisplayName("Retrieve country details successfully with valid ID")
    public void test_get_country_by_valid_id() {
        countryService = mock(CountryService.class);
        CountryController countryController = new CountryController(countryService, objectMapper);
        Long validId = 1L;
        CountryRespondDto expectedCountry = new CountryRespondDto();
        expectedCountry.setId(validId);
        expectedCountry.setName("TestCountry");

        when(countryService.getById(validId)).thenReturn(expectedCountry);

        CountryRespondDto actualCountry = countryController.getById(validId);

        assertNotNull(actualCountry);
        assertEquals(expectedCountry.getId(), actualCountry.getId());
        assertEquals(expectedCountry.getName(), actualCountry.getName());
    }

    @Test
    @DisplayName("Handle non-existent country ID gracefully")
    public void test_get_country_by_non_existent_id() {
        countryService = mock(CountryService.class);
        CountryController countryController = new CountryController(countryService, objectMapper);
        Long nonExistentId = 999L;

        when(countryService.getById(nonExistentId)).thenReturn(null);

        CountryRespondDto actualCountry = countryController.getById(nonExistentId);

        assertNull(actualCountry);
    }

    @Test
    @DisplayName("Handle negative ID gracefully")
    public void test_get_country_by_negative_id() {
        countryService = mock(CountryService.class);
        CountryController countryController = new CountryController(countryService, objectMapper);
        Long negativeId = -1L;

        when(countryService.getById(negativeId)).thenReturn(null);

        CountryRespondDto actualCountry = countryController.getById(negativeId);

        assertNull(actualCountry);
    }

    @Test
    @DisplayName("Handle situation with server exception")
    public void test_get_country_by_id_service_exception() {
        countryService = mock(CountryService.class);
        CountryController countryController = new CountryController(countryService, objectMapper);
        Long invalidId = -1L;

        when(countryService.getById(invalidId)).thenThrow(new IllegalArgumentException("Invalid ID"));

        assertThrows(IllegalArgumentException.class, () -> countryController.getById(invalidId));
        assertThrows(IllegalArgumentException.class, () -> {
            countryController.getById(invalidId);
        });
    }

    @Test
    @DisplayName("Handle situation with all valid data")
    public void test_get_all_with_valid_pageable() {
        Pageable pageable = PageRequest.of(0, 10);
        CountryRespondDto country1 = new CountryRespondDto();
        country1.setId(1L);
        country1.setName("Country1");

        CountryRespondDto country2 = new CountryRespondDto();
        country2.setId(2L);
        country2.setName("Country2");

        List<CountryRespondDto> expectedCountries = List.of(country1, country2);

        countryService = Mockito.mock(CountryService.class);
        Mockito.when(countryService.getAll(pageable)).thenReturn(expectedCountries);

        CountryController countryController = new CountryController(countryService, objectMapper);

        List<CountryRespondDto> actualCountries = countryController.getAll(pageable);

        Assertions.assertNotNull(actualCountries); // Ensure the result is not null
        Assertions.assertEquals(expectedCountries.size(), actualCountries.size()); // Verify size
        Assertions.assertEquals(expectedCountries, actualCountries); // Verify content
    }

    @Test
    @DisplayName("Handle situation with server exception")
    public void test_get_all_countries_should_throw_exception_when_service_fails() {
        Pageable pageable = PageRequest.of(0, 10);

        countryService = Mockito.mock(CountryService.class);
        Mockito.when(countryService.getAll(pageable)).thenThrow(new RuntimeException("Service Exception"));

        CountryController countryController = new CountryController(countryService, objectMapper);

        RuntimeException exception = Assertions.assertThrows(RuntimeException.class, () -> {
            countryController.getAll(pageable);
        });

        Assertions.assertEquals("Service Exception", exception.getMessage());
    }

    @Test
    @DisplayName("Handle successfully receiving ten random countries")
    public void test_get_ten_random_countries_success() {
        List<CountryRespondWithPhotoDto> expectedCountries = new ArrayList<>();
        expectedCountries.add(new CountryRespondWithPhotoDto(1L, "Country1", Continent.ASIA, "photo1.jpg"));
        expectedCountries.add(new CountryRespondWithPhotoDto(2L, "Country2", Continent.EUROPE, "photo2.jpg"));
        expectedCountries.add(new CountryRespondWithPhotoDto(3L, "Country3", Continent.AFRICA, "photo3.jpg"));
        expectedCountries.add(new CountryRespondWithPhotoDto(4L, "Country4", Continent.NORTH_AMERICA, "photo4.jpg"));
        expectedCountries.add(new CountryRespondWithPhotoDto(5L, "Country5", Continent.SOUTH_AMERICA, "photo5.jpg"));
        expectedCountries.add(new CountryRespondWithPhotoDto(6L, "Country6", Continent.ASIA, "photo6.jpg"));
        expectedCountries.add(new CountryRespondWithPhotoDto(7L, "Country7", Continent.ASIA, "photo7.jpg"));
        expectedCountries.add(new CountryRespondWithPhotoDto(8L, "Country8", Continent.EUROPE, "photo8.jpg"));
        expectedCountries.add(new CountryRespondWithPhotoDto(9L, "Country9", Continent.AFRICA, "photo9.jpg"));
        expectedCountries.add(new CountryRespondWithPhotoDto(10L, "Country10", Continent.NORTH_AMERICA, "photo10.jpg"));

        when(countryService.getTenRandomCountries()).thenReturn(expectedCountries);

        CountryController countryController = new CountryController(countryService, objectMapper);
        List<CountryRespondWithPhotoDto> actualCountries = countryController.getTenRandomCountries();

        assertNotNull(actualCountries);
        assertEquals(10, actualCountries.size());
        assertEquals(expectedCountries, actualCountries);

        verify(countryService, times(1)).getTenRandomCountries();
    }

    @Test
    @DisplayName("Successfully delete country by name")
    public void test_delete_country_by_name_success() {
        countryService = Mockito.mock(CountryService.class);
        CountryController countryController = new CountryController(countryService, objectMapper);
        CountryDeleteRequestDto requestDto = new CountryDeleteRequestDto();
        requestDto.setName("TestCountry");

        countryController.deleteByCountryName(requestDto);

        Mockito.verify(countryService).deleteByName(requestDto);
    }

    @Test
    @DisplayName("Handle situation when country not found")
    public void test_delete_country_by_name_not_found() {
        countryService = Mockito.mock(CountryService.class);
        CountryController countryController = new CountryController(countryService, objectMapper);
        CountryDeleteRequestDto requestDto = new CountryDeleteRequestDto();
        requestDto.setName("NonExistentCountry");

        Mockito.doThrow(new EntityNotFoundException("Country not found")).when(countryService).deleteByName(requestDto);

        assertThrows(EntityNotFoundException.class, () -> {
            countryController.deleteByCountryName(requestDto);
        });
    }
}
