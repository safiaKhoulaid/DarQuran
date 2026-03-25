package com.darquran.support;

import com.darquran.application.service.BlacklistService;
import com.darquran.infrastructure.config.security.CustomUserDetailsService;
import com.darquran.infrastructure.config.security.JwtService;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.AuthenticationProvider;

/**
 * Beans de sécurité mockés pour que {@code @WebMvcTest} charge le filtre JWT et {@link com.darquran.infrastructure.config.security.ApplicationConfig}
 * sans scanner les {@code @Service} du domaine.
 */
public abstract class AbstractWebMvcControllerTest {

    @MockBean
    protected JwtService jwtService;

    @MockBean
    protected BlacklistService blacklistService;

    @MockBean
    protected CustomUserDetailsService customUserDetailsService;

    @MockBean
    protected AuthenticationProvider authenticationProvider;
}
