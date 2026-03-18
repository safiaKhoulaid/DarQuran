package com.darquran.presentation.controller;

import com.darquran.application.dto.auth.login.LoginRequest;
import com.darquran.application.dto.auth.login.LoginResponse;
import com.darquran.application.dto.auth.logout.LogoutRequest;
import com.darquran.application.dto.auth.refreshToken.RefreshTokenRequest;
import com.darquran.application.dto.auth.register.RegisterRequest;
import com.darquran.application.dto.auth.register.RegisterResponse;
import com.darquran.application.dto.auth.resetPassword.ForgotPasswordRequest;
import com.darquran.application.dto.auth.resetPassword.ResetPasswordRequest;
import com.darquran.application.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService service;

    /*============ INSCREPTION ==========*/

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(
            @RequestBody RegisterRequest request
    ) {
        return ResponseEntity.ok(service.register(request));
    }

    /*========== Connection =========*/

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> authenticate(
            @RequestBody LoginRequest request
    ) {
        return ResponseEntity.ok(service.authenticate(request));
    }

    /*======== REFRESH TOKEN =========*/

    @PostMapping("/refresh-token")
    public ResponseEntity<LoginResponse> refreshToken(
            @RequestBody RefreshTokenRequest request
    ) {
        return ResponseEntity.ok(service.refreshToken(request.getRefreshToken()));
    }

    /*======== LOUGOUT =========*/
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @RequestHeader(name = "Authorization", required = false) String authHeader,
            @RequestBody LogoutRequest request
    ) {
        service.logout(authHeader, request.getRefreshToken());
        return ResponseEntity.noContent().build();
    }

    /*======== FORGET PASSWORD ==========*/
    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        service.requestPasswordReset(request);
        return ResponseEntity.ok("Code envoyé avec succès");
    }

    /*========= RESET PASSWORD==========*/
    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        service.resetPassword(request);
        return ResponseEntity.ok("Mot de passe modifié avec succès");
    }

}