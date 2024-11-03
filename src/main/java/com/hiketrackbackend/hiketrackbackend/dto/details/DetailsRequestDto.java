package com.hiketrackbackend.hiketrackbackend.dto.details;

import com.hiketrackbackend.hiketrackbackend.model.tour.details.Activity;
import com.hiketrackbackend.hiketrackbackend.model.tour.details.RouteType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DetailsRequestDto {

    @Positive(message = "Elevation gain must be positive")
    private Integer elevationGain;

    @NotNull(message = "Route type cannot be null")
    private RouteType routeType;

    @Positive(message = "Duration of trip must be positive")
    private Integer duration;

    @NotBlank(message = "Map cannot be blank")
    private String map;

    @NotNull(message = "Set at least one activity")
    private Activity activity;
}
