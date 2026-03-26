package com.darquran.presentation.advice;

import com.darquran.domain.exception.InvalidOTPException;
import com.darquran.domain.exception.UserNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;
    private MockHttpServletRequest request;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
        request = new MockHttpServletRequest();
        request.setRequestURI("/api/test");
    }

    @Test
    void handleUserNotFound() {
        var res = handler.handleUserNotFound(new UserNotFoundException("id-1"), request);
        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(res.getBody()).isNotNull();
        assertThat(res.getBody().getMessage()).contains("id-1");
        assertThat(res.getBody().getPath()).isEqualTo("/api/test");
    }

    @Test
    void handleInvalidOtp() {
        var res = handler.handleInvalidOTP(new InvalidOTPException("otp"), request);
        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(res.getBody()).isNotNull();
        assertThat(res.getBody().getMessage()).isEqualTo("otp");
    }

    @Test
    void handleBadCredentials() {
        var res = handler.handleBadCredentials(new BadCredentialsException("x"), request);
        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(res.getBody()).isNotNull();
        assertThat(res.getBody().getMessage()).contains("incorrect");
    }

    @Test
    void handleIllegalArgument() {
        var res = handler.handleIllegalArgument(new IllegalArgumentException("bad arg"), request);
        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(res.getBody()).isNotNull();
        assertThat(res.getBody().getMessage()).isEqualTo("bad arg");
    }

    @Test
    void handleIllegalState() {
        var res = handler.handleIllegalState(new IllegalStateException("s3 down"), request);
        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.SERVICE_UNAVAILABLE);
        assertThat(res.getBody()).isNotNull();
        assertThat(res.getBody().getMessage()).isEqualTo("s3 down");
    }

    @Test
    void handleEntityNotFound() {
        var res = handler.handleEntityNotFound(new EntityNotFoundException("missing"), request);
        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(res.getBody()).isNotNull();
        assertThat(res.getBody().getMessage()).isEqualTo("missing");
    }

    @Test
    void handleValidationException_errorsEmpty_branchFalse() throws Exception {
        var mp = methodParameterForBody();
        var target = new ValidationBody();
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(target, "body");
        var ex = new MethodArgumentNotValidException(mp, errors);

        var res = handler.handleValidationException(ex, request);

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(res.getBody()).isNotNull();
        assertThat(res.getBody().getMessage()).isEqualTo("Erreur de validation des données envoyées.");
    }

    @Test
    void handleValidationException_errorsNotEmpty_branchTrue_andFieldErrorOrObjectError() throws Exception {
        var mp = methodParameterForBody();
        var target = new ValidationBody();
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(target, "body");

        errors.addError(new FieldError("body", "name", "required"));
        errors.addError(new ObjectError("global", "global-fail"));
        var ex = new MethodArgumentNotValidException(mp, errors);

        var res = handler.handleValidationException(ex, request);

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(res.getBody()).isNotNull();
        assertThat(res.getBody().getMessage())
                .contains("Erreur de validation des données envoyées.")
                .contains("name : required")
                .contains("global : global-fail");
    }

    @Test
    void handleGlobalException() {
        var res = handler.handleGlobalException(new RuntimeException("boom"), request);
        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(res.getBody()).isNotNull();
        assertThat(res.getBody().getMessage()).isEqualTo("Une erreur inattendue est survenue");
    }

    private static MethodParameter methodParameterForBody() throws Exception {
        Method m = ValidationApi.class.getDeclaredMethod("body", ValidationBody.class);
        return new MethodParameter(m, 0);
    }

    @SuppressWarnings("unused")
    private static class ValidationApi {
        void body(ValidationBody body) {
        }
    }

    private static class ValidationBody {
        @SuppressWarnings("unused")
        String name;
    }
}

