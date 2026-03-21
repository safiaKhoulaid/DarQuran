package com.darquran.application.mapper.absence;

import com.darquran.application.dto.absence.StudentAbsenceResponse;
import com.darquran.domain.model.entities.school.StudentAbsence;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface StudentAbsenceMapper {

    @Mapping(target = "studentId", source = "student.id")
    @Mapping(target = "studentNom", source = "student.nom")
    @Mapping(target = "studentPrenom", source = "student.prenom")
    @Mapping(target = "scheduleSlotId", source = "scheduleSlot.id")
    StudentAbsenceResponse toResponse(StudentAbsence absence);
}

