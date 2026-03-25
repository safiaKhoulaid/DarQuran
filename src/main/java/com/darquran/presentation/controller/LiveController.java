package com.darquran.presentation.controller;

import com.darquran.application.dto.live.LiveCommentRequest;
import com.darquran.application.dto.live.LiveCommentResponse;
import com.darquran.application.dto.live.LiveSessionRequest;
import com.darquran.application.dto.live.LiveSessionResponse;
import com.darquran.application.service.LiveSessionService;
import com.darquran.domain.model.entities.users.User;
import com.darquran.domain.model.enums.Role;
import com.darquran.domain.model.enums.Section;
import com.darquran.domain.model.enums.live.LiveSessionStatus;
import com.darquran.domain.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/live")
@RequiredArgsConstructor
public class LiveController {

    private final LiveSessionService liveSessionService;
    private final UserRepository userRepository;

    private boolean isInternalUser(Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) return false;
        return userRepository.findByEmail(auth.getName())
                .map(u -> u.getRole() == Role.ENSEIGNANT || u.getRole() == Role.ELEVE
                        || u.getRole() == Role.ADMIN_SECTION || u.getRole() == Role.SUPER_ADMIN)
                .orElse(false);
    }

    private String currentUserId(Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) return null;
        return userRepository.findByEmail(auth.getName()).map(u -> u.getId()).orElse(null);
    }

    /**
     * Droits pour lancer / modifier / démarrer / arrêter un live :
     * - SuperAdmin : peut tout faire (créer, modifier, supprimer, start, end).
     * - Admin (ADMIN_SECTION) : idem ; pour créer, peut fournir userId (animateur) dans la requête.
     * - Enseignant (ENSEIGNANT) : idem ; à la création il est le professeur de la session.
     */
    private boolean canLaunchLive(Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) return false;
        return userRepository.findByEmail(auth.getName())
                .map(u -> u.getRole() == Role.SUPER_ADMIN
                        || u.getRole() == Role.ADMIN_SECTION
                        || u.getRole() == Role.ENSEIGNANT)
                .orElse(false);
    }

    private Role currentUserRole(Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) return null;
        return userRepository.findByEmail(auth.getName()).map(u -> u.getRole()).orElse(null);
    }

    /* ===== Endpoints internes (authentifiés) ===== */

    @PostMapping("/sessions")
    public ResponseEntity<LiveSessionResponse> create(
            Authentication auth,
            @Valid @RequestBody LiveSessionRequest request) {
        if (!canLaunchLive(auth)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        String userId = currentUserId(auth);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(liveSessionService.create(userId, request));
    }

    @GetMapping("/sessions")
    public ResponseEntity<Page<LiveSessionResponse>> getAll(
            @PageableDefault(size = 20, sort = "scheduledStartAt") Pageable pageable) {
        return ResponseEntity.ok(liveSessionService.getAll(pageable));
    }

    @GetMapping("/sessions/status/{status}")
    public ResponseEntity<Page<LiveSessionResponse>> getByStatus(
            @PathVariable("status") LiveSessionStatus status,
            @PageableDefault(size = 20, sort = "scheduledStartAt") Pageable pageable) {
        return ResponseEntity.ok(liveSessionService.getByStatus(status, pageable));
    }

    @GetMapping("/sessions/scheduled")
    public ResponseEntity<List<LiveSessionResponse>> getScheduled(
            @RequestParam("start") LocalDateTime start,
            @RequestParam("end") LocalDateTime end) {
        return ResponseEntity.ok(liveSessionService.getScheduledBetween(start, end));
    }

    /** Sessions de ma section (INTERNAL, pour élèves/profs de la même section). */
    @GetMapping("/sessions/my-section")
    public ResponseEntity<Page<LiveSessionResponse>> getSessionsForMySection(
            Authentication auth,
            @RequestParam(value = "status", defaultValue = "LIVE") LiveSessionStatus status,
            @PageableDefault(size = 20, sort = "scheduledStartAt") Pageable pageable) {
        if (auth == null || !auth.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Section section = userRepository.findByEmail(auth.getName()).map(User::getSection).orElse(null);
        return ResponseEntity.ok(liveSessionService.getSessionsForMySection(status, section, pageable));
    }

    @GetMapping("/sessions/{id}")
    public ResponseEntity<LiveSessionResponse> getById(Authentication auth, @PathVariable("id") String id) {
        Section viewerSection = auth != null && auth.isAuthenticated()
                ? userRepository.findByEmail(auth.getName()).map(User::getSection).orElse(null)
                : null;
        if (!liveSessionService.canAccess(id, isInternalUser(auth), viewerSection)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(liveSessionService.getById(id));
    }

    @PutMapping("/sessions/{id}")
    public ResponseEntity<LiveSessionResponse> update(
            Authentication auth,
            @PathVariable("id") String id,
            @Valid @RequestBody LiveSessionRequest request) {
        if (!canLaunchLive(auth)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(liveSessionService.update(id, request));
    }

    @DeleteMapping("/sessions/{id}")
    public ResponseEntity<Void> delete(Authentication auth, @PathVariable("id") String id) {
        if (!canLaunchLive(auth)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        liveSessionService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/sessions/{id}/start")
    public ResponseEntity<LiveSessionResponse> startStream(Authentication auth, @PathVariable("id") String id) {
        if (!canLaunchLive(auth)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(liveSessionService.startStream(id));
    }

    @PostMapping("/sessions/{id}/end")
    public ResponseEntity<LiveSessionResponse> endStream(Authentication auth, @PathVariable("id") String id) {
        if (!canLaunchLive(auth)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(liveSessionService.endStream(id));
    }

    @GetMapping("/sessions/{id}/comments")
    public ResponseEntity<List<LiveCommentResponse>> getComments(Authentication auth, @PathVariable("id") String id) {
        Section viewerSection = auth != null && auth.isAuthenticated()
                ? userRepository.findByEmail(auth.getName()).map(User::getSection).orElse(null)
                : null;
        if (!liveSessionService.canAccess(id, isInternalUser(auth), viewerSection)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(liveSessionService.getComments(id));
    }

    @PostMapping("/sessions/{id}/comments")
    public ResponseEntity<LiveCommentResponse> addComment(
            Authentication auth,
            @PathVariable("id") String id,
            @Valid @RequestBody LiveCommentRequest request) {
        Section viewerSection = auth != null && auth.isAuthenticated()
                ? userRepository.findByEmail(auth.getName()).map(User::getSection).orElse(null)
                : null;
        if (!liveSessionService.canAccess(id, isInternalUser(auth), viewerSection)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        String userId = currentUserId(auth);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(liveSessionService.addComment(id, userId, request));
    }

    /* ===== Endpoints publics (accès externe, non authentifié) ===== */

    @GetMapping("/public/sessions")
    public ResponseEntity<Page<LiveSessionResponse>> getPublicSessions(
            @RequestParam(value = "status", defaultValue = "LIVE") LiveSessionStatus status,
            @PageableDefault(size = 20, sort = "scheduledStartAt") Pageable pageable) {
        return ResponseEntity.ok(liveSessionService.getPublicSessions(status, pageable));
    }

    @GetMapping("/public/sessions/{id}")
    public ResponseEntity<LiveSessionResponse> getPublicSessionById(
            Authentication auth,
            @PathVariable("id") String id) {
        Section viewerSection = null;
        if (auth != null && auth.isAuthenticated()) {
            viewerSection = userRepository.findByEmail(auth.getName()).map(User::getSection).orElse(null);
        }
        return liveSessionService.getSessionByIdForViewer(id, viewerSection)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.FORBIDDEN).build());
    }

    @GetMapping("/public/sessions/{id}/comments")
    public ResponseEntity<List<LiveCommentResponse>> getPublicSessionComments(@PathVariable("id") String id) {
        if (liveSessionService.getPublicSessionById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(liveSessionService.getComments(id));
    }

    @PostMapping("/public/sessions/{id}/comments")
    public ResponseEntity<LiveCommentResponse> addPublicComment(
            @PathVariable("id") String id,
            @Valid @RequestBody LiveCommentRequest request) {
        return liveSessionService.addPublicComment(id, request)
                .map(c -> ResponseEntity.status(HttpStatus.CREATED).body(c))
                .orElse(ResponseEntity.status(HttpStatus.FORBIDDEN).build());
    }
}
