package com.darquran.presentation.controller;

import com.darquran.support.AbstractWebMvcControllerTest;
import com.darquran.support.DarQuranWebMvcTest;
import com.darquran.application.dto.notification.UserNotificationResponse;
import com.darquran.application.service.UserNotificationService;
import com.darquran.domain.model.entities.users.Student;
import com.darquran.domain.model.enums.notification.UserNotificationType;
import com.darquran.domain.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DarQuranWebMvcTest(UserNotificationController.class)
class UserNotificationControllerTest extends AbstractWebMvcControllerTest {

    private static final String EMAIL = "eleve@test.com";
    private static final String USER_ID = "user-uuid-1";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserNotificationService userNotificationService;

    @MockBean
    private UserRepository userRepository;

    @Test
    @DisplayName("GET /api/notifications sans auth → 401")
    void list_withoutAuth_returns401() throws Exception {
        mockMvc.perform(get("/api/notifications"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("GET /api/notifications retourne une page avec content")
    @WithMockUser(username = EMAIL)
    void list_withAuth_returns200AndContent() throws Exception {
        Student student = new Student();
        student.setId(USER_ID);
        student.setEmail(EMAIL);
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(student));

        UserNotificationResponse item = UserNotificationResponse.builder()
                .id("n1")
                .type(UserNotificationType.LIVE_STARTED)
                .title("Live")
                .body("Texte")
                .linkUrl("/live/1")
                .read(false)
                .createdAt(LocalDateTime.parse("2025-06-01T12:00:00"))
                .build();
        Page<UserNotificationResponse> page = new PageImpl<>(List.of(item), PageRequest.of(0, 20), 1);
        when(userNotificationService.listForUser(eq(USER_ID), any())).thenReturn(page);

        mockMvc.perform(get("/api/notifications"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].id").value("n1"))
                .andExpect(jsonPath("$.content[0].read").value(false))
                .andExpect(jsonPath("$.totalElements").value(1));

        verify(userNotificationService).listForUser(eq(USER_ID), any());
    }

    @Test
    @DisplayName("GET /api/notifications/unread-count retourne le nombre")
    @WithMockUser(username = EMAIL)
    void unreadCount_returnsCount() throws Exception {
        Student student = new Student();
        student.setId(USER_ID);
        student.setEmail(EMAIL);
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(student));
        when(userNotificationService.countUnread(USER_ID)).thenReturn(5L);

        mockMvc.perform(get("/api/notifications/unread-count"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count").value(5));
    }

    @Test
    @DisplayName("POST /api/notifications/read-all → 204")
    @WithMockUser(username = EMAIL)
    void markAllRead_returns204() throws Exception {
        Student student = new Student();
        student.setId(USER_ID);
        student.setEmail(EMAIL);
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(student));

        mockMvc.perform(post("/api/notifications/read-all").with(csrf()))
                .andExpect(status().isNoContent());

        verify(userNotificationService).markAllRead(USER_ID);
    }
}
