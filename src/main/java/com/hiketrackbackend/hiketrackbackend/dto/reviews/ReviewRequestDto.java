package com.hiketrackbackend.hiketrackbackend.dto.reviews;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReviewRequestDto {
    @NotBlank
    @Size(min = 1, max = 600)
    private String content;
}
