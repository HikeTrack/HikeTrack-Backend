package com.hiketrackbackend.hiketrackbackend.dto.tour;

import com.hiketrackbackend.hiketrackbackend.dto.tour.details.DetailsRespondDto;
import com.hiketrackbackend.hiketrackbackend.dto.reviews.ReviewsRespondDto;
import com.hiketrackbackend.hiketrackbackend.model.tour.Difficulty;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

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
    private List<ReviewsRespondDto> reviews;
    private int currentReviewPage;
    private int totalReviewPages;
    private long totalReviewElements;
    private String mainPhoto;
    private Long guideId;
    private Long averageRating;
    private Long totalAmountOfMarks;
}
