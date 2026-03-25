package com.darquran.application.service;

import com.darquran.application.dto.absence.StudentAbsenceResponse;
import com.darquran.application.dto.dashboard.StudentDashboardSummary;
import com.darquran.application.dto.enrollment.EnrollmentResponse;
import com.darquran.application.dto.grade.StudentGradeResponse;
import com.darquran.application.dto.room.RoomResponse;
import com.darquran.application.dto.schedule.ScheduleSlotResponse;
import com.darquran.application.dto.users.student.StudentRequest;
import com.darquran.application.dto.users.student.StudentResponse;

import java.util.List;

public interface StudentDashboardService {

    StudentResponse getProfile(String studentId);

    StudentResponse updateProfile(String studentId, StudentRequest request);

    List<StudentGradeResponse> getGrades(String studentId);

    List<StudentAbsenceResponse> getAbsences(String studentId);

    List<EnrollmentResponse> getEnrollments(String studentId);

    List<ScheduleSlotResponse> getSchedule(String studentId);

    List<RoomResponse> getRooms(String studentId);

    StudentDashboardSummary getDashboardSummary(String studentId);
}
