package com.hiketrackbackend.hiketrackbackend.service;

import com.hiketrackbackend.hiketrackbackend.dto.AdminRequestDto;
import com.hiketrackbackend.hiketrackbackend.dto.UserDevMsgRespondDto;

public interface AdministrationService {
    UserDevMsgRespondDto createNewsletter(AdminRequestDto request);
}
