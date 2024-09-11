package com.hiketrackbackend.hiketrackbackend.dto.country;

import com.hiketrackbackend.hiketrackbackend.model.Country;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CountryRequestDto {
    @NotBlank
    @Size(max = 170)
    private String name;
    @NotBlank
    private Country.Continent continent;
}
