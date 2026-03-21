package com.darquran.application.service;

import com.darquran.domain.model.entities.users.redis.RefreshToken;

public interface RefreshTokenService {

    RefreshToken createRefreshToken(String email);

    RefreshToken findByToken(String token);

    void deleteByToken(String token);
}