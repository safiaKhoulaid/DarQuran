package com.darquran.application.service.impl;

import com.darquran.application.dto.notification.UserNotificationResponse;
import com.darquran.application.service.EmailService;
import com.darquran.application.service.UserNotificationService;
import com.darquran.domain.model.entities.course.Course;
import com.darquran.domain.model.entities.live.LiveSession;
import com.darquran.domain.model.entities.notification.UserNotification;
import com.darquran.domain.model.entities.users.User;
import com.darquran.domain.model.enums.Role;
import com.darquran.domain.model.enums.Section;
import com.darquran.domain.model.enums.notification.UserNotificationType;
import com.darquran.domain.repository.UserNotificationRepository;
import com.darquran.domain.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserNotificationServiceImpl implements UserNotificationService {

    private final UserNotificationRepository userNotificationRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

    @Value("${app.front-url:}")
    private String frontUrl;

    @Override
    @Transactional(readOnly = true)
    public Page<UserNotificationResponse> listForUser(String userId, Pageable pageable) {
        return userNotificationRepository.findByUser_IdOrderByCreatedAtDesc(userId, pageable)
                .map(this::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public long countUnread(String userId) {
        return userNotificationRepository.countByUser_IdAndReadFalse(userId);
    }

    @Override
    @Transactional
    public void markRead(String userId, String notificationId) {
        UserNotification n = userNotificationRepository.findByIdAndUser_Id(notificationId, userId)
                .orElseThrow(() -> new EntityNotFoundException("Notification not found: " + notificationId));
        n.setRead(true);
        userNotificationRepository.save(n);
    }

    @Override
    @Transactional
    public void markAllRead(String userId) {
        List<UserNotification> pending = userNotificationRepository.findByUser_IdAndReadFalse(userId);
        pending.forEach(n -> n.setRead(true));
        userNotificationRepository.saveAll(pending);
    }

    @Override
    @Transactional
    public void dispatchLiveStarted(LiveSession session) {
        String subject = "DarQuran - Un live a commencé : " + session.getTitle();
        String linkLine = (frontUrl != null && !frontUrl.isBlank())
                ? "\n\nRegardez le live ici : " + frontUrl
                : "";
        String body = """
                Bonjour,

                Le live « %s » a commencé. Connectez-vous à la plateforme pour le regarder.%s

                — Équipe DarQuran
                """.formatted(session.getTitle(), linkLine);

        String linkUrl = (frontUrl != null && !frontUrl.isBlank())
                ? frontUrl.replaceAll("/+$", "") + "/live/" + session.getId()
                : "/live/" + session.getId();

        User launcher = session.getUser() != null
                ? userRepository.findById(session.getUser().getId()).orElse(session.getUser())
                : null;
        Role launcherRole = launcher != null ? launcher.getRole() : null;
        Section launcherSection = session.getSection();
        boolean notifyAllUsers = launcherRole == Role.SUPER_ADMIN;

        List<User> recipients = userRepository.findAll().stream()
                .filter(u -> {
                    if (notifyAllUsers) return true;
                    if (launcherSection == null) return false;
                    return u.getSection() != null && u.getSection() == launcherSection;
                })
                .toList();

        List<UserNotification> toSave = new ArrayList<>();
        for (User u : recipients) {
            toSave.add(UserNotification.builder()
                    .user(u)
                    .type(UserNotificationType.LIVE_STARTED)
                    .title(subject.replace("DarQuran - ", ""))
                    .body(body)
                    .linkUrl(linkUrl)
                    .read(false)
                    .build());
            if (u.getEmail() != null && !u.getEmail().isBlank()) {
                emailService.sendEmail(u.getEmail(), subject, body);
            }
        }
        userNotificationRepository.saveAll(toSave);
        log.info(
                "Notifications live (app + email) pour {} destinataire(s), session {} (launcherRole={}, section={})",
                recipients.size(),
                session.getId(),
                launcherRole,
                launcherSection
        );
    }

    @Override
    @Transactional
    public void dispatchCoursePublished(Course course) {
        String subject = "DarQuran - Nouveau cours publié : " + course.getTitle();
        String linkLine = (frontUrl != null && !frontUrl.isBlank())
                ? "\n\nConsultez-le ici : " + frontUrl + "/courses"
                : "";
        String body = """
                Bonjour,

                Un nouveau cours est disponible : « %s ».%s

                — Équipe DarQuran
                """.formatted(course.getTitle(), linkLine);

        String linkUrl = (frontUrl != null && !frontUrl.isBlank())
                ? frontUrl.replaceAll("/+$", "") + "/courses"
                : "/courses";

        List<User> recipients = userRepository.findAll();

        List<UserNotification> toSave = new ArrayList<>();
        for (User u : recipients) {
            toSave.add(UserNotification.builder()
                    .user(u)
                    .type(UserNotificationType.COURSE_PUBLISHED)
                    .title(subject.replace("DarQuran - ", ""))
                    .body(body)
                    .linkUrl(linkUrl)
                    .read(false)
                    .build());
            if (u.getEmail() != null && !u.getEmail().isBlank()) {
                emailService.sendEmail(u.getEmail(), subject, body);
            }
        }
        userNotificationRepository.saveAll(toSave);
        log.info("Notifications cours publié (app + email) pour {} utilisateur(s), cours {}", recipients.size(), course.getId());
    }

    private UserNotificationResponse toResponse(UserNotification n) {
        return UserNotificationResponse.builder()
                .id(n.getId())
                .type(n.getType())
                .title(n.getTitle())
                .body(n.getBody())
                .linkUrl(n.getLinkUrl())
                .read(n.isRead())
                .createdAt(n.getCreatedAt())
                .build();
    }
}
