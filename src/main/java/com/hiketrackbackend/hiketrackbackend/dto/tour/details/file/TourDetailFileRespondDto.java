package com.hiketrackbackend.hiketrackbackend.dto.tour.details.file;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TourDetailFileRespondDto {
    private Long id;
    private LocalDateTime createdAt;
    private String fileUrl;
}
