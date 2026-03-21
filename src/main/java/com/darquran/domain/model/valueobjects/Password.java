package com.darquran.domain.model.valueobjects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

@Embeddable
@Getter
@EqualsAndHashCode
@NoArgsConstructor(force = true)
public class Password {

    @Column(name = "password", nullable = false)
    private final String value;

    public Password(String value) {
        this.value = value;
    }


    public static Password create(String rawPassword, PasswordEncoder encoder) {

        if (rawPassword == null || rawPassword.isBlank()) {
            throw new IllegalArgumentException("Le mot de passe ne peut pas être vide.");
        }

        if (rawPassword.length() < 8) {
            throw new IllegalArgumentException("Le mot de passe doit contenir au moins 8 caractères.");
        }

        if (!rawPassword.matches(".*\\d.*")) {
            throw new IllegalArgumentException("Le mot de passe doit contenir au moins un chiffre (0-9).");
        }

        if (!rawPassword.matches(".*[a-z].*")) {
            throw new IllegalArgumentException("Le mot de passe doit contenir au moins une lettre minuscule.");
        }

        if (!rawPassword.matches(".*[A-Z].*")) {
            throw new IllegalArgumentException("Le mot de passe doit contenir au moins une lettre majuscule.");
        }

        return new Password(encoder.encode(rawPassword));
    }

    public static Password fromHash(String hash) {
        return new Password(hash);
    }

    @Override
    public String toString() {
        return "******";
    }
}