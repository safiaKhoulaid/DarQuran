package com.darquran.application.dto.auth.login;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Setter
@Getter
public class LoginResponse {
    private String id;
    private String token;
    private String refreshToken;
    private String nom;
    private String prenom;
    private String email;
    private String role;

}