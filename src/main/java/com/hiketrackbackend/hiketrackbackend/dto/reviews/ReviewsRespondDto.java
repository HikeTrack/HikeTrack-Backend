package com.hiketrackbackend.hiketrackbackend.dto.reviews;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReviewsRespondDto {
    private Long id;
    private Long userId;
    private String content;
    private Long tourId;
    private LocalDateTime dateCreated;
}
