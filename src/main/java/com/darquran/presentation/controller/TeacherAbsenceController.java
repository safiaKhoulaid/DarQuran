package com.darquran.presentation.controller;

import com.darquran.application.dto.absence.TeacherAbsenceRequest;
import com.darquran.application.dto.absence.TeacherAbsenceResponse;
import com.darquran.application.service.TeacherAbsenceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/absences/teachers")
@RequiredArgsConstructor
public class TeacherAbsenceController {

    private final TeacherAbsenceService teacherAbsenceService;

    @PostMapping
    public ResponseEntity<TeacherAbsenceResponse> markAbsence(@Valid @RequestBody TeacherAbsenceRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(teacherAbsenceService.markAbsence(request));
    }

    @GetMapping("/teacher/{teacherId}")
    public ResponseEntity<List<TeacherAbsenceResponse>> getByTeacher(@PathVariable String teacherId) {
        return ResponseEntity.ok(teacherAbsenceService.getByTeacher(teacherId));
    }

    @GetMapping("/teacher/{teacherId}/period")
    public ResponseEntity<List<TeacherAbsenceResponse>> getByTeacherAndPeriod(
            @PathVariable String teacherId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end
    ) {
        return ResponseEntity.ok(teacherAbsenceService.getByTeacherAndPeriod(teacherId, start, end));
    }
}

