package com.darquran.application.mapper.users.student;

import com.darquran.application.dto.users.student.StudentRequest;
import com.darquran.application.dto.users.student.StudentResponse;
import com.darquran.application.mapper.valueobjects.AdresseMapper;
import com.darquran.domain.model.entities.users.Student;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = {AdresseMapper.class})
public interface StudentMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Student toEntity(StudentRequest dto);

    StudentResponse toResponse(Student entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void updateEntityFromRequest(StudentRequest request, @MappingTarget Student entity);
}

