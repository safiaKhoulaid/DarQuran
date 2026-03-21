package com.darquran.application.mapper.absence;

import com.darquran.application.dto.absence.TeacherAbsenceResponse;
import com.darquran.domain.model.entities.school.TeacherAbsence;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TeacherAbsenceMapper {

    @Mapping(target = "teacherId", source = "teacher.id")
    @Mapping(target = "teacherNom", source = "teacher.nom")
    @Mapping(target = "teacherPrenom", source = "teacher.prenom")
    @Mapping(target = "scheduleSlotId", source = "scheduleSlot.id")
    TeacherAbsenceResponse toResponse(TeacherAbsence absence);
}

