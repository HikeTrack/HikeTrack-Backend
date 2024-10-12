package com.hiketrackbackend.hiketrackbackend.security.token;

public interface UserTokenService {
    void createToken(Long userId);
}
