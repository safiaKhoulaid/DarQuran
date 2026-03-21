package com.darquran.application.service;

import com.darquran.application.dto.live.LiveCommentRequest;
import com.darquran.application.dto.live.LiveCommentResponse;
import com.darquran.application.dto.live.LiveSessionRequest;
import com.darquran.application.dto.live.LiveSessionResponse;
import com.darquran.domain.model.enums.Section;
import com.darquran.domain.model.enums.live.LiveSessionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface LiveSessionService {

    /** currentUserId = utilisateur connecté ; l'animateur est résolu : utilisateur connecté, ou request.userId (Admin/SuperAdmin). */
    LiveSessionResponse create(String currentUserId, LiveSessionRequest request);

    LiveSessionResponse getById(String id);

    LiveSessionResponse getByStreamKey(String streamKey);

    Page<LiveSessionResponse> getAll(Pageable pageable);

    Page<LiveSessionResponse> getByStatus(LiveSessionStatus status, Pageable pageable);

    List<LiveSessionResponse> getScheduledBetween(LocalDateTime start, LocalDateTime end);

    LiveSessionResponse update(String id, LiveSessionRequest request);

    void delete(String id);

    LiveSessionResponse startStream(String id);

    LiveSessionResponse endStream(String id);

    boolean canAccess(String sessionId, boolean isInternalUser);

    /** Vérifie l'accès en tenant compte de la section (INTERNAL = même section uniquement). */
    boolean canAccess(String sessionId, boolean isInternalUser, Section viewerSection);

    LiveCommentResponse addComment(String sessionId, String userId, LiveCommentRequest request);

    List<LiveCommentResponse> getComments(String sessionId);

    /** Sessions publiques (EXTERNAL) pour accès non authentifié. */
    Page<LiveSessionResponse> getPublicSessions(LiveSessionStatus status, Pageable pageable);

    /** Sessions de ma section (INTERNAL, réservées HOMME ou FEMME). */
    Page<LiveSessionResponse> getSessionsForMySection(LiveSessionStatus status, Section section, Pageable pageable);

    /** Détail d'une session publique si accessType = EXTERNAL. */
    java.util.Optional<LiveSessionResponse> getPublicSessionById(String id);

    /** Détail pour un viewer : EXTERNAL toujours autorisé ; INTERNAL uniquement si viewerSection == session.section. */
    java.util.Optional<LiveSessionResponse> getSessionByIdForViewer(String id, Section viewerSection);

    /** Ajout d'un commentaire par le public (sans compte). authorDisplayName requis. */
    java.util.Optional<LiveCommentResponse> addPublicComment(String sessionId, LiveCommentRequest request);
}
