package com.darquran.application.service.impl;

import com.darquran.application.dto.live.LiveCommentRequest;
import com.darquran.application.dto.live.LiveCommentResponse;
import com.darquran.application.dto.live.LiveSessionRequest;
import com.darquran.application.dto.live.LiveSessionResponse;
import com.darquran.application.mapper.live.LiveCommentMapper;
import com.darquran.application.mapper.live.LiveSessionMapper;
import com.darquran.application.service.LiveSessionService;
import com.darquran.application.service.UserNotificationService;
import com.darquran.domain.model.entities.live.LiveComment;
import com.darquran.domain.model.entities.live.LiveSession;
import com.darquran.domain.model.entities.users.User;
import com.darquran.domain.model.enums.Role;
import com.darquran.domain.model.enums.Section;
import com.darquran.domain.model.enums.live.LiveAccessType;
import com.darquran.domain.model.enums.live.LiveSessionStatus;
import com.darquran.domain.repository.LiveCommentRepository;
import com.darquran.domain.repository.LiveSessionRepository;
import com.darquran.domain.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class LiveSessionServiceImpl implements LiveSessionService {

    private final LiveSessionRepository liveSessionRepository;
    private final LiveCommentRepository liveCommentRepository;
    private final UserRepository userRepository;
    private final LiveSessionMapper liveSessionMapper;
    private final LiveCommentMapper liveCommentMapper;
    private final UserNotificationService userNotificationService;

    @Value("${app.live.hls-base-url:http://localhost:8081/hls}")
    private String hlsBaseUrl;

    @Value("${app.live.rtmp-server-url:rtmp://localhost:1935/live}")
    private String rtmpServerUrl;

    /** Remplit les URLs de streaming (RTMP pour OBS, HLS déjà dans l'entité) dans la réponse. */
    private LiveSessionResponse withStreamingUrls(LiveSessionResponse response) {
        if (response != null) {
            response.setRtmpIngestUrl(rtmpServerUrl);
        }
        return response;
    }

    @Override
    @Transactional
    public LiveSessionResponse create(String currentUserId, LiveSessionRequest request) {
        // Résoudre l'utilisateur animateur : d'abord l'utilisateur connecté, sinon request.userId (Admin/SuperAdmin)
        User user = userRepository.findById(currentUserId).orElse(null);
        if (user == null && request.getUserId() != null && !request.getUserId().isBlank()) {
            user = userRepository.findById(request.getUserId())
                    .orElseThrow(() -> new EntityNotFoundException("User not found: " + request.getUserId()));
        }
        if (user == null) {
            throw new IllegalArgumentException("Aucun utilisateur associé : utilisateur connecté introuvable et aucun userId n'est fourni.");
        }
        if (liveSessionRepository.existsByStreamKey(request.getStreamKey())) {
            throw new IllegalArgumentException("Stream key already in use: " + request.getStreamKey());
        }
        LiveSession entity = liveSessionMapper.toEntity(request);
        entity.setHlsPlaybackUrl(hlsBaseUrl + "/" + request.getStreamKey() + ".m3u8");
        entity.setUser(user);
        entity.setSection(user.getSection());
        if (user.getRole() == Role.ENSEIGNANT) {
            entity.setAccessType(LiveAccessType.INTERNAL);
        }
        return withStreamingUrls(liveSessionMapper.toResponse(liveSessionRepository.save(entity)));
    }

    @Override
    public LiveSessionResponse getById(String id) {
        return liveSessionRepository.findById(id)
                .map(liveSessionMapper::toResponse)
                .map(this::withStreamingUrls)
                .orElseThrow(() -> new EntityNotFoundException("Live session not found: " + id));
    }

    @Override
    public LiveSessionResponse getByStreamKey(String streamKey) {
        return liveSessionRepository.findByStreamKey(streamKey)
                .map(liveSessionMapper::toResponse)
                .map(this::withStreamingUrls)
                .orElseThrow(() -> new EntityNotFoundException("Live session not found for stream: " + streamKey));
    }

    @Override
    public Page<LiveSessionResponse> getAll(Pageable pageable) {
        return liveSessionRepository.findAll(pageable)
                .map(liveSessionMapper::toResponse)
                .map(this::withStreamingUrls);
    }

    @Override
    public Page<LiveSessionResponse> getByStatus(LiveSessionStatus status, Pageable pageable) {
        return liveSessionRepository.findByStatus(status, pageable)
                .map(liveSessionMapper::toResponse)
                .map(this::withStreamingUrls);
    }

    @Override
    public List<LiveSessionResponse> getScheduledBetween(LocalDateTime start, LocalDateTime end) {
        return liveSessionRepository.findByScheduledStartAtBetweenAndStatus(start, end, LiveSessionStatus.SCHEDULED)
                .stream()
                .map(liveSessionMapper::toResponse)
                .map(this::withStreamingUrls)
                .toList();
    }

    @Override
    @Transactional
    public LiveSessionResponse update(String id, LiveSessionRequest request) {
        LiveSession entity = liveSessionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Live session not found: " + id));
        if (!entity.getStreamKey().equals(request.getStreamKey()) && liveSessionRepository.existsByStreamKey(request.getStreamKey())) {
            throw new IllegalArgumentException("Stream key already in use: " + request.getStreamKey());
        }
        liveSessionMapper.updateEntityFromRequest(request, entity);
        // Résoudre l'utilisateur animateur via UserRepository si userId est fourni
        if (request.getUserId() != null && !request.getUserId().isBlank()) {
            User user = userRepository.findById(request.getUserId())
                    .orElseThrow(() -> new EntityNotFoundException("User not found: " + request.getUserId()));
            entity.setUser(user);
            entity.setSection(user.getSection());
        }
        entity.setHlsPlaybackUrl(hlsBaseUrl + "/" + request.getStreamKey() + ".m3u8");
        return withStreamingUrls(liveSessionMapper.toResponse(liveSessionRepository.save(entity)));
    }

    @Override
    @Transactional
    public void delete(String id) {
        if (!liveSessionRepository.existsById(id)) {
            throw new EntityNotFoundException("Live session not found: " + id);
        }
        liveSessionRepository.deleteById(id);
    }

    @Override
    @Transactional
    public LiveSessionResponse startStream(String id) {
        LiveSession entity = liveSessionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Live session not found: " + id));
        boolean wasScheduled = entity.getStatus() == LiveSessionStatus.SCHEDULED;
        entity.setStatus(LiveSessionStatus.LIVE);
        entity.setStartedAt(LocalDateTime.now());
        LiveSession saved = liveSessionRepository.save(entity);

        if (wasScheduled) {
            userNotificationService.dispatchLiveStarted(saved);
        }

        return withStreamingUrls(liveSessionMapper.toResponse(saved));
    }

    @Override
    @Transactional
    public LiveSessionResponse endStream(String id) {
        LiveSession entity = liveSessionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Live session not found: " + id));
        entity.setStatus(LiveSessionStatus.ENDED);
        entity.setEndedAt(LocalDateTime.now());
        return withStreamingUrls(liveSessionMapper.toResponse(liveSessionRepository.save(entity)));
    }

    @Override
    public boolean canAccess(String sessionId, boolean isInternalUser) {
        return canAccess(sessionId, isInternalUser, null);
    }

    @Override
    public boolean canAccess(String sessionId, boolean isInternalUser, Section viewerSection) {
        LiveSession session = liveSessionRepository.findById(sessionId)
                .orElseThrow(() -> new EntityNotFoundException("Live session not found: " + sessionId));
        if (session.getAccessType() == LiveAccessType.EXTERNAL) {
            return true;
        }
        if (session.getAccessType() == LiveAccessType.INTERNAL && session.getSection() != null) {
            return viewerSection != null && viewerSection == session.getSection();
        }
        return isInternalUser;
    }

    @Override
    @Transactional
    public LiveCommentResponse addComment(String sessionId, String userId, LiveCommentRequest request) {
        LiveSession session = liveSessionRepository.findById(sessionId)
                .orElseThrow(() -> new EntityNotFoundException("Live session not found: " + sessionId));
        LiveComment comment = liveCommentMapper.toEntity(request);
        comment.setLiveSession(session);
        if (userId != null && !userId.isBlank()) {
            comment.setAuthor(userRepository.findById(userId).orElse(null));
        }
        if (comment.getAuthor() == null && request.getAuthorDisplayName() != null) {
            comment.setAuthorDisplayName(request.getAuthorDisplayName());
        }
        comment = liveCommentRepository.save(comment);
        return liveCommentMapper.toResponse(comment);
    }

    @Override
    public List<LiveCommentResponse> getComments(String sessionId) {
        return liveCommentRepository.findByLiveSessionIdOrderByCreatedAtAsc(sessionId)
                .stream()
                .map(liveCommentMapper::toResponse)
                .toList();
    }

    @Override
    public Page<LiveSessionResponse> getPublicSessions(LiveSessionStatus status, Pageable pageable) {
        return liveSessionRepository.findByStatusAndAccessType(
                status,
                LiveAccessType.EXTERNAL,
                pageable
        ).map(liveSessionMapper::toResponse).map(this::withStreamingUrls);
    }

    @Override
    public Page<LiveSessionResponse> getSessionsForMySection(LiveSessionStatus status, Section section, Pageable pageable) {
        if (section == null) {
            return liveSessionRepository.findByStatus(status, pageable)
                    .map(liveSessionMapper::toResponse).map(this::withStreamingUrls);
        }
        return liveSessionRepository.findByStatusAndAccessTypeAndSection(
                status, LiveAccessType.INTERNAL, section, pageable
        ).map(liveSessionMapper::toResponse).map(this::withStreamingUrls);
    }

    @Override
    public java.util.Optional<LiveSessionResponse> getPublicSessionById(String id) {
        return liveSessionRepository.findById(id)
                .filter(s -> s.getAccessType() == LiveAccessType.EXTERNAL)
                .map(liveSessionMapper::toResponse)
                .map(this::withStreamingUrls);
    }

    @Override
    public java.util.Optional<LiveSessionResponse> getSessionByIdForViewer(String id, Section viewerSection) {
        return liveSessionRepository.findById(id)
                .filter(s -> {
                    if (s.getAccessType() == LiveAccessType.EXTERNAL) return true;
                    if (s.getAccessType() == LiveAccessType.INTERNAL && s.getSection() != null) {
                        return viewerSection != null && viewerSection == s.getSection();
                    }
                    return viewerSection != null;
                })
                .map(liveSessionMapper::toResponse)
                .map(this::withStreamingUrls);
    }

    @Override
    @Transactional
    public java.util.Optional<LiveCommentResponse> addPublicComment(String sessionId, LiveCommentRequest request) {
        LiveSession session = liveSessionRepository.findById(sessionId)
                .orElse(null);
        if (session == null || session.getAccessType() != LiveAccessType.EXTERNAL) {
            return java.util.Optional.empty();
        }
        if (request.getAuthorDisplayName() == null || request.getAuthorDisplayName().isBlank()) {
            throw new IllegalArgumentException("authorDisplayName is required for public comments");
        }
        LiveComment comment = liveCommentMapper.toEntity(request);
        comment.setLiveSession(session);
        comment.setAuthorDisplayName(request.getAuthorDisplayName());
        comment = liveCommentRepository.save(comment);
        return java.util.Optional.of(liveCommentMapper.toResponse(comment));
    }
}
