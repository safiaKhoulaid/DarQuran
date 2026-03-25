package com.darquran.application.dto.enrollment;

import com.darquran.domain.model.enums.courses.CourseLevel;
import com.darquran.domain.model.enums.courses.CourseStatus;
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
    private CourseLevel courseLevel;
    private CourseStatus courseStatus;
    private String courseDescription;
    private LocalDateTime enrolledAt;
    private Boolean active;
}

