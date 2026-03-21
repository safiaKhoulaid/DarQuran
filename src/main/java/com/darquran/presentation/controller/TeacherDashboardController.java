package com.darquran.presentation.controller;

import com.darquran.application.dto.absence.StudentAbsenceRequest;
import com.darquran.application.dto.absence.StudentAbsenceResponse;
import com.darquran.application.dto.grade.StudentGradeRequest;
import com.darquran.application.dto.grade.StudentGradeResponse;
import com.darquran.application.dto.room.RoomResponse;
import com.darquran.application.dto.schedule.ScheduleSlotResponse;
import com.darquran.application.dto.teacher.TeacherStudentResponse;
import com.darquran.application.dto.courses.CourseResponse;
import com.darquran.application.service.TeacherDashboardService;
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
@RequestMapping("/api/teacher")
@RequiredArgsConstructor
public class TeacherDashboardController {

    private final TeacherDashboardService teacherDashboardService;
    private final UserRepository userRepository;

    private String currentTeacherId(Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) return null;
        return userRepository.findByEmail(auth.getName())
                .filter(u -> u.getRole() == Role.ENSEIGNANT)
                .map(User::getId)
                .orElse(null);
    }

    @GetMapping("/classes")
    public ResponseEntity<List<RoomResponse>> getMyClasses(Authentication auth) {
        String teacherId = currentTeacherId(auth);
        if (teacherId == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(teacherDashboardService.getMyClasses(teacherId));
    }

    @GetMapping("/classes/{roomId}/students")
    public ResponseEntity<List<TeacherStudentResponse>> getStudentsByClass(
            Authentication auth,
            @PathVariable String roomId) {
        String teacherId = currentTeacherId(auth);
        if (teacherId == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(teacherDashboardService.getStudentsByClass(roomId, teacherId));
    }

    @GetMapping("/courses")
    public ResponseEntity<List<CourseResponse>> getMyCourses(Authentication auth) {
        String teacherId = currentTeacherId(auth);
        if (teacherId == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(teacherDashboardService.getMyCourses(teacherId));
    }

    @GetMapping("/schedule-slots")
    public ResponseEntity<List<ScheduleSlotResponse>> getMyScheduleSlots(Authentication auth) {
        String teacherId = currentTeacherId(auth);
        if (teacherId == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(teacherDashboardService.getMyScheduleSlots(teacherId));
    }

    @PostMapping("/absences")
    public ResponseEntity<StudentAbsenceResponse> markAbsence(
            Authentication auth,
            @Valid @RequestBody StudentAbsenceRequest request) {
        String teacherId = currentTeacherId(auth);
        if (teacherId == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(teacherDashboardService.markAbsence(teacherId, request));
    }

    @GetMapping("/absences")
    public ResponseEntity<List<StudentAbsenceResponse>> getAbsencesByClass(
            Authentication auth,
            @RequestParam String roomId) {
        String teacherId = currentTeacherId(auth);
        if (teacherId == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(teacherDashboardService.getAbsencesByClass(roomId, teacherId));
    }

    @PostMapping("/grades")
    public ResponseEntity<StudentGradeResponse> addGrade(
            Authentication auth,
            @Valid @RequestBody StudentGradeRequest request) {
        String teacherId = currentTeacherId(auth);
        if (teacherId == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(teacherDashboardService.addGrade(teacherId, request));
    }

    @GetMapping("/grades")
    public ResponseEntity<List<StudentGradeResponse>> getGradesByCourse(
            Authentication auth,
            @RequestParam String courseId) {
        String teacherId = currentTeacherId(auth);
        if (teacherId == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(teacherDashboardService.getGradesByCourse(courseId, teacherId));
    }
}
