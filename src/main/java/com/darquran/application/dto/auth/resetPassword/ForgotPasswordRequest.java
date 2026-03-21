package com.darquran.application.dto.auth.resetPassword;

import jakarta.validation.constraints.NotBlank;

/** Contact = email ou numéro de téléphone. */
public record ForgotPasswordRequest(
        @NotBlank(message = "L'email ou le numéro de téléphone est requis") String contact
) {}