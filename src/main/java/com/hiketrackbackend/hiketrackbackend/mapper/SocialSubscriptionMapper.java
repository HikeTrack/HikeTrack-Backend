package com.hiketrackbackend.hiketrackbackend.mapper;

import com.hiketrackbackend.hiketrackbackend.config.MapperConfig;
import com.hiketrackbackend.hiketrackbackend.dto.socialSubscription.SubscriptionRequestDto;
import com.hiketrackbackend.hiketrackbackend.model.SocialSubscription;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface SocialSubscriptionMapper {
    SocialSubscription toEntity(SubscriptionRequestDto requestDto);
}
