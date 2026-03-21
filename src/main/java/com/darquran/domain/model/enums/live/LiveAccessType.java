package com.darquran.domain.model.enums.live;

/**
 * Type d'accès à une session live : interne (élèves/profs) ou externe (public).
 */
public enum LiveAccessType {
    INTERNAL,  // Élèves et professeurs de l'institut
    EXTERNAL   // Public (tout le monde)
}
