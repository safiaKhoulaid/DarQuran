package com.darquran.application.dto.auth.resetPassword;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Contact = email ou numéro de téléphone (le même que celui utilisé dans forgot-password).
 */
public record ResetPasswordRequest(
        @NotBlank(message = "Le contact (email ou téléphone) est requis")
        String contact,
        @NotBlank(message = "Le code OTP est requis")
        String otp,
        @NotBlank(message = "Le nouveau mot de passe est requis")
        @Size(min = 6, message = "Le mot de passe doit contenir au moins 6 caractères")
        String newPassword
) {
}
