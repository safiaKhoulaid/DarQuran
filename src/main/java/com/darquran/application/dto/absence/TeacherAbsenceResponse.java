package com.darquran.application.dto.absence;

import com.darquran.domain.model.enums.AbsenceStatus;
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
public class TeacherAbsenceResponse {

    private String id;
    private String teacherId;
    private String teacherNom;
    private String teacherPrenom;
    private String scheduleSlotId;
    private LocalDate date;
    private AbsenceStatus status;
    private String justificationText;
    private String justificationFileUrl;
    private LocalDateTime createdAt;
}

