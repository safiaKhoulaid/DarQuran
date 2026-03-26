package com.darquran.domain.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DomainExceptionsTest {

    @Test
    void userNotFound_byId_messageContainsId() {
        var ex = new UserNotFoundException("user-1");
        assertThat(ex.getMessage()).contains("user-1");
    }

    @Test
    void userNotFound_byEmail_constructor() {
        var ex = new UserNotFoundException("a@b.com", true);
        assertThat(ex.getMessage()).contains("a@b.com");
    }

    @Test
    void invalidOtp_messagePropagated() {
        var ex = new InvalidOTPException("bad");
        assertThat(ex.getMessage()).isEqualTo("bad");
    }
}

