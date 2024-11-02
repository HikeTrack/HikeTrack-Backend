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
    private String photoUrl;

    public CountryRespondWithPhotoDto(Long id, String name, Continent continent, String photoUrl) {
        this.id = id;
        this.name = name;
        this.continent = continent;
        this.photoUrl = photoUrl;
    }

    public CountryRespondWithPhotoDto() {
    }
}
