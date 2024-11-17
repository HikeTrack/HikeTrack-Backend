package com.hiketrackbackend.hiketrackbackend.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hiketrackbackend.hiketrackbackend.dto.country.CountryDeleteRequestDto;
import com.hiketrackbackend.hiketrackbackend.dto.country.CountryRequestDto;
import com.hiketrackbackend.hiketrackbackend.dto.country.CountryRespondDto;
import com.hiketrackbackend.hiketrackbackend.dto.country.CountryRespondWithPhotoDto;
import com.hiketrackbackend.hiketrackbackend.dto.country.CountrySearchParameters;
import com.hiketrackbackend.hiketrackbackend.exception.EntityNotFoundException;
import com.hiketrackbackend.hiketrackbackend.model.country.Continent;
import com.hiketrackbackend.hiketrackbackend.service.CountryService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

@ExtendWith(MockitoExtension.class)
public class CountryControllerTest {
    @Mock
    private static CountryService countryService;

    @Mock
    private static ObjectMapper objectMapper;

    @Mock
    private static MultipartFile file;

    @InjectMocks
    private CountryController countryController;
    private static List<CountryRespondWithPhotoDto> expectedCountries;

    @BeforeAll
    static void beforeAll() {
        expectedCountries = setExpectedCountries();
    }

    @DisplayName("Successfully create a country with valid data and image file")
    @Test
    public void testCreateCountryWithValidDataAndImage() throws Exception {
        String dataString = "{\"name\":\"TestCountry\",\"continent\":\"EUROPE\"}";

        CountryRespondDto expectedResponse = new CountryRespondDto();
        CountryRequestDto countryRequestDto = new CountryRequestDto();
        countryRequestDto.setName("TestCountry");
        countryRequestDto.setContinent(Continent.EUROPE);

        when(objectMapper.readValue(dataString, CountryRequestDto.class)).thenReturn(countryRequestDto);
        when(countryService.createCountry(any(CountryRequestDto.class), any(MultipartFile.class)))
                .thenReturn(expectedResponse);

        CountryRespondDto response = countryController.createCountry(dataString, file);

        assertNotNull(response);
        verify(countryService).createCountry(argThat(dto ->
                "TestCountry".equals(dto.getName()) &&
                        Continent.EUROPE.equals(dto.getContinent())
        ), eq(file));
    }

    @Test
    @DisplayName("Handle invalid JSON format in dataString gracefully")
    public void testCreateCountryWithInvalidJsonFormat() throws JsonProcessingException {
        String invalidDataString = "invalid json";

        when(objectMapper.readValue(invalidDataString, CountryRequestDto.class))
                .thenThrow(new JsonProcessingException("Invalid JSON") {});

        assertThrows(IllegalArgumentException.class, () -> {
            countryController.createCountry(invalidDataString, file);
        });
    }

    @Test
    @DisplayName("Handle valid image file uploads correctly")
    public void handleCreateValidImageFileUploadsCorrectly() throws Exception {
        String dataString = "{\"name\":\"TestCountry\",\"continent\":\"EUROPE\"}";
        MultipartFile file = mock(MultipartFile.class);
        CountryRespondDto expectedResponse = new CountryRespondDto();

        CountryRequestDto countryRequestDto = new CountryRequestDto();
        countryRequestDto.setName("TestCountry");
        countryRequestDto.setContinent(Continent.EUROPE);

        when(objectMapper.readValue(dataString, CountryRequestDto.class)).thenReturn(countryRequestDto);
        when(countryService.createCountry(any(CountryRequestDto.class), any(MultipartFile.class)))
                .thenReturn(expectedResponse);

        CountryRespondDto response = countryController.createCountry(dataString, file);

        assertNotNull(response);
        verify(countryService).createCountry(argThat(dto ->
                "TestCountry".equals(dto.getName()) &&
                        Continent.EUROPE.equals(dto.getContinent())
        ), eq(file));
    }

    @Test
    @DisplayName("Handle missing or null dataString parameter")
    public void testCreateHandleMissingOrNullDataStringParameter() {
        assertThrows(IllegalArgumentException.class, () -> {
            countryController.createCountry(null, file);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            countryController.createCountry("", file);
        });
    }

