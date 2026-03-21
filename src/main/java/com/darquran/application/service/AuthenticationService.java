package com.darquran.application.service;

import com.darquran.application.dto.auth.login.LoginRequest;
import com.darquran.application.dto.auth.login.LoginResponse;
import com.darquran.application.dto.auth.register.RegisterRequest;
import com.darquran.application.dto.auth.register.RegisterResponse;
import com.darquran.application.dto.auth.resetPassword.ForgotPasswordRequest;
import com.darquran.application.dto.auth.resetPassword.ResetPasswordRequest;

public interface AuthenticationService {

    RegisterResponse register(RegisterRequest request);

    LoginResponse authenticate(LoginRequest request);

    LoginResponse refreshToken(String requestRefreshToken);

    void logout(String authHeader, String refreshToken);

    void requestPasswordReset(ForgotPasswordRequest request);

    void resetPassword(ResetPasswordRequest request);
}