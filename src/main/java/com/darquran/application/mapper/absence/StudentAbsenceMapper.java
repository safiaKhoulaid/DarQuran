package com.darquran.application.mapper.absence;

import com.darquran.application.dto.absence.StudentAbsenceResponse;
import com.darquran.domain.model.entities.school.StudentAbsence;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface StudentAbsenceMapper {

    static String buildTeacherName(StudentAbsence absence) {
        if (absence == null || absence.getScheduleSlot() == null || absence.getScheduleSlot().getTeacher() == null) {
            return null;
        }
        var t = absence.getScheduleSlot().getTeacher();
        String p = t.getPrenom();
        String n = t.getNom();
        if (p == null && n == null) return null;
        return ((p != null ? p : "") + " " + (n != null ? n : "")).trim();
    }

    @Mapping(target = "studentId", source = "student.id")
    @Mapping(target = "studentNom", source = "student.nom")
    @Mapping(target = "studentPrenom", source = "student.prenom")
    @Mapping(target = "scheduleSlotId", source = "scheduleSlot.id")
    @Mapping(target = "courseTitle", source = "scheduleSlot.course.title")
    @Mapping(target = "teacherName", expression = "java(com.darquran.application.mapper.absence.StudentAbsenceMapper.buildTeacherName(absence))")
    StudentAbsenceResponse toResponse(StudentAbsence absence);
}

