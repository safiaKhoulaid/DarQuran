package com.darquran.application.service;

import com.darquran.application.dto.schedule.ScheduleSlotRequest;
import com.darquran.application.dto.schedule.ScheduleSlotResponse;

import java.util.List;

public interface ScheduleService {

    ScheduleSlotResponse create(ScheduleSlotRequest request);

    ScheduleSlotResponse getById(String id);

    List<ScheduleSlotResponse> getAll();

    List<ScheduleSlotResponse> getByRoom(String roomId);

    List<ScheduleSlotResponse> getByCourse(String courseId);

    List<ScheduleSlotResponse> getByTeacher(String teacherId);

    void delete(String id);
}

