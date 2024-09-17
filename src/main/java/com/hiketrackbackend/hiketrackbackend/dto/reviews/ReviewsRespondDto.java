package com.hiketrackbackend.hiketrackbackend.dto.reviews;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
public class ReviewsRespondDto {
    private Long id;
    private Long userId;
    private String content;
    private Long tourId;
    private LocalDateTime dateCreated;
}
