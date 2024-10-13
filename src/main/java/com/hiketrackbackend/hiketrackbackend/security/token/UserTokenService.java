package com.hiketrackbackend.hiketrackbackend.security.token;

import com.hiketrackbackend.hiketrackbackend.model.UserToken;

public interface UserTokenService {
    UserToken createToken(Long userId);
    UserToken getUserToken(String tokenKey);
}
