package com.hiketrackbackend.hiketrackbackend.dto.bookmark;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookmarkRequestDto {
    @NotNull(message = "tourId shouldn`t be null")
    @Positive(message = "tourId should be positive number")
    private Long tourId;
}
