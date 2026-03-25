package com.darquran.application.service.impl;

import com.darquran.application.mapper.live.LiveCommentMapper;
import com.darquran.application.mapper.live.LiveSessionMapper;
import com.darquran.application.service.UserNotificationService;
import com.darquran.domain.model.entities.live.LiveSession;
import com.darquran.domain.model.entities.users.Teacher;
import com.darquran.domain.model.enums.Section;
import com.darquran.domain.model.enums.live.LiveAccessType;
import com.darquran.domain.model.enums.live.LiveSessionStatus;
import com.darquran.domain.repository.LiveCommentRepository;
import com.darquran.domain.repository.LiveSessionRepository;
import com.darquran.domain.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LiveSessionServiceImplTest {

    @Mock
    private LiveSessionRepository liveSessionRepository;

    @Mock
    private LiveCommentRepository liveCommentRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private LiveSessionMapper liveSessionMapper;

    @Mock
    private LiveCommentMapper liveCommentMapper;

    @Mock
    private UserNotificationService userNotificationService;

    @InjectMocks
    private LiveSessionServiceImpl service;

    @BeforeEach
    void injectUrls() {
        ReflectionTestUtils.setField(service, "hlsBaseUrl", "http://hls.test/hls");
        ReflectionTestUtils.setField(service, "rtmpServerUrl", "rtmp://rtmp.test/live");
    }

    @Test
    @DisplayName("canAccess : session EXTERNAL toujours accessible")
    void canAccess_external_alwaysTrue() {
        LiveSession session = LiveSession.builder()
                .id("s1")
                .title("T")
                .accessType(LiveAccessType.EXTERNAL)
                .status(LiveSessionStatus.LIVE)
                .streamKey("k")
                .scheduledStartAt(java.time.LocalDateTime.now())
                .build();
        when(liveSessionRepository.findById("s1")).thenReturn(Optional.of(session));

        assertThat(service.canAccess("s1", false, null)).isTrue();
        assertThat(service.canAccess("s1", false, Section.FEMME)).isTrue();
    }

    @Test
    @DisplayName("canAccess : session INTERNAL avec section → même section uniquement")
    void canAccess_internal_section_match() {
        LiveSession session = LiveSession.builder()
                .id("s1")
                .title("T")
                .accessType(LiveAccessType.INTERNAL)
                .section(Section.FEMME)
                .status(LiveSessionStatus.LIVE)
                .streamKey("k")
                .scheduledStartAt(java.time.LocalDateTime.now())
                .build();
        when(liveSessionRepository.findById("s1")).thenReturn(Optional.of(session));

        assertThat(service.canAccess("s1", true, Section.FEMME)).isTrue();
        assertThat(service.canAccess("s1", true, Section.HOMME)).isFalse();
        assertThat(service.canAccess("s1", true, null)).isFalse();
    }

    @Test
    @DisplayName("getSessionByIdForViewer : INTERNAL FEMME + viewer HOMME → vide")
    void getSessionByIdForViewer_wrongSection_empty() {
        LiveSession session = LiveSession.builder()
                .id("s1")
                .title("T")
                .accessType(LiveAccessType.INTERNAL)
                .section(Section.FEMME)
                .status(LiveSessionStatus.LIVE)
                .streamKey("k")
                .scheduledStartAt(java.time.LocalDateTime.now())
                .build();
        when(liveSessionRepository.findById("s1")).thenReturn(Optional.of(session));

        assertThat(service.getSessionByIdForViewer("s1", Section.HOMME)).isEmpty();
    }

    @Test
    @DisplayName("startStream depuis SCHEDULED déclenche dispatchLiveStarted")
    void startStream_fromScheduled_dispatchesNotification() {
        Teacher teacher = new Teacher();
        teacher.setId("t1");
        LiveSession entity = LiveSession.builder()
                .id("sid")
                .title("Titre")
                .status(LiveSessionStatus.SCHEDULED)
                .accessType(LiveAccessType.INTERNAL)
                .streamKey("k")
                .scheduledStartAt(java.time.LocalDateTime.now())
                .user(teacher)
                .build();
        when(liveSessionRepository.findById("sid")).thenReturn(Optional.of(entity));
        when(liveSessionRepository.save(any(LiveSession.class))).thenAnswer(inv -> inv.getArgument(0));
        when(liveSessionMapper.toResponse(any(LiveSession.class))).thenReturn(
                com.darquran.application.dto.live.LiveSessionResponse.builder()
                        .id("sid")
                        .streamKey("k")
                        .status(LiveSessionStatus.LIVE)
                        .accessType(LiveAccessType.INTERNAL)
                        .scheduledStartAt(entity.getScheduledStartAt())
                        .build());

        service.startStream("sid");

        verify(userNotificationService).dispatchLiveStarted(any(LiveSession.class));
    }
}
