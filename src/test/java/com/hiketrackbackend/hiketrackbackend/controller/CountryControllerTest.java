package com.hiketrackbackend.hiketrackbackend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hiketrackbackend.hiketrackbackend.dto.country.CountryDeleteRequestDto;
import com.hiketrackbackend.hiketrackbackend.dto.country.CountryRequestDto;
import com.hiketrackbackend.hiketrackbackend.dto.country.CountryRespondDto;
import com.hiketrackbackend.hiketrackbackend.dto.country.CountryRespondWithPhotoDto;
import com.hiketrackbackend.hiketrackbackend.dto.country.CountrySearchParameters;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.multipart.MultipartFile;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

@WebMvcTest(CountryController.class)
public class CountryControllerTest {
    protected static MockMvc mockMvc;

    @MockBean
    private CountryService countryService;

    @MockBean
    private static JwtUtil jwtUtil;

    @MockBean
    private static UserDetailsService userDetailsService;

    @MockBean
    private static UserTokenService<HttpServletRequest> userTokenService;

    @Autowired
    private ObjectMapper objectMapper;

    private static List<CountryRespondWithPhotoDto> expectedCountries;

    @BeforeAll
    static void beforeAll(@Autowired WebApplicationContext applicationContext) {
        mockMvc = MockMvcBuilders.webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
        expectedCountries = setExpectedCountries();
    }

