package com.darquran.application.dto.live;

import com.darquran.domain.model.enums.Section;
import com.darquran.domain.model.enums.live.LiveAccessType;
import com.darquran.domain.model.enums.live.LiveSessionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LiveSessionResponse {

    private String id;
    private String title;
    private String description;
    private String streamKey;
    /** URL RTMP pour envoyer le flux (OBS, etc.) : serveur + application, ex. rtmp://localhost:1935/live */
    private String rtmpIngestUrl;
    private String hlsPlaybackUrl;
    private LiveSessionStatus status;
    private LiveAccessType accessType;
    private boolean adaptiveQualityEnabled;
    private boolean recordingEnabled;
    private String recordingUrl;
    private LocalDateTime scheduledStartAt;
    private LocalDateTime scheduledEndAt;
    private LocalDateTime startedAt;
    private LocalDateTime endedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String userId;
    private String userName;
    /** Section du professeur : accès réservé aux utilisateurs de la même section (HOMME/FEMME). */
    private Section section;
    private int commentCount;
}
