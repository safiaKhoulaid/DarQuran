package com.darquran.application.dto.valueobjects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdresseResponse {

    private String rue;
    private String ville;
    private String codePostal;
    private String pays;
    private String adresseComplete;
}