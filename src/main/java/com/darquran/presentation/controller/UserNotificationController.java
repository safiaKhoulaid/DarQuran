package com.darquran.presentation.controller;

import com.darquran.application.dto.notification.UnreadCountResponse;
import com.darquran.application.dto.notification.UserNotificationPageResponse;
import com.darquran.application.service.UserNotificationService;
import com.darquran.domain.model.entities.users.User;
import com.darquran.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class UserNotificationController {

    private final UserNotificationService userNotificationService;
    private final UserRepository userRepository;

    private User requireUser(Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        return userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
    }

    @GetMapping
    public ResponseEntity<UserNotificationPageResponse> list(
            Authentication auth,
            @PageableDefault(size = 20) Pageable pageable) {
        User user = requireUser(auth);
        return ResponseEntity.ok(
                UserNotificationPageResponse.from(userNotificationService.listForUser(user.getId(), pageable)));
    }

    @GetMapping("/unread-count")
    public ResponseEntity<UnreadCountResponse> unreadCount(Authentication auth) {
        User user = requireUser(auth);
        return ResponseEntity.ok(new UnreadCountResponse(userNotificationService.countUnread(user.getId())));
    }

    @PostMapping("/{id}/read")
    public ResponseEntity<Void> markRead(Authentication auth, @PathVariable("id") String id) {
        User user = requireUser(auth);
        userNotificationService.markRead(user.getId(), id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/read-all")
    public ResponseEntity<Void> markAllRead(Authentication auth) {
        User user = requireUser(auth);
        userNotificationService.markAllRead(user.getId());
        return ResponseEntity.noContent().build();
    }
}
