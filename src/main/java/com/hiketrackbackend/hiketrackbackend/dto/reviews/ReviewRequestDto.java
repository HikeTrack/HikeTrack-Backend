package com.hiketrackbackend.hiketrackbackend.dto.reviews;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReviewRequestDto {

    @NotBlank(message = "Review content cannot be blank")
    @Size(min = 1, max = 600, message = "Review content must be between 1 and 600 characters")
    private String content;
}
