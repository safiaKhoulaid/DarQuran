package com.darquran.application.dto.grade;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentGradeRequest {

    @NotBlank(message = "L'élève est requis")
    private String studentId;

    @NotBlank(message = "Le cours est requis")
    private String courseId;

    @NotNull(message = "La note est requise")
    private Double value;

    private LocalDate gradeDate;

    private String comment;
}