    @Test
    @DisplayName("Update country with valid data")
    public void testUpdateCountryWithValidDataAndImage() throws Exception {
        Long id = 1L;
        String dataString = "{\"name\":\"CountryName\",\"continent\":\"EUROPE\"}";
        CountryRespondDto expectedResponse = new CountryRespondDto();

        CountryRequestDto countryRequestDto = new CountryRequestDto();
        countryRequestDto.setName("CountryName");
        countryRequestDto.setContinent(Continent.EUROPE);

        when(objectMapper.readValue(dataString, CountryRequestDto.class)).thenReturn(countryRequestDto);
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
    public void testUpdateCountryWithInvalidJsonData() throws JsonProcessingException {
        Long id = 1L;
        String invalidDataString = "invalid json";

        when(objectMapper.readValue(invalidDataString, CountryRequestDto.class))
                .thenThrow(new JsonProcessingException("Invalid JSON") {});

        assertThrows(IllegalArgumentException.class, () -> {
            countryController.updateCountry(id, invalidDataString, file);
        });
    }

    @Test
    @DisplayName("Handle non-existent country ID gracefully")
    public void testHandleUpdateNonExistentCountryIdGracefully() throws JsonProcessingException {
        Long id = 999L;
        String dataString = "{\"name\":\"CountryName\",\"continent\":\"EUROPE\"}";

        CountryRequestDto countryRequestDto = new CountryRequestDto();
        countryRequestDto.setName("CountryName");
        countryRequestDto.setContinent(Continent.EUROPE);

        when(objectMapper.readValue(dataString, CountryRequestDto.class))
                .thenReturn(countryRequestDto);

        when(countryService.updateCountry(any(CountryRequestDto.class), eq(file), eq(id)))
                .thenThrow(new IllegalArgumentException("Invalid data format: Cannot find country with ID " + id));

        assertThrows(IllegalArgumentException.class, () -> countryController.updateCountry(id, dataString, file));
    }

    @Test
    @DisplayName("Handle valid JSON data string conversion to CountryRequestDto")
    public void testHandleUpdateValidJsonDataConversion() throws Exception {
        Long id = 1L;
        String dataString = "{\"name\":\"CountryName\",\"continent\":\"EUROPE\"}";
        CountryRespondDto expectedResponse = new CountryRespondDto();

        CountryRequestDto countryRequestDto = new CountryRequestDto();
        countryRequestDto.setName("CountryName");
        countryRequestDto.setContinent(Continent.EUROPE);

        when(objectMapper.readValue(dataString, CountryRequestDto.class)).thenReturn(countryRequestDto);
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
    @DisplayName("Search returns a list of countries matching valid parameters")
    public void testSearchWithValidParameters() {
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
        CountrySearchParameters params = new CountrySearchParameters(new String[]{"InvalidContinent"}, new String[]{"InvalidCountry"});
        Pageable pageable = PageRequest.of(0, 10);
        when(countryService.search(params, pageable)).thenReturn(Collections.emptyList());

        List<CountryRespondDto> result = countryController.search(params, pageable);

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Search handles empty parameters and returns all countries")
    public void testSearchWithEmptyParameters() {
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
        Long nonExistentId = 999L;

        when(countryService.getById(nonExistentId)).thenReturn(null);

        CountryRespondDto actualCountry = countryController.getById(nonExistentId);

        assertNull(actualCountry);
    }

    @Test
    @DisplayName("Should handle exception from service gracefully for negative ID")
    public void testGetCountryByNegativeId() {
        Long negativeId = -1L;

        when(countryService.getById(negativeId)).thenThrow(new EntityNotFoundException("Could not find country with id: " + negativeId));

        Exception exception = assertThrows(EntityNotFoundException.class, () -> {
            countryController.getById(negativeId);
        });

        assertEquals("Could not find country with id: " + negativeId, exception.getMessage());

        verify(countryService, times(1)).getById(negativeId);
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

        Mockito.when(countryService.getAll(pageable)).thenReturn(expectedCountries);


        List<CountryRespondDto> actualCountries = countryController.getAll(pageable);

        Assertions.assertNotNull(actualCountries); // Ensure the result is not null
        Assertions.assertEquals(expectedCountries.size(), actualCountries.size()); // Verify size
        Assertions.assertEquals(expectedCountries, actualCountries); // Verify content
    }

    @Test
    @DisplayName("Handle situation with server exception")
    public void testGetAllCountriesShouldThrowExceptionWhenServiceFails() {
        Pageable pageable = PageRequest.of(0, 10);

        Mockito.when(countryService.getAll(pageable)).thenThrow(new RuntimeException("Service Exception"));


        RuntimeException exception = Assertions.assertThrows(RuntimeException.class, () -> {
            countryController.getAll(pageable);
        });

        Assertions.assertEquals("Service Exception", exception.getMessage());
    }

    @Test
    @DisplayName("Handle successfully receiving ten random countries")
    public void testGetTenRandomCountriesSuccess() {
        when(countryService.getTenRandomCountries()).thenReturn(expectedCountries);

        List<CountryRespondWithPhotoDto> actualCountries = countryController.getTenRandomCountries();

        assertNotNull(actualCountries);
        assertEquals(10, actualCountries.size());
        assertEquals(expectedCountries, actualCountries);

        verify(countryService, times(1)).getTenRandomCountries();
    }

    @Test
    @DisplayName("Successfully delete country by name")
    public void testDeleteCountryByNameSuccess() {
        CountryDeleteRequestDto requestDto = new CountryDeleteRequestDto();
        requestDto.setName("TestCountry");

        countryController.deleteByCountryName(requestDto);

        Mockito.verify(countryService).deleteByName(requestDto);
    }

    @Test
    @DisplayName("Handle situation when country not found")
    public void testDeleteCountryByNameNotFound() {
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
        countryRespondWithPhotoDto.setPhoto("photo1.jpg");
        countryRespondWithPhotoDto.setContinent(Continent.ASIA);
        expectedCountries.add(countryRespondWithPhotoDto);

        CountryRespondWithPhotoDto countryRespondWithPhotoDto2 = new CountryRespondWithPhotoDto();
        countryRespondWithPhotoDto.setId(2L);
        countryRespondWithPhotoDto.setPhoto("photo2.jpg");
        countryRespondWithPhotoDto.setContinent(Continent.EUROPE);
        expectedCountries.add(countryRespondWithPhotoDto2);

        CountryRespondWithPhotoDto countryRespondWithPhotoDto3 = new CountryRespondWithPhotoDto();
        countryRespondWithPhotoDto.setId(3L);
        countryRespondWithPhotoDto.setPhoto("photo3.jpg");
        countryRespondWithPhotoDto.setContinent(Continent.AFRICA);
        expectedCountries.add(countryRespondWithPhotoDto3);

        CountryRespondWithPhotoDto countryRespondWithPhotoDto4 = new CountryRespondWithPhotoDto();
        countryRespondWithPhotoDto.setId(4L);
        countryRespondWithPhotoDto.setPhoto("photo4.jpg");
        countryRespondWithPhotoDto.setContinent(Continent.AUSTRALIA);
        expectedCountries.add(countryRespondWithPhotoDto4);

        CountryRespondWithPhotoDto countryRespondWithPhotoDto5 = new CountryRespondWithPhotoDto();
        countryRespondWithPhotoDto.setId(5L);
        countryRespondWithPhotoDto.setPhoto("photo5.jpg");
        countryRespondWithPhotoDto.setContinent(Continent.ASIA);
        expectedCountries.add(countryRespondWithPhotoDto5);

        CountryRespondWithPhotoDto countryRespondWithPhotoDto6 = new CountryRespondWithPhotoDto();
        countryRespondWithPhotoDto.setId(6L);
        countryRespondWithPhotoDto.setPhoto("photo6.jpg");
        countryRespondWithPhotoDto.setContinent(Continent.NORTH_AMERICA);
        expectedCountries.add(countryRespondWithPhotoDto6);

        CountryRespondWithPhotoDto countryRespondWithPhotoDto7 = new CountryRespondWithPhotoDto();
        countryRespondWithPhotoDto.setId(7L);
        countryRespondWithPhotoDto.setPhoto("photo7.jpg");
        countryRespondWithPhotoDto.setContinent(Continent.ASIA);
        expectedCountries.add(countryRespondWithPhotoDto7);

        CountryRespondWithPhotoDto countryRespondWithPhotoDto8 = new CountryRespondWithPhotoDto();
        countryRespondWithPhotoDto.setId(8L);
        countryRespondWithPhotoDto.setPhoto("photo8.jpg");
        countryRespondWithPhotoDto.setContinent(Continent.SOUTH_AMERICA);
        expectedCountries.add(countryRespondWithPhotoDto8);

        CountryRespondWithPhotoDto countryRespondWithPhotoDto9 = new CountryRespondWithPhotoDto();
        countryRespondWithPhotoDto.setId(9L);
        countryRespondWithPhotoDto.setPhoto("photo9.jpg");
        countryRespondWithPhotoDto.setContinent(Continent.SOUTH_AMERICA);
        expectedCountries.add(countryRespondWithPhotoDto9);

        CountryRespondWithPhotoDto countryRespondWithPhotoDto10 = new CountryRespondWithPhotoDto();
        countryRespondWithPhotoDto.setId(10L);
        countryRespondWithPhotoDto.setPhoto("photo10.jpg");
        countryRespondWithPhotoDto.setContinent(Continent.EUROPE);
        expectedCountries.add(countryRespondWithPhotoDto10);

        return  expectedCountries;
    }
}
