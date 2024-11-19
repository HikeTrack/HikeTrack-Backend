package com.hiketrackbackend.hiketrackbackend.dto.country;

import com.hiketrackbackend.hiketrackbackend.model.country.Continent;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CountryRespondWithPhotoDto {
    private Long id;
    private String name;
    private Continent continent;
    private String photo;
}
