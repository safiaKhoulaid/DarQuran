package com.darquran.application.service;

import com.darquran.application.dto.enrollment.EnrollmentRequest;
import com.darquran.application.dto.enrollment.EnrollmentResponse;

import java.util.List;

public interface EnrollmentService {

    EnrollmentResponse enroll(EnrollmentRequest request);

    void unenroll(String enrollmentId);

    List<EnrollmentResponse> getByStudent(String studentId);

    List<EnrollmentResponse> getByCourse(String courseId);
}

