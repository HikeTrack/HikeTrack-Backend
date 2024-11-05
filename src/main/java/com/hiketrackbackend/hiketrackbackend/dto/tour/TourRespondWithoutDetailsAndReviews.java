package com.hiketrackbackend.hiketrackbackend.dto.tour;

import com.hiketrackbackend.hiketrackbackend.model.tour.Difficulty;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TourRespondWithoutDetailsAndReviews {
    private Long id;
    private String name;
    private int length;
    private BigDecimal price;
    private ZonedDateTime date;
    private Difficulty difficulty;
    private Long countryId;
    private String mainPhoto;
    private Long guideId;
    private Long averageRating;
    private Long totalAmountOfMarks;
}
