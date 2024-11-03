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
public class TourRequestDto {

    @NotBlank(message = "Tour name is mandatory and cannot be empty")
    private String name;

    @Positive(message = "Length cannot be negative")
    private Integer length;

    @NotNull(message = "Price cannot be null")
    @Positive(message = "Price has to be positive")
    private BigDecimal price;

    @NotNull(message = "Enter departure date")
    private ZonedDateTime date;

    @NotNull(message = "Difficulty cannot be empty")
    private Difficulty difficulty;

    @NotNull(message = "Country id has to be specified")
    @Positive(message = "Country id has to be positive")
    private Long countryId;

    @NotNull(message = "Details must be provided")
    private DetailsRequestDto detailsRequestDto;
}
