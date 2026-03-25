package com.darquran.presentation.controller;

import com.darquran.support.AbstractWebMvcControllerTest;
import com.darquran.support.DarQuranWebMvcTest;
import com.darquran.application.dto.schedule.ScheduleSlotResponse;
import com.darquran.application.service.ScheduleService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalTime;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DarQuranWebMvcTest(ScheduleController.class)
class ScheduleControllerTest extends AbstractWebMvcControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ScheduleService scheduleService;

    @Test
    @DisplayName("GET /api/schedule-slots sans auth → 401")
    void getAll_withoutAuth_returns401() throws Exception {
        mockMvc.perform(get("/api/schedule-slots"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("GET /api/schedule-slots avec auth → 200 et JSON")
    @WithMockUser
    void getAll_withAuth_returnsSlots() throws Exception {
        ScheduleSlotResponse slot = ScheduleSlotResponse.builder()
                .id("slot-1")
                .dayOfWeek(1)
                .startTime(LocalTime.of(8, 0))
                .endTime(LocalTime.of(9, 0))
                .roomId("r1")
                .roomName("Salle A")
                .courseId("c1")
                .courseTitle("Tajwid")
                .teacherId("t1")
                .teacherName("Prof X")
                .build();
        when(scheduleService.getAll()).thenReturn(List.of(slot));

        mockMvc.perform(get("/api/schedule-slots"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("slot-1"))
                .andExpect(jsonPath("$[0].courseTitle").value("Tajwid"));
    }
}
