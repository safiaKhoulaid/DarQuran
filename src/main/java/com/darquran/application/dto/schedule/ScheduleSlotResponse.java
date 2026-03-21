package com.darquran.application.dto.schedule;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleSlotResponse {

    private String id;
    private int dayOfWeek;
    private LocalTime startTime;
    private LocalTime endTime;

    private String roomId;
    private String roomName;

    private String courseId;
    private String courseTitle;

    private String teacherId;
    private String teacherName;
}

