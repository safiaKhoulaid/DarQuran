package com.darquran.application.service;

import com.darquran.application.dto.absence.StudentAbsenceRequest;
import com.darquran.application.dto.absence.StudentAbsenceResponse;

import java.time.LocalDate;
import java.util.List;

public interface StudentAbsenceService {

    StudentAbsenceResponse markAbsence(StudentAbsenceRequest request);

    List<StudentAbsenceResponse> getByStudent(String studentId);

    List<StudentAbsenceResponse> getByStudentAndPeriod(String studentId, LocalDate start, LocalDate end);
}

