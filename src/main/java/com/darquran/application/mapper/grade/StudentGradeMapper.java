package com.darquran.application.mapper.grade;

import com.darquran.application.dto.grade.StudentGradeResponse;
import com.darquran.domain.model.entities.school.StudentGrade;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface StudentGradeMapper {

    static String buildStudentName(com.darquran.domain.model.entities.school.StudentGrade grade) {
        if (grade == null || grade.getStudent() == null) return null;
        String p = grade.getStudent().getPrenom();
        String n = grade.getStudent().getNom();
        if (p == null && n == null) return null;
        return ((p != null ? p : "") + " " + (n != null ? n : "")).trim();
    }

    @Mapping(target = "studentId", source = "student.id")
    @Mapping(target = "studentName", expression = "java(com.darquran.application.mapper.grade.StudentGradeMapper.buildStudentName(grade))")
    @Mapping(target = "courseId", source = "course.id")
    @Mapping(target = "courseTitle", source = "course.title")
    @Mapping(target = "teacherId", source = "teacher.id")
    StudentGradeResponse toResponse(StudentGrade grade);
}
