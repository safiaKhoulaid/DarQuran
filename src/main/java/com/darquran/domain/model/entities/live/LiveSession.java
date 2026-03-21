package com.darquran.domain.model.entities.live;

import com.darquran.domain.model.entities.users.User;
import com.darquran.domain.model.enums.Section;
import com.darquran.domain.model.enums.live.LiveAccessType;
import com.darquran.domain.model.enums.live.LiveSessionStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "live_sessions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LiveSession {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String title;

    @Column(length = 2000)
    private String description;

    /**
     * Clé du flux (ex: nom du stream RTMP/HLS, utilisé pour l'URL de lecture).
     */
    @Column(nullable = false, unique = true)
    private String streamKey;

    /**
     * URL HLS de lecture (ex: /hls/{streamKey}.m3u8).
     */
    private String hlsPlaybackUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private LiveSessionStatus status = LiveSessionStatus.SCHEDULED;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private LiveAccessType accessType;

    /**
     * Qualité adaptative : activée ou non (HLS gère les variantes côté lecteur).
     */
    @Builder.Default
    private boolean adaptiveQualityEnabled = true;

    /**
     * Enregistrement automatique activé pour cette session.
     */
    @Builder.Default
    private boolean recordingEnabled = true;

    /**
     * URL de l'enregistrement une fois disponible (post-traitement ou stockage).
     */
    private String recordingUrl;

    @Column(nullable = false)
    private LocalDateTime scheduledStartAt;

    private LocalDateTime scheduledEndAt;

    private LocalDateTime startedAt;

    private LocalDateTime endedAt;

    /**
     * Notification pré-diffusion envoyée ou non.
     */
    @Builder.Default
    private boolean notificationSent = false;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    /**
     * Section du professeur (HOMME/FEMME). Restreint l'accès au live : seuls les utilisateurs de la même section peuvent regarder.
     */
    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private Section section;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "liveSession", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("createdAt ASC")
    @Builder.Default
    private List<LiveComment> comments = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }


    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
