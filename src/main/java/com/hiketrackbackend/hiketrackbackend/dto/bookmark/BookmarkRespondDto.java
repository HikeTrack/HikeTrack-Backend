package com.hiketrackbackend.hiketrackbackend.dto.bookmark;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookmarkRespondDto {
    private Long id;
    private Long userId;
    private Long tourId;
    private LocalDateTime addedAt;
}
