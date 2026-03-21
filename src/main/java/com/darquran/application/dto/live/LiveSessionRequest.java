package com.darquran.application.dto.live;

import com.darquran.domain.model.enums.live.LiveAccessType;
import com.darquran.infrastructure.config.json.LenientLocalDateTimeDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LiveSessionRequest {

    /** Optionnel : ID de l'utilisateur animateur (réservé à Admin/SuperAdmin). Si absent, l'utilisateur connecté est utilisé. */
    private String userId;

    @NotBlank(message = "Le titre est requis")
    @Size(min = 3, max = 255)
    private String title;

    @Size(max = 2000)
    private String description;

    @NotBlank(message = "Le stream key est requis")
    @Size(max = 100)
    @Pattern(regexp = "^[a-zA-Z0-9_-]+$", message = "Le stream key ne doit contenir que des lettres, chiffres, tirets et underscores (pas d'espaces)")
    private String streamKey;

    @NotNull(message = "Le type d'accès est requis")
    private LiveAccessType accessType;

    private boolean adaptiveQualityEnabled = true;

    private boolean recordingEnabled = true;

    @NotNull(message = "La date et l'heure de début sont requises")
    @JsonDeserialize(using = LenientLocalDateTimeDeserializer.class)
    private LocalDateTime scheduledStartAt;

    @JsonDeserialize(using = LenientLocalDateTimeDeserializer.class)
    private LocalDateTime scheduledEndAt;
}
