package com.darquran.application.dto.dashboard;

import com.darquran.application.dto.absence.StudentAbsenceResponse;
import com.darquran.application.dto.enrollment.EnrollmentResponse;
import com.darquran.application.dto.grade.StudentGradeResponse;
import com.darquran.application.dto.room.RoomResponse;
import com.darquran.application.dto.schedule.ScheduleSlotResponse;
import com.darquran.application.dto.users.student.StudentResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentDashboardSummary {

    private StudentResponse profile;
    private List<StudentGradeResponse> grades;
    private List<StudentAbsenceResponse> absences;
    private List<EnrollmentResponse> enrollments;
    private List<ScheduleSlotResponse> schedule;
    private List<RoomResponse> rooms;
    private StudentStatistics statistics;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StudentStatistics {
        private int totalEnrollments;
        private int activeEnrollments;
        private int totalGrades;
        private Double averageGrade;
        private int totalAbsences;
        private int presentDays;
        private int lateDays;
        private int excusedAbsences;
        private int unexcusedAbsences;
        private Double attendanceRate;
    }
}