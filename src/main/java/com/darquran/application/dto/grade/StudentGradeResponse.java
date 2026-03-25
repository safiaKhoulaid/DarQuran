package com.darquran.application.dto.grade;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentGradeResponse {

    private String id;
    private String studentId;
    private String studentName;
    private String courseId;
    private String courseTitle;
    private Double value;
    private LocalDate gradeDate;
    private String comment;
    private String teacherId;
    private String teacherName;
    private LocalDateTime createdAt;
}
