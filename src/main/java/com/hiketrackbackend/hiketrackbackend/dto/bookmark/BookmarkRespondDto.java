package com.hiketrackbackend.hiketrackbackend.dto.bookmark;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
public class BookmarkRespondDto {
    private Long id;
    private Long userId;
    private Long tourId;
    private LocalDateTime addedAt;
}
