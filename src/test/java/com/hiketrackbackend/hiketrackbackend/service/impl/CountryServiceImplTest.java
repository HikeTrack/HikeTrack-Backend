package com.hiketrackbackend.hiketrackbackend.service.impl;

import com.hiketrackbackend.hiketrackbackend.dto.country.*;
import com.hiketrackbackend.hiketrackbackend.exception.EntityNotFoundException;
import com.hiketrackbackend.hiketrackbackend.mapper.CountryMapper;
import com.hiketrackbackend.hiketrackbackend.model.country.Continent;
import com.hiketrackbackend.hiketrackbackend.model.country.Country;
import com.hiketrackbackend.hiketrackbackend.repository.country.CountryRepository;
import com.hiketrackbackend.hiketrackbackend.repository.country.CountrySpecificationBuilder;
import com.hiketrackbackend.hiketrackbackend.service.files.FileStorageService;
import org.junit.jupiter.api.BeforeEach;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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
    public void testCreateCountryWhenFileIsEmptyThenThrowRuntimeException() {
        // Arrange
        when(file.isEmpty()).thenReturn(true);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                countryService.createCountry(countryRequestDto, file)
        );

        assertEquals("Country photo is mandatory. Please upload a file.", exception.getMessage());
    }

    @Test
    public void testCreateCountryWhenFileIsNotEmptyThenReturnCountryRespondDto() {
        // Arrange
        when(file.isEmpty()).thenReturn(false);
        when(countryMapper.toEntity(countryRequestDto)).thenReturn(country);
        when(s3Service.uploadFileToS3(anyString(), anyList())).thenReturn(Collections.singletonList("photoUrl"));
        when(countryRepository.save(country)).thenReturn(country);
        when(countryMapper.toDto(country)).thenReturn(countryRespondDto);

        // Act
        CountryRespondDto result = countryService.createCountry(countryRequestDto, file);

        // Assert
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
    public void testUpdateCountryWhenFileIsEmptyThenThrowRuntimeException() {
        // Arrange
        when(file.isEmpty()).thenReturn(true);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                countryService.updateCountry(countryRequestDto, file, 1L)
        );

        assertEquals("Country photo is mandatory. Please upload a file.", exception.getMessage());
    }

    @Test
    public void testUpdateCountryWhenFileIsNotEmptyThenReturnCountryRespondDto() {
        // Arrange
        when(file.isEmpty()).thenReturn(false);
        when(countryRepository.findById(1L)).thenReturn(Optional.of(country));
        when(s3Service.uploadFileToS3(anyString(), anyList())).thenReturn(Collections.singletonList("photoUrl"));
        when(countryRepository.save(country)).thenReturn(country);
        when(countryMapper.toDto(country)).thenReturn(countryRespondDto);

        // Act
        CountryRespondDto result = countryService.updateCountry(countryRequestDto, file, 1L);

        // Assert
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
    public void testGetByIdWhenCountryExistsThenReturnCountryRespondDto() {
        // Arrange
        when(countryRepository.findById(1L)).thenReturn(Optional.of(country));
        when(countryMapper.toDto(country)).thenReturn(countryRespondDto);

        // Act
        CountryRespondDto result = countryService.getById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(countryRespondDto.getId(), result.getId());
        assertEquals(countryRespondDto.getName(), result.getName());
        assertEquals(countryRespondDto.getContinent(), result.getContinent());

        verify(countryRepository, times(1)).findById(1L);
        verify(countryMapper, times(1)).toDto(country);
    }

    @Test
    public void testGetByIdWhenCountryDoesNotExistThenThrowEntityNotFoundException() {
        // Arrange
        when(countryRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                countryService.getById(1L)
        );

        assertEquals("Could not find country with id: 1", exception.getMessage());

        verify(countryRepository, times(1)).findById(1L);
    }

    @Test
    public void testSearchWhenRepositoryReturnsEmptyListThenReturnEmptyList() {
        // Arrange
        CountrySearchParameters params = new CountrySearchParameters(new String[]{"Europe"}, new String[]{"Test Country"});
        Pageable pageable = PageRequest.of(0, 10);

        when(countrySpecificationBuilder.build(params)).thenReturn(mock(Specification.class));
        when(countryRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(new PageImpl<>(Collections.emptyList()));

        // Act
        List<CountryRespondDto> result = countryService.search(params, pageable);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(countrySpecificationBuilder, times(1)).build(params);
        verify(countryRepository, times(1)).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    public void testGetAllWhenRepositoryReturnsEmptyListThenReturnEmptyList() {
        // Arrange
        when(countryRepository.findAll()).thenReturn(Collections.emptyList());

        // Act
        List<CountryRespondDto> result = countryService.getAll(PageRequest.of(0, 10));

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(countryRepository, times(1)).findAll();
    }

    @Test
    public void testGetTenRandomCountriesWhenRepositoryReturnsCountriesThenReturnCountries() {
        // Arrange
        List<Country> countries = Collections.singletonList(country);
        List<CountryRespondWithPhotoDto> countryRespondWithPhotoDtos = Collections.singletonList(new CountryRespondWithPhotoDto());

        when(countryRepository.findTenRandomCountry()).thenReturn(countries);
        when(countryMapper.toDto(anyList())).thenReturn(countryRespondWithPhotoDtos);

        // Act
        List<CountryRespondWithPhotoDto> result = countryService.getTenRandomCountries();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());

        verify(countryRepository, times(1)).findTenRandomCountry();
        verify(countryMapper, times(1)).toDto(anyList());
    }

    @Test
    public void testGetTenRandomCountriesWhenRepositoryReturnsEmptyListThenReturnEmptyList() {
        // Arrange
        when(countryRepository.findTenRandomCountry()).thenReturn(Collections.emptyList());

        // Act
        List<CountryRespondWithPhotoDto> result = countryService.getTenRandomCountries();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(countryRepository, times(1)).findTenRandomCountry();
    }

    @Test
    public void testDeleteByNameWhenCountryIsFoundThenDeleteCountry() {
        // Arrange
        CountryDeleteRequestDto requestDto = new CountryDeleteRequestDto();
        requestDto.setName("Test Country");

        when(countryRepository.findCountryByName("Test Country")).thenReturn(Optional.of(country));

        // Act
        countryService.deleteByName(requestDto);

        // Assert
        verify(countryRepository, times(1)).delete(country);
    }

    @Test
    public void testDeleteByNameWhenCountryIsNotFoundThenThrowEntityNotFoundException() {
        // Arrange
        CountryDeleteRequestDto requestDto = new CountryDeleteRequestDto();
        requestDto.setName("Nonexistent Country");

        when(countryRepository.findCountryByName("Nonexistent Country")).thenReturn(Optional.empty());

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                countryService.deleteByName(requestDto)
        );

        assertEquals("Could not find country with name: Nonexistent Country", exception.getMessage());

        verify(countryRepository, times(1)).findCountryByName("Nonexistent Country");
    }
}