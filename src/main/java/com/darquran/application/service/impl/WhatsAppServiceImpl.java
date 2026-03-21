package com.darquran.application.service.impl;

import com.darquran.application.service.WhatsAppService;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class WhatsAppServiceImpl implements WhatsAppService {

    @Value("${twilio.account_sid}")
    private String accountSid;

    @Value("${twilio.auth_token}")
    private String authToken;

    @Value("${twilio.phone_number}")
    private String fromNumber;

    @PostConstruct
    public void init() {
        Twilio.init(accountSid, authToken);
    }

    @Override
    public void sendOTP(String toPhoneNumber, String otpCode) {
        try {

            Message message = Message.creator(
                            new PhoneNumber("whatsapp:" + toPhoneNumber),
                            new PhoneNumber("whatsapp:" + fromNumber),
                            "Salam, votre code DarQuran est : " + otpCode)
                    .create();

            System.out.println("✅ Message envoyé! SID: " + message.getSid());

        } catch (Exception e) {
            System.err.println("❌ Erreur Twilio: " + e.getMessage());
        }
    }
}

