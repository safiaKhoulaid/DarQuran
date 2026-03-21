package com.darquran.presentation.controller;

import com.darquran.application.dto.absence.StudentAbsenceRequest;
import com.darquran.application.dto.absence.StudentAbsenceResponse;
import com.darquran.application.service.StudentAbsenceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/absences/students")
@RequiredArgsConstructor
public class StudentAbsenceController {

    private final StudentAbsenceService studentAbsenceService;

    @PostMapping
    public ResponseEntity<StudentAbsenceResponse> markAbsence(@Valid @RequestBody StudentAbsenceRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(studentAbsenceService.markAbsence(request));
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<StudentAbsenceResponse>> getByStudent(@PathVariable String studentId) {
        return ResponseEntity.ok(studentAbsenceService.getByStudent(studentId));
    }

    @GetMapping("/student/{studentId}/period")
    public ResponseEntity<List<StudentAbsenceResponse>> getByStudentAndPeriod(
            @PathVariable String studentId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end
    ) {
        return ResponseEntity.ok(studentAbsenceService.getByStudentAndPeriod(studentId, start, end));
    }
}

