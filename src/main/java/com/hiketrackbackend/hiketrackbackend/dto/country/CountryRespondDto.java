package com.hiketrackbackend.hiketrackbackend.dto.country;

import com.hiketrackbackend.hiketrackbackend.model.country.Continent;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CountryRespondDto {
    private Long id;
    private String name;
    private Continent continent;
    private List<String> photoUrls;
}
