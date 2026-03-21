package com.darquran.application.dto.auth.register;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Builder
@Getter
public class RegisterResponse {
    private String token;
    private String refreshToken;
}
