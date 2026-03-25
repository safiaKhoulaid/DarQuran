package com.darquran.application.dto.valueobjects;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdresseRequest {

    @NotBlank(message = "La rue est requise")
    @Size(max = 200)
    private String rue;

    @NotBlank(message = "La ville est requise")
    @Size(max = 100)
    private String ville;

    @NotBlank(message = "Le code postal est requis")
    @Size(max = 20)
    private String codePostal;

    @NotBlank(message = "Le pays est requis")
    @Size(max = 100)
    private String pays;
}