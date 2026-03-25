package com.darquran.domain.repository;

import com.darquran.domain.model.entities.school.ScheduleSlot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ScheduleSlotRepository extends JpaRepository<ScheduleSlot, String> {

    List<ScheduleSlot> findByRoomId(String roomId);

    List<ScheduleSlot> findByCourseId(String courseId);

    List<ScheduleSlot> findByTeacherId(String teacherId);

    List<ScheduleSlot> findByRoomIdAndTeacherId(String roomId, String teacherId);

    List<ScheduleSlot> findByTeacherIdAndCourseId(String teacherId, String courseId);

    List<ScheduleSlot> findByCourseIdIn(java.util.List<String> courseIds);
}

