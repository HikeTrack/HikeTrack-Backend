package com.hiketrackbackend.hiketrackbackend.dto.tour;

import com.hiketrackbackend.hiketrackbackend.model.Tour;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Getter
@Setter
public class TourRequestDto {
    @NotBlank
    private String name;

    @NotBlank
    @Min(0)
    private int length;

    @NotBlank
    @Min(0)
    private BigDecimal price;

    @NotBlank
    private ZonedDateTime date;

    @NotBlank
    private Tour.Difficulty difficulty;

    @NotBlank
    @Min(0)
    private Long countryId;
}