    @DisplayName("Successfully create a country with valid data and image file")
    @Test
    public void testCreateCountryWithValidDataAndImage() {
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
    @DisplayName("Handle invalid JSON format in dataString gracefully")
    public void testCreateCountryWithInvalidJsonFormat() {
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
    public void handleCreateValidImageFileUploadsCorrectly() {
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
    public void testCreateHandleMissingOrNullDataStringParameter() {
        countryService = mock(CountryService.class);
        CountryController countryController = new CountryController(countryService, objectMapper);
        MultipartFile file = mock(MultipartFile.class);

        assertThrows(IllegalArgumentException.class, () -> {
            countryController.createCountry(null, file);
        });
    }

    @Test
    @DisplayName("Update country with valid data")
    public void testUpdateCountryWithValidDataAndImage() {
        countryService = mock(CountryService.class);
        CountryController countryController = new CountryController(countryService, objectMapper);
        Long id = 1L;
        String dataString = "{\"name\":\"CountryName\",\"continent\":\"EUROPE\"}";
        MultipartFile file = mock(MultipartFile.class);
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
    public void testUpdateCountryWithInvalidJsonData() {
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
    public void testHandleUpdateNonExistentCountryIdGracefully() {
        countryService = mock(CountryService.class);
        CountryController countryController = new CountryController(countryService, objectMapper);
        Long id = 999L;
        String dataString = "{\"name\":\"CountryName\",\"continent\":\"EUROPE\"}";
        MultipartFile file = mock(MultipartFile.class);

        when(countryService.updateCountry(any(CountryRequestDto.class), eq(file), eq(id)))
                .thenThrow(new IllegalArgumentException("Invalid data format: Cannot find country with ID " + id));

        assertThrows(IllegalArgumentException.class, () -> countryController.updateCountry(id, dataString, file));
    }

    @Test
    @DisplayName("Handle valid JSON data string conversion to CountryRequestDto")
    public void testHandleUpdateValidJsonDataConversion() {
        countryService = mock(CountryService.class);
        CountryController countryController = new CountryController(countryService, objectMapper);
        Long id = 1L;
        String dataString = "{\"name\":\"CountryName\",\"continent\":\"EUROPE\"}";
        MultipartFile file = mock(MultipartFile.class);
        CountryRespondDto expectedResponse = new CountryRespondDto();

        when(countryService.updateCountry(any(CountryRequestDto.class), eq(file), eq(id)))
                .thenReturn(expectedResponse);

        CountryRespondDto actualResponse = countryController.updateCountry(id, dataString, file);

        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    @DisplayName("Search returns a list of countries matching valid parameters")
    public void testSearchWithValidParameters() {
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
    public void testSearchWithInvalidParameters() {
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
    public void testSearchWithEmptyParameters() {
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
    public void testGetCountryByValidId() {
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
    public void testGetCountryByNonExistentId() {
        countryService = mock(CountryService.class);
        CountryController countryController = new CountryController(countryService, objectMapper);
        Long nonExistentId = 999L;

        when(countryService.getById(nonExistentId)).thenReturn(null);

        CountryRespondDto actualCountry = countryController.getById(nonExistentId);

        assertNull(actualCountry);
    }

    @Test
    @DisplayName("Handle negative ID gracefully")
    public void testGetCountryByNegativeId() {
        countryService = mock(CountryService.class);
        CountryController countryController = new CountryController(countryService, objectMapper);
        Long negativeId = -1L;

        when(countryService.getById(negativeId)).thenReturn(null);

        CountryRespondDto actualCountry = countryController.getById(negativeId);

        assertNull(actualCountry);
    }

    @Test
    @DisplayName("Handle situation with server exception")
    public void testGetCountryByIdServiceException() {
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
    public void testGetAllWithValidPageable() {
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
    public void testGetAllCountriesShouldThrowExceptionWhenServiceFails() {
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
    public void testGetTenRandomCountriesSuccess() {
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
    public void testDeleteCountryByNameSuccess() {
        countryService = Mockito.mock(CountryService.class);
        CountryController countryController = new CountryController(countryService, objectMapper);
        CountryDeleteRequestDto requestDto = new CountryDeleteRequestDto();
        requestDto.setName("TestCountry");

        countryController.deleteByCountryName(requestDto);

        Mockito.verify(countryService).deleteByName(requestDto);
    }

    @Test
    @DisplayName("Handle situation when country not found")
    public void testDeleteCountryByNameNotFound() {
        countryService = Mockito.mock(CountryService.class);
        CountryController countryController = new CountryController(countryService, objectMapper);
        CountryDeleteRequestDto requestDto = new CountryDeleteRequestDto();
        requestDto.setName("NonExistentCountry");

        Mockito.doThrow(new EntityNotFoundException("Country not found")).when(countryService).deleteByName(requestDto);

        assertThrows(EntityNotFoundException.class, () -> {
            countryController.deleteByCountryName(requestDto);
        });
    }

    private static List<CountryRespondWithPhotoDto> setExpectedCountries() {
        List<CountryRespondWithPhotoDto> expectedCountries = new ArrayList<>();

        CountryRespondWithPhotoDto countryRespondWithPhotoDto = new CountryRespondWithPhotoDto();
        countryRespondWithPhotoDto.setId(1L);
        countryRespondWithPhotoDto.setPhotoUrl("photo1.jpg");
        countryRespondWithPhotoDto.setContinent(Continent.ASIA);
        expectedCountries.add(countryRespondWithPhotoDto);

        CountryRespondWithPhotoDto countryRespondWithPhotoDto2 = new CountryRespondWithPhotoDto();
        countryRespondWithPhotoDto.setId(2L);
        countryRespondWithPhotoDto.setPhotoUrl("photo2.jpg");
        countryRespondWithPhotoDto.setContinent(Continent.EUROPE);
        expectedCountries.add(countryRespondWithPhotoDto2);

        CountryRespondWithPhotoDto countryRespondWithPhotoDto3 = new CountryRespondWithPhotoDto();
        countryRespondWithPhotoDto.setId(3L);
        countryRespondWithPhotoDto.setPhotoUrl("photo3.jpg");
        countryRespondWithPhotoDto.setContinent(Continent.AFRICA);
        expectedCountries.add(countryRespondWithPhotoDto3);

        CountryRespondWithPhotoDto countryRespondWithPhotoDto4 = new CountryRespondWithPhotoDto();
        countryRespondWithPhotoDto.setId(4L);
        countryRespondWithPhotoDto.setPhotoUrl("photo4.jpg");
        countryRespondWithPhotoDto.setContinent(Continent.AUSTRALIA);
        expectedCountries.add(countryRespondWithPhotoDto4);

        CountryRespondWithPhotoDto countryRespondWithPhotoDto5 = new CountryRespondWithPhotoDto();
        countryRespondWithPhotoDto.setId(5L);
        countryRespondWithPhotoDto.setPhotoUrl("photo5.jpg");
        countryRespondWithPhotoDto.setContinent(Continent.ASIA);
        expectedCountries.add(countryRespondWithPhotoDto5);

        CountryRespondWithPhotoDto countryRespondWithPhotoDto6 = new CountryRespondWithPhotoDto();
        countryRespondWithPhotoDto.setId(6L);
        countryRespondWithPhotoDto.setPhotoUrl("photo6.jpg");
        countryRespondWithPhotoDto.setContinent(Continent.NORTH_AMERICA);
        expectedCountries.add(countryRespondWithPhotoDto6);

        CountryRespondWithPhotoDto countryRespondWithPhotoDto7 = new CountryRespondWithPhotoDto();
        countryRespondWithPhotoDto.setId(7L);
        countryRespondWithPhotoDto.setPhotoUrl("photo7.jpg");
        countryRespondWithPhotoDto.setContinent(Continent.ASIA);
        expectedCountries.add(countryRespondWithPhotoDto7);

        CountryRespondWithPhotoDto countryRespondWithPhotoDto8 = new CountryRespondWithPhotoDto();
        countryRespondWithPhotoDto.setId(8L);
        countryRespondWithPhotoDto.setPhotoUrl("photo8.jpg");
        countryRespondWithPhotoDto.setContinent(Continent.SOUTH_AMERICA);
        expectedCountries.add(countryRespondWithPhotoDto8);

        CountryRespondWithPhotoDto countryRespondWithPhotoDto9 = new CountryRespondWithPhotoDto();
        countryRespondWithPhotoDto.setId(9L);
        countryRespondWithPhotoDto.setPhotoUrl("photo9.jpg");
        countryRespondWithPhotoDto.setContinent(Continent.SOUTH_AMERICA);
        expectedCountries.add(countryRespondWithPhotoDto9);

        CountryRespondWithPhotoDto countryRespondWithPhotoDto10 = new CountryRespondWithPhotoDto();
        countryRespondWithPhotoDto.setId(10L);
        countryRespondWithPhotoDto.setPhotoUrl("photo10.jpg");
        countryRespondWithPhotoDto.setContinent(Continent.EUROPE);
        expectedCountries.add(countryRespondWithPhotoDto10);

        return  expectedCountries;
    }
}
