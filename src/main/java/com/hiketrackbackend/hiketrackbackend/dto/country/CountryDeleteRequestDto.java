package com.hiketrackbackend.hiketrackbackend.dto.country;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CountryDeleteRequestDto {
    @NotBlank(message = "Name cannot be null")
    @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters")
    private String name;
}
