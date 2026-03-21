package com.darquran.application.service;

public interface BlacklistService {

    void blackListToken(String token, long timeToLive);

    boolean isBlacklisted(String token);
}