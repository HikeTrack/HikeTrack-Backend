package com.hiketrackbackend.hiketrackbackend.dto.tour;

import com.hiketrackbackend.hiketrackbackend.dto.details.DetailsRequestDto;
import com.hiketrackbackend.hiketrackbackend.model.tour.Difficulty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Getter
@Setter
public class TourUpdateRequestDto {
    @NotBlank(message = "Tour name is mandatory and cannot be empty")
    private String name;

    @NotBlank
    @Min(value = 0, message = "Length cannot be negative")
    private int length;

    @NotBlank
    @Min(value = 0, message = "Price has to be positive")
    private BigDecimal price;

    @NotBlank(message = "Enter departure date")
    private ZonedDateTime date;

    @NotBlank(message = "Difficulty cannot be empty")
    private Difficulty difficulty;

    @NotBlank
    @Min(value = 0, message = "Country id has to be positive")
    private Long countryId;

    @NotBlank
    private DetailsRequestDto detailsRequestDto;
}
