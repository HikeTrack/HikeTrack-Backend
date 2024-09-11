package com.hiketrackbackend.hiketrackbackend.dto.tour;

import lombok.Getter;
import lombok.Setter;
import java.time.ZonedDateTime;

@Getter
@Setter
public class TourRespondDto {
    private Long id;
    private String name;
    private int length;
    private ZonedDateTime date;
    private Long countryId;
}
