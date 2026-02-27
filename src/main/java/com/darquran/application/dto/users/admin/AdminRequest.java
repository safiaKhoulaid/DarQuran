package com.darquran.application.dto.users.admin;

import com.darquran.domain.model.enums.Section;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class AdminRequest {

    @NotBlank(message = "Le nom est requis")
    @Size(max = 100)
    private String nom;

    @NotBlank
    @Size(max = 100)
    private String prenom;

    @NotBlank(message = "L'email est requis")
    @Email(message = "L'email doit être valide")
    private String email;

    @Size(min = 8, message = "Le mot de passe doit contenir au moins 8 caractères")
    private String password;

    @Size(max = 20, message = "Le numéro de téléphone doit contenir au plus 20 caractères")
    private String telephone;

     @NotNull(message = "La section est requise")
    private Section section;

    private LocalDate dateNaissance;

    private String photoUrl;
}

