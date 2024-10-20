package com.hiketrackbackend.hiketrackbackend.dto.user;

import com.hiketrackbackend.hiketrackbackend.dto.userProfile.UserProfileRespondDto;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UserRespondDto {
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private List<String> role;
    private UserProfileRespondDto userProfileRespondDto;
    //TODO добавить тур ид сюда
}
