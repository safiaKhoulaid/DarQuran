package com.darquran.support;

import com.darquran.infrastructure.config.security.JwtAuthenticationFilter;
import com.darquran.infrastructure.config.security.SecurityConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.core.annotation.AliasFor;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * {@link WebMvcTest} avec exclusions pour la sécurité : sans cela, Boot crée
 * {@code inMemoryUserDetailsManager} en plus du mock {@link com.darquran.infrastructure.config.security.CustomUserDetailsService},
 * ce qui casse l'injection de {@code JwtAuthenticationFilter} (deux {@code UserDetailsService}).
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@WebMvcTest(excludeAutoConfiguration = UserDetailsServiceAutoConfiguration.class)
@Import({SecurityConfiguration.class, JwtAuthenticationFilter.class})
@ActiveProfiles("test")
public @interface DarQuranWebMvcTest {

    @AliasFor(annotation = WebMvcTest.class, attribute = "controllers")
    Class<?>[] value() default {};
}
