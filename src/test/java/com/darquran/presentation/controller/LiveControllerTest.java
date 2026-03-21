package com.darquran.presentation.controller;

import com.darquran.application.dto.live.LiveSessionResponse;
import com.darquran.application.service.LiveSessionService;
import com.darquran.domain.model.entities.users.Teacher;
import com.darquran.domain.model.enums.Role;
import com.darquran.domain.model.enums.live.LiveAccessType;
import com.darquran.domain.model.enums.live.LiveSessionStatus;
import com.darquran.domain.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LiveController.class)
class LiveControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private LiveSessionService liveSessionService;

    @MockBean
    private UserRepository userRepository;

    private static final String TEACHER_EMAIL = "enseignant@test.com";
    private static final String TEACHER_ID = "teacher-uuid-1";
    private static final String SESSION_ID = "session-uuid-1";

    private Teacher createMockTeacher() {
        Teacher teacher = new Teacher();
        teacher.setId(TEACHER_ID);
        teacher.setEmail(TEACHER_EMAIL);
        teacher.setRole(Role.ENSEIGNANT);
        teacher.setNom("Test");
        teacher.setPrenom("Enseignant");
        return teacher;
    }

    private LiveSessionResponse createMockSessionResponse() {
        return LiveSessionResponse.builder()
                .id(SESSION_ID)
                .title("Cours test")
                .streamKey("test-stream")
                .status(LiveSessionStatus.SCHEDULED)
                .accessType(LiveAccessType.EXTERNAL)
                .scheduledStartAt(LocalDateTime.now().plusHours(1))
                .userId(TEACHER_ID)
                .userName("Enseignant Test")
                .build();
    }

    @Test
    @DisplayName("POST /api/live/sessions - Enseignant peut créer une session")
    @WithMockUser(username = TEACHER_EMAIL)
    void createSession_asTeacher_returns201() throws Exception {
        when(userRepository.findByEmail(TEACHER_EMAIL)).thenReturn(Optional.of(createMockTeacher()));
        LiveSessionResponse response = createMockSessionResponse();
        when(liveSessionService.create(eq(TEACHER_ID), any())).thenReturn(response);

        String body = """
                {
                  "title": "Cours de Tajwid",
                  "streamKey": "tajwid-1",
                  "accessType": "EXTERNAL",
                  "scheduledStartAt": "2025-12-01T14:00:00"
                }
                """;

        mockMvc.perform(post("/api/live/sessions")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(SESSION_ID))
                .andExpect(jsonPath("$.title").value("Cours test"));

        verify(liveSessionService).create(eq(TEACHER_ID), any());
    }

    @Test
    @DisplayName("POST /api/live/sessions - Sans auth retourne 401")
    void createSession_withoutAuth_returns401() throws Exception {
        String body = """
                {
                  "title": "Cours",
                  "streamKey": "stream-1",
                  "accessType": "EXTERNAL",
                  "scheduledStartAt": "2025-12-01T14:00:00"
                }
                """;

        mockMvc.perform(post("/api/live/sessions")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("POST /api/live/sessions/{id}/start - Enseignant peut démarrer le live")
    @WithMockUser(username = TEACHER_EMAIL)
    void startStream_asTeacher_returns200() throws Exception {
        when(userRepository.findByEmail(TEACHER_EMAIL)).thenReturn(Optional.of(createMockTeacher()));
        LiveSessionResponse response = createMockSessionResponse();
        response.setStatus(LiveSessionStatus.LIVE);
        when(liveSessionService.startStream(SESSION_ID)).thenReturn(response);

        mockMvc.perform(post("/api/live/sessions/" + SESSION_ID + "/start").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("LIVE"));

        verify(liveSessionService).startStream(SESSION_ID);
    }

    @Test
    @DisplayName("POST /api/live/sessions/{id}/end - Enseignant peut arrêter le live")
    @WithMockUser(username = TEACHER_EMAIL)
    void endStream_asTeacher_returns200() throws Exception {
        when(userRepository.findByEmail(TEACHER_EMAIL)).thenReturn(Optional.of(createMockTeacher()));
        LiveSessionResponse response = createMockSessionResponse();
        response.setStatus(LiveSessionStatus.ENDED);
        when(liveSessionService.endStream(SESSION_ID)).thenReturn(response);

        mockMvc.perform(post("/api/live/sessions/" + SESSION_ID + "/end").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ENDED"));

        verify(liveSessionService).endStream(SESSION_ID);
    }

    @Test
    @DisplayName("GET /api/live/public/sessions - Accessible sans token")
    void getPublicSessions_withoutAuth_returns200() throws Exception {
        mockMvc.perform(get("/api/live/public/sessions").param("status", "LIVE"))
                .andExpect(status().isOk());
    }
}
