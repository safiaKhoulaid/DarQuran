package com.darquran.application.dto.enrollment;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnrollmentRequest {

    @NotBlank(message = "L'identifiant de l'élève est requis")
    private String studentId;

    @NotBlank(message = "L'identifiant du cours est requis")
    private String courseId;
}

