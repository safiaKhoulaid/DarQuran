package com.darquran.application.service;

public interface WhatsAppService {

    void sendOTP(String toPhoneNumber, String otpCode);
}