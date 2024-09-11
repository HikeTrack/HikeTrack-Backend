package com.hiketrackbackend.hiketrackbackend.dto.tour;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import java.time.ZonedDateTime;

@Getter
@Setter
public class TourRequestDto {
    @NotBlank
    private String name;

    @NotBlank
    @Min(0)
    private int length;

    private ZonedDateTime date;

    @NotBlank
    @Min(0)
    private Long countryId;
}
