package com.darquran.domain.repository;

import com.darquran.domain.model.entities.school.TeacherAbsence;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface TeacherAbsenceRepository extends JpaRepository<TeacherAbsence, String> {

    List<TeacherAbsence> findByTeacherId(String teacherId);

    List<TeacherAbsence> findByScheduleSlotId(String scheduleSlotId);

    List<TeacherAbsence> findByTeacherIdAndDateBetween(String teacherId, LocalDate start, LocalDate end);
}

