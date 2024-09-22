package com.hiketrackbackend.hiketrackbackend.dto.details;

import com.hiketrackbackend.hiketrackbackend.model.details.Activity;
import com.hiketrackbackend.hiketrackbackend.model.details.RouteType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DetailsRequestDto {
    private String  additionalPhotos;

    @NotBlank
    @Min(0)
    private int elevationGain;

    @NotBlank(message = "Rout type cannot be null")
    private RouteType routeType;

    @NotBlank
    @Min(value = 0, message = "Duration of trip must be positive")
    private int duration;

    @NotBlank
    private String map;

    @NotBlank(message = "Set at lest one activity")
    private Activity activity;
}