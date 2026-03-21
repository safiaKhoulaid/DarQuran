package com.darquran.application.dto.enrollment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnrollmentResponse {

    private String id;
    private String studentId;
    private String studentNom;
    private String studentPrenom;
    private String courseId;
    private String courseTitle;
    private LocalDateTime enrolledAt;
    private Boolean active;
}

