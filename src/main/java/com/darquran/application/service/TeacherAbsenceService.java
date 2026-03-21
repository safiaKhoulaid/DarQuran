package com.darquran.application.service;

import com.darquran.application.dto.absence.TeacherAbsenceRequest;
import com.darquran.application.dto.absence.TeacherAbsenceResponse;

import java.time.LocalDate;
import java.util.List;

public interface TeacherAbsenceService {

    TeacherAbsenceResponse markAbsence(TeacherAbsenceRequest request);

    List<TeacherAbsenceResponse> getByTeacher(String teacherId);

    List<TeacherAbsenceResponse> getByTeacherAndPeriod(String teacherId, LocalDate start, LocalDate end);
}

