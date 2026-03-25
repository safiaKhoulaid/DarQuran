package com.darquran;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import com.darquran.application.service.BlacklistService;
import com.darquran.application.service.WhatsAppService;
import com.darquran.domain.repository.RefreshTokenRepository;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class DarQuranApplicationTests {

    @MockBean
    private JavaMailSender javaMailSender;

    @MockBean
    private BlacklistService blacklistService;

    @MockBean
    private RefreshTokenRepository refreshTokenRepository;

    @MockBean
    private StringRedisTemplate stringRedisTemplate;

    @MockBean
    private WhatsAppService whatsAppService;

    @Test
    void contextLoads() {
    }
}
