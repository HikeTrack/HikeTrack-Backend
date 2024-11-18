
package com.hiketrackbackend.hiketrackbackend.dto.tour.details;

import com.hiketrackbackend.hiketrackbackend.model.tour.details.Activity;
import com.hiketrackbackend.hiketrackbackend.model.tour.details.RouteType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DetailsRespondWithoutPhotosDto {
    private Long id;
    private int elevationGain;
    private RouteType routeType;
    private int duration;
    private String map;
    private Activity activity;
    private String description;
}