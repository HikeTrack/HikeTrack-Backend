package com.hiketrackbackend.hiketrackbackend.dto.tour;

import com.hiketrackbackend.hiketrackbackend.dto.details.DetailsRespondDto;
import com.hiketrackbackend.hiketrackbackend.model.tour.Difficulty;
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
    private Difficulty difficulty;
    private Long countryId;
    private DetailsRespondDto details;
    private String mainPhoto;
}
