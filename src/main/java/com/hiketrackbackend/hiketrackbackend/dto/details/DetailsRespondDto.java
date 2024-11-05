package com.hiketrackbackend.hiketrackbackend.dto.details;

import com.hiketrackbackend.hiketrackbackend.model.tour.details.Activity;
import com.hiketrackbackend.hiketrackbackend.model.tour.details.RouteType;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DetailsRespondDto {
    private Long id;
    private List<String> additionalPhotos;
    private int elevationGain;
    private RouteType routeType;
    private int duration;
    private String map;
    private Activity activity;
}
