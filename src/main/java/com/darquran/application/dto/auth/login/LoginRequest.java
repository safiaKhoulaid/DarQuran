package com.darquran.application.dto.auth.login;

import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {

    @Email
    private String email ;

    private String password ;



}
