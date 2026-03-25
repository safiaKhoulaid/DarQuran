package com.darquran.domain.model.valueobjects;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Embeddable
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Adresse {

    @NotBlank(message = "Le nom de la rue est obligatoire")
    @Size(min = 5, max = 100, message = "L'adresse doit contenir entre 5 et 100 caractères")
    private String rue;

    @NotBlank(message = "La ville est obligatoire")
    private String ville;

    @Pattern(regexp = "^\\d{5}$", message = "Le code postal doit contenir exactement 5 chiffres")
    private String codePostal;

    @NotBlank(message = "Le pays est obligatoire")
    @Builder.Default
    private String pays = "Maroc";

    public String adresseComplete() {
        return rue + ", " + codePostal + " " + ville + ", " + pays;
    }
}