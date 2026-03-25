package com.darquran.application.mapper.enrollment;

import com.darquran.application.dto.enrollment.EnrollmentResponse;
import com.darquran.domain.model.entities.school.Enrollment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface EnrollmentMapper {

    @Mapping(target = "studentId", source = "student.id")
    @Mapping(target = "studentNom", source = "student.nom")
    @Mapping(target = "studentPrenom", source = "student.prenom")
    @Mapping(target = "courseId", source = "course.id")
    @Mapping(target = "courseTitle", source = "course.title")
    @Mapping(target = "courseLevel", source = "course.level")
    @Mapping(target = "courseStatus", source = "course.status")
    @Mapping(target = "courseDescription", source = "course.description")
    EnrollmentResponse toResponse(Enrollment enrollment);
}

