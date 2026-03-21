package com.darquran.domain.repository;

import com.darquran.domain.model.entities.school.StudentAbsence;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface StudentAbsenceRepository extends JpaRepository<StudentAbsence, String> {

    List<StudentAbsence> findByStudentId(String studentId);

    List<StudentAbsence> findByScheduleSlotId(String scheduleSlotId);

    List<StudentAbsence> findByStudentIdAndDateBetween(String studentId, LocalDate start, LocalDate end);

    List<StudentAbsence> findByScheduleSlotIdIn(java.util.List<String> scheduleSlotIds);
}

