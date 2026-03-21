package com.darquran.application.dto.absence;

import com.darquran.domain.model.enums.AbsenceStatus;
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
public class StudentAbsenceRequest {

    @NotBlank
    private String studentId;

    @NotBlank
    private String scheduleSlotId;

    @NotNull
    private LocalDate date;

    @NotNull
    private AbsenceStatus status;

    private String justificationText;

    private String justificationFileUrl;
}

