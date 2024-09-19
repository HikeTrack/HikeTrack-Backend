package com.hiketrackbackend.hiketrackbackend.dto.bookmark;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookmarkRequestDto {
    @NotNull(message = "tourId shouldn`t be null")
    @Min(value = 1, message = "tourId should be positive number")
    private Long tourId;
}
