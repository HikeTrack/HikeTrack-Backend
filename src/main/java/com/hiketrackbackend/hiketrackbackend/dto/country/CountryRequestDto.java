package com.hiketrackbackend.hiketrackbackend.dto.country;

import com.hiketrackbackend.hiketrackbackend.model.country.Continent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CountryRequestDto {
    @NotBlank(message = "Country name cannot be empty")
    @Size(max = 170, message = "Maximum name size is 170 symbols")
    private String name;

    @NotBlank(message = "Continent cannot be empty")
    private Continent continent;

    @NotBlank(message = "Photo is mandatory")
    private String photo;
}
