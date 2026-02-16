package com.darquran.application.dto.courses;

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
public class CourseResponse {

    private String id;
    private String title;
    private String slug;
    private String description;
    private String miniature;
    private boolean isPublic;
    private CourseStatus status;
    private CourseLevel level;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private int numberOfLessons;
}