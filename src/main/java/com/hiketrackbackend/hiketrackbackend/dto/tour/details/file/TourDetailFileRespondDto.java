package com.hiketrackbackend.hiketrackbackend.dto.tour.details.file;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class TourDetailFileRespondDto {
    private Long id;
    private LocalDateTime createdAt;
    private String fileUrl;
}
