package com.darquran.application.service.impl;

import com.darquran.application.dto.notification.UserNotificationResponse;
import com.darquran.application.service.EmailService;
import com.darquran.domain.model.entities.live.LiveSession;
import com.darquran.domain.model.entities.notification.UserNotification;
import com.darquran.domain.model.entities.users.Student;
import com.darquran.domain.model.entities.users.Teacher;
import com.darquran.domain.model.enums.Role;
import com.darquran.domain.model.enums.Section;
import com.darquran.domain.model.enums.live.LiveAccessType;
import com.darquran.domain.model.enums.live.LiveSessionStatus;
import com.darquran.domain.model.enums.notification.UserNotificationType;
import com.darquran.domain.repository.UserNotificationRepository;
import com.darquran.domain.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserNotificationServiceImplTest {

    @Mock
    private UserNotificationRepository userNotificationRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private UserNotificationServiceImpl service;

    @BeforeEach
    void setFrontUrl() {
        ReflectionTestUtils.setField(service, "frontUrl", "http://localhost:4200");
    }

    @Test
    @DisplayName("listForUser mappe les entités vers des DTO avec le champ read")
    void listForUser_returnsMappedPage() {
        String userId = "user-1";
        Pageable pageable = PageRequest.of(0, 20);
        UserNotification entity = UserNotification.builder()
                .id("n1")
                .type(UserNotificationType.LIVE_STARTED)
                .title("Live")
                .body("Corps")
                .linkUrl("/live/1")
                .read(false)
                .createdAt(LocalDateTime.parse("2025-01-01T10:00:00"))
                .build();
        Page<UserNotification> page = new PageImpl<>(List.of(entity), pageable, 1);
        when(userNotificationRepository.findByUser_IdOrderByCreatedAtDesc(userId, pageable)).thenReturn(page);

        Page<UserNotificationResponse> result = service.listForUser(userId, pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getId()).isEqualTo("n1");
        assertThat(result.getContent().get(0).isRead()).isFalse();
        assertThat(result.getContent().get(0).getType()).isEqualTo(UserNotificationType.LIVE_STARTED);
    }

    @Test
    @DisplayName("countUnread délègue au repository")
    void countUnread_delegates() {
        when(userNotificationRepository.countByUser_IdAndReadFalse("u1")).thenReturn(3L);
        assertThat(service.countUnread("u1")).isEqualTo(3L);
    }

    @Test
    @DisplayName("markRead lève EntityNotFoundException si la notification n'appartient pas à l'utilisateur")
    void markRead_notFound_throws() {
        when(userNotificationRepository.findByIdAndUser_Id("n1", "u1")).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.markRead("u1", "n1"))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("markRead marque la notification comme lue")
    void markRead_saves() {
        UserNotification n = UserNotification.builder()
                .id("n1")
                .read(false)
                .type(UserNotificationType.LIVE_STARTED)
                .title("t")
                .body("b")
                .createdAt(LocalDateTime.now())
                .build();
        when(userNotificationRepository.findByIdAndUser_Id("n1", "u1")).thenReturn(Optional.of(n));

        service.markRead("u1", "n1");

        assertThat(n.isRead()).isTrue();
        verify(userNotificationRepository).save(n);
    }

    @Test
    @DisplayName("markAllRead met à jour toutes les notifications non lues")
    void markAllRead_updatesAll() {
        UserNotification a = UserNotification.builder()
                .id("a")
                .read(false)
                .type(UserNotificationType.LIVE_STARTED)
                .title("t")
                .body("b")
                .createdAt(LocalDateTime.now())
                .build();
        when(userNotificationRepository.findByUser_IdAndReadFalse("u1")).thenReturn(List.of(a));

        service.markAllRead("u1");

        assertThat(a.isRead()).isTrue();
        verify(userNotificationRepository).saveAll(List.of(a));
    }

    @Test
    @DisplayName("dispatchLiveStarted enseignant FEMME filtre les destinataires FEMME")
    void dispatchLiveStarted_teacherSection_filtersRecipients() {
        Teacher teacher = new Teacher();
        teacher.setId("t1");
        teacher.setRole(Role.ENSEIGNANT);
        teacher.setSection(Section.FEMME);

        Student femme = new Student();
        femme.setId("s1");
        femme.setSection(Section.FEMME);
        femme.setEmail("f@example.com");

        Student homme = new Student();
        homme.setId("s2");
        homme.setSection(Section.HOMME);
        homme.setEmail("h@example.com");

        when(userRepository.findById("t1")).thenReturn(Optional.of(teacher));
        when(userRepository.findAll()).thenReturn(List.of(femme, homme));

        LiveSession session = LiveSession.builder()
                .id("live-1")
                .title("Cours")
                .streamKey("sk")
                .accessType(LiveAccessType.INTERNAL)
                .status(LiveSessionStatus.LIVE)
                .section(Section.FEMME)
                .scheduledStartAt(LocalDateTime.now())
                .user(teacher)
                .build();

        service.dispatchLiveStarted(session);

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<UserNotification>> captor = ArgumentCaptor.forClass(List.class);
        verify(userNotificationRepository).saveAll(captor.capture());
        assertThat(captor.getValue()).hasSize(1);
        assertThat(captor.getValue().get(0).getUser().getId()).isEqualTo("s1");

        verify(emailService).sendEmail(eq("f@example.com"), any(), any());
        verify(emailService, never()).sendEmail(eq("h@example.com"), any(), any());
    }

    @Test
    @DisplayName("dispatchLiveStarted SUPER_ADMIN notifie tous les utilisateurs")
    void dispatchLiveStarted_superAdmin_notifiesAll() {
        Teacher admin = new Teacher();
        admin.setId("admin-1");
        admin.setRole(Role.SUPER_ADMIN);
        admin.setSection(Section.HOMME);

        Student s1 = new Student();
        s1.setId("a");
        s1.setSection(Section.FEMME);
        Student s2 = new Student();
        s2.setId("b");
        s2.setSection(Section.HOMME);

        when(userRepository.findById("admin-1")).thenReturn(Optional.of(admin));
        when(userRepository.findAll()).thenReturn(List.of(s1, s2));

        LiveSession session = LiveSession.builder()
                .id("live-2")
                .title("Live admin")
                .streamKey("sk2")
                .accessType(LiveAccessType.EXTERNAL)
                .status(LiveSessionStatus.LIVE)
                .section(Section.HOMME)
                .scheduledStartAt(LocalDateTime.now())
                .user(admin)
                .build();

        service.dispatchLiveStarted(session);

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<UserNotification>> captor = ArgumentCaptor.forClass(List.class);
        verify(userNotificationRepository).saveAll(captor.capture());
        assertThat(captor.getValue()).hasSize(2);
    }
}
