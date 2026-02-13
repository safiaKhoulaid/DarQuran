package com.darquran.application.dto;

import com.darquran.domain.model.enums.courses.CourseLevel;
import com.darquran.domain.model.enums.courses.CourseStatus;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
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