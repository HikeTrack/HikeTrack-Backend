package com.hiketrackbackend.hiketrackbackend.dto.country;

import com.hiketrackbackend.hiketrackbackend.model.Country;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RespondCountryDto {
    private Long id;
    private String name;
    private Country.Continent continent;
}
