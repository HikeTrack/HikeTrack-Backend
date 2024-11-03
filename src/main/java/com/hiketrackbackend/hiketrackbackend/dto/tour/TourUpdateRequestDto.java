package com.hiketrackbackend.hiketrackbackend.dto.tour;

import com.hiketrackbackend.hiketrackbackend.dto.details.DetailsRequestDto;
import com.hiketrackbackend.hiketrackbackend.model.tour.Difficulty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Getter
@Setter
public class TourUpdateRequestDto {

    @NotBlank(message = "Tour name is mandatory and cannot be empty")
    private String name;

    @Positive(message = "Length must be positive")
    private Integer length;

    @NotNull(message = "Price cannot be null")
    @Positive(message = "Price has to be positive")
    private BigDecimal price;

    @NotNull(message = "Enter departure date")
    private ZonedDateTime date;

    @NotNull(message = "Difficulty cannot be null")
    private Difficulty difficulty;

    @NotNull(message = "Country ID must be specified")
    @Positive(message = "Country ID has to be positive")
    private Long countryId;

    @NotNull(message = "Details must be provided")
    private DetailsRequestDto detailsRequestDto;
}
