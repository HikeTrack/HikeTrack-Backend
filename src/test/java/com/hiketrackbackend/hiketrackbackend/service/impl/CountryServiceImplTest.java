package com.hiketrackbackend.hiketrackbackend.service.impl;

import com.hiketrackbackend.hiketrackbackend.dto.country.CountryDeleteRequestDto;
import com.hiketrackbackend.hiketrackbackend.dto.country.CountryRequestDto;
import com.hiketrackbackend.hiketrackbackend.dto.country.CountryRespondDto;
import com.hiketrackbackend.hiketrackbackend.dto.country.CountryRespondWithPhotoDto;
import com.hiketrackbackend.hiketrackbackend.dto.country.CountrySearchParameters;
import com.hiketrackbackend.hiketrackbackend.exception.EntityNotFoundException;
import com.hiketrackbackend.hiketrackbackend.mapper.CountryMapper;
import com.hiketrackbackend.hiketrackbackend.model.country.Continent;
import com.hiketrackbackend.hiketrackbackend.model.country.Country;
import com.hiketrackbackend.hiketrackbackend.repository.country.CountryRepository;
import com.hiketrackbackend.hiketrackbackend.repository.country.CountrySpecificationBuilder;
import com.hiketrackbackend.hiketrackbackend.service.files.FileStorageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CountryServiceImplTest {
    @Mock
    private CountryRepository countryRepository;

    @Mock
    private CountryMapper countryMapper;

    @Mock
    private FileStorageService s3Service;

    @Mock
    private CountrySpecificationBuilder countrySpecificationBuilder;

    @InjectMocks
    private CountryServiceImpl countryService;
    private CountryRequestDto countryRequestDto;
    private MultipartFile file;
    private Country country;
    private CountryRespondDto countryRespondDto;

    @BeforeEach
    public void setUp() {
        countryRequestDto = new CountryRequestDto();
        countryRequestDto.setName("Test Country");
        countryRequestDto.setContinent(Continent.EUROPE);

        file = mock(MultipartFile.class);

        country = new Country();
        country.setId(1L);
        country.setName("Test Country");
        country.setContinent(Continent.EUROPE);
        country.setPhoto("photoUrl");

        countryRespondDto = new CountryRespondDto();
        countryRespondDto.setId(1L);
        countryRespondDto.setName("Test Country");
        countryRespondDto.setContinent(Continent.EUROPE);
    }

    @Test
    @DisplayName("Create country with empty photo")
    public void testCreateCountryWhenFileIsEmptyThenThrowRuntimeException() {
        when(file.isEmpty()).thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                countryService.createCountry(countryRequestDto, file)
        );

        assertEquals("Country photo is mandatory. Please upload a file.", exception.getMessage());
    }

    @Test
    @DisplayName("Create country with valid data")
    public void testCreateCountryWhenFileIsNotEmptyThenReturnCountryRespondDto() {
        when(file.isEmpty()).thenReturn(false);
        when(countryMapper.toEntity(countryRequestDto)).thenReturn(country);
        when(s3Service.uploadFileToS3(anyString(), anyList())).thenReturn(Collections.singletonList("photoUrl"));
        when(countryRepository.save(country)).thenReturn(country);
        when(countryMapper.toDto(country)).thenReturn(countryRespondDto);

        CountryRespondDto result = countryService.createCountry(countryRequestDto, file);

        assertNotNull(result);
        assertEquals(countryRespondDto.getId(), result.getId());
        assertEquals(countryRespondDto.getName(), result.getName());
        assertEquals(countryRespondDto.getContinent(), result.getContinent());

        verify(countryMapper, times(1)).toEntity(countryRequestDto);
        verify(s3Service, times(1)).uploadFileToS3(anyString(), anyList());
        verify(countryRepository, times(1)).save(country);
        verify(countryMapper, times(1)).toDto(country);
    }

    @Test
    @DisplayName("Update country with empty photo")
    public void testUpdateCountryWhenFileIsEmptyThenThrowRuntimeException() {
        when(file.isEmpty()).thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                countryService.updateCountry(countryRequestDto, file, 1L)
        );

        assertEquals("Country photo is mandatory. Please upload a file.", exception.getMessage());
    }

    @Test
    @DisplayName("Update country with valid photo")
    public void testUpdateCountryWhenFileIsNotEmptyThenReturnCountryRespondDto() {
        when(file.isEmpty()).thenReturn(false);
        when(countryRepository.findById(1L)).thenReturn(Optional.of(country));
        when(s3Service.uploadFileToS3(anyString(), anyList())).thenReturn(Collections.singletonList("photoUrl"));
        when(countryRepository.save(country)).thenReturn(country);
        when(countryMapper.toDto(country)).thenReturn(countryRespondDto);

        CountryRespondDto result = countryService.updateCountry(countryRequestDto, file, 1L);

        assertNotNull(result);
        assertEquals(countryRespondDto.getId(), result.getId());
        assertEquals(countryRespondDto.getName(), result.getName());
        assertEquals(countryRespondDto.getContinent(), result.getContinent());

        verify(countryRepository, times(1)).findById(1L);
        verify(s3Service, times(1)).deleteFileFromS3("photoUrl");
        verify(s3Service, times(1)).uploadFileToS3(anyString(), anyList());
        verify(countryRepository, times(1)).save(country);
        verify(countryMapper, times(1)).toDto(country);
    }

    @Test
    @DisplayName("Get country with not valid id")
    public void testGetByIdWhenCountryDoesNotExistThenThrowEntityNotFoundException() {
        when(countryRepository.findById(1L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                countryService.getById(1L)
        );

        assertEquals("Could not find country with id: 1", exception.getMessage());

        verify(countryRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Get country by valid id")
    public void testGetByIdWhenCountryExistsThenReturnCountryRespondDto() {
        when(countryRepository.findById(1L)).thenReturn(Optional.of(country));
        when(countryMapper.toDto(country)).thenReturn(countryRespondDto);

        CountryRespondDto result = countryService.getById(1L);

        assertNotNull(result);
        assertEquals(countryRespondDto.getId(), result.getId());
        assertEquals(countryRespondDto.getName(), result.getName());
        assertEquals(countryRespondDto.getContinent(), result.getContinent());

        verify(countryRepository, times(1)).findById(1L);
        verify(countryMapper, times(1)).toDto(country);
    }

    @Test
    @DisplayName("Search country with no data in DB")
    public void testSearchWhenRepositoryReturnsEmptyListThenReturnEmptyList() {
        CountrySearchParameters params = new CountrySearchParameters(new String[]{"Europe"}, new String[]{"Test Country"});
        Pageable pageable = PageRequest.of(0, 10);

        when(countrySpecificationBuilder.build(params)).thenReturn(mock(Specification.class));
        when(countryRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(new PageImpl<>(Collections.emptyList()));

        List<CountryRespondDto> result = countryService.search(params, pageable);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(countrySpecificationBuilder, times(1)).build(params);
        verify(countryRepository, times(1)).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    @DisplayName("Get all countries with empty DB")
    public void testGetAllWhenRepositoryReturnsEmptyListThenReturnEmptyList() {
        when(countryRepository.findAll()).thenReturn(Collections.emptyList());

        List<CountryRespondDto> result = countryService.getAll(PageRequest.of(0, 10));

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(countryRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Get 10 random countries with valid data")
    public void testGetTenRandomCountriesWhenRepositoryReturnsCountriesThenReturnCountries() {
        List<Country> countries = Collections.singletonList(country);
        List<CountryRespondWithPhotoDto> countryRespondWithPhotoDtos = Collections.singletonList(new CountryRespondWithPhotoDto());

        when(countryRepository.findTenRandomCountry()).thenReturn(countries);
        when(countryMapper.toDto(anyList())).thenReturn(countryRespondWithPhotoDtos);

        List<CountryRespondWithPhotoDto> result = countryService.getTenRandomCountries();

        assertNotNull(result);
        assertEquals(1, result.size());

        verify(countryRepository, times(1)).findTenRandomCountry();
        verify(countryMapper, times(1)).toDto(anyList());
    }

    @Test
    @DisplayName("Get 10 random countries with empty DB")
    public void testGetTenRandomCountriesWhenRepositoryReturnsEmptyListThenReturnEmptyList() {
        when(countryRepository.findTenRandomCountry()).thenReturn(Collections.emptyList());

        List<CountryRespondWithPhotoDto> result = countryService.getTenRandomCountries();

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(countryRepository, times(1)).findTenRandomCountry();
    }

    @Test
    @DisplayName("Delete country with valid data")
    public void testDeleteByNameWhenCountryIsFoundThenDeleteCountry() {
        CountryDeleteRequestDto requestDto = new CountryDeleteRequestDto();
        requestDto.setName("Test Country");

        when(countryRepository.findCountryByName("Test Country")).thenReturn(Optional.of(country));

        countryService.deleteByName(requestDto);

        verify(countryRepository, times(1)).delete(country);
    }

    @Test
    @DisplayName("Delete county with not valid name")
    public void testDeleteByNameWhenCountryIsNotFoundThenThrowEntityNotFoundException() {
        CountryDeleteRequestDto requestDto = new CountryDeleteRequestDto();
        requestDto.setName("Nonexistent Country");

        when(countryRepository.findCountryByName("Nonexistent Country")).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                countryService.deleteByName(requestDto)
        );

        assertEquals("Could not find country with name: Nonexistent Country", exception.getMessage());

        verify(countryRepository, times(1)).findCountryByName("Nonexistent Country");
    }
}