package com.darquran.domain.model.enums.live;

/**
 * Statut d'une session de diffusion en direct.
 */
public enum LiveSessionStatus {
    SCHEDULED,   // Planifiée
    LIVE,        // En cours de diffusion
    ENDED,       // Terminée
    CANCELLED,   // Annulée
    RECORDING    // En cours d'enregistrement (post-diffusion)
}
