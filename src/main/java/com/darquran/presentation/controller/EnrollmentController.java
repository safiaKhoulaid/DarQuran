package com.darquran.presentation.controller;

import com.darquran.application.dto.enrollment.EnrollmentRequest;
import com.darquran.application.dto.enrollment.EnrollmentResponse;
import com.darquran.application.service.EnrollmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/enrollments")
@RequiredArgsConstructor
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    @PostMapping
    public ResponseEntity<EnrollmentResponse> enroll(@Valid @RequestBody EnrollmentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(enrollmentService.enroll(request));
    }

    @PostMapping("/{id}/unenroll")
    public ResponseEntity<Void> unenroll(@PathVariable String id) {
        enrollmentService.unenroll(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<EnrollmentResponse>> getByStudent(@PathVariable String studentId) {
        return ResponseEntity.ok(enrollmentService.getByStudent(studentId));
    }

    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<EnrollmentResponse>> getByCourse(@PathVariable String courseId) {
        return ResponseEntity.ok(enrollmentService.getByCourse(courseId));
    }
}

