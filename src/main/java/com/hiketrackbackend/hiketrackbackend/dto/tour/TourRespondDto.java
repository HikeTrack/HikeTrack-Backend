package com.hiketrackbackend.hiketrackbackend.dto.tour;

import com.hiketrackbackend.hiketrackbackend.model.Tour;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Getter
@Setter
public class TourRespondDto {
    private Long id;
    private String name;
    private int length;
    private BigDecimal price;
    private ZonedDateTime date;
    private Tour.Difficulty difficulty;
    private Long countryId;
}
