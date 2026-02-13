package com.darquran.application.dto.courses;

import com.darquran.domain.model.enums.courses.CourseLevel;
import com.darquran.domain.model.enums.courses.CourseStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseRequest {

    @NotBlank(message = "Title is required")
    @Size(min = 3, max = 255, message = "Title must be between 3 and 255 characters")
    private String title;

    @Size(max = 2000, message = "Description is too long")
    private String description;

    private String miniature;


    private String slug;

    @Builder.Default
    private boolean isPublic = false;

    @NotNull(message = "Course level is required")
    private CourseLevel level;

    private CourseStatus status;
}