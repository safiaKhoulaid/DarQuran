package com.darquran.presentation.controller;

import com.darquran.application.dto.absence.StudentAbsenceResponse;
import com.darquran.application.dto.dashboard.StudentDashboardSummary;
import com.darquran.application.dto.enrollment.EnrollmentResponse;
import com.darquran.application.dto.grade.StudentGradeResponse;
import com.darquran.application.dto.room.RoomResponse;
import com.darquran.application.dto.schedule.ScheduleSlotResponse;
import com.darquran.application.dto.users.student.StudentRequest;
import com.darquran.application.dto.users.student.StudentResponse;
import com.darquran.application.service.StudentDashboardService;
import com.darquran.domain.model.entities.users.User;
import com.darquran.domain.model.enums.Role;
import com.darquran.domain.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/student")
@RequiredArgsConstructor
public class StudentDashboardController {

    private final StudentDashboardService studentDashboardService;
    private final UserRepository userRepository;

    private String currentStudentId(Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) return null;
        return userRepository.findByEmail(auth.getName())
                .filter(u -> u.getRole() == Role.ELEVE)
                .map(User::getId)
                .orElse(null);
    }

    @GetMapping("/dashboard")
    public ResponseEntity<StudentDashboardSummary> getDashboard(Authentication auth) {
        String studentId = currentStudentId(auth);
        if (studentId == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(studentDashboardService.getDashboardSummary(studentId));
    }

    @GetMapping("/profile")
    public ResponseEntity<StudentResponse> getProfile(Authentication auth) {
        String studentId = currentStudentId(auth);
        if (studentId == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(studentDashboardService.getProfile(studentId));
    }

    @PutMapping("/profile")
    public ResponseEntity<StudentResponse> updateProfile(
            Authentication auth,
            @Valid @RequestBody StudentRequest request) {
        String studentId = currentStudentId(auth);
        if (studentId == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(studentDashboardService.updateProfile(studentId, request));
    }

    @GetMapping("/grades")
    public ResponseEntity<List<StudentGradeResponse>> getGrades(Authentication auth) {
        String studentId = currentStudentId(auth);
        if (studentId == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(studentDashboardService.getGrades(studentId));
    }

    @GetMapping("/absences")
    public ResponseEntity<List<StudentAbsenceResponse>> getAbsences(Authentication auth) {
        String studentId = currentStudentId(auth);
        if (studentId == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(studentDashboardService.getAbsences(studentId));
    }

    @GetMapping("/enrollments")
    public ResponseEntity<List<EnrollmentResponse>> getEnrollments(Authentication auth) {
        String studentId = currentStudentId(auth);
        if (studentId == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(studentDashboardService.getEnrollments(studentId));
    }

    @GetMapping("/schedule")
    public ResponseEntity<List<ScheduleSlotResponse>> getSchedule(Authentication auth) {
        String studentId = currentStudentId(auth);
        if (studentId == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(studentDashboardService.getSchedule(studentId));
    }

    @GetMapping("/room")
    public ResponseEntity<List<RoomResponse>> getRooms(Authentication auth) {
        String studentId = currentStudentId(auth);
        if (studentId == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(studentDashboardService.getRooms(studentId));
    }
}
