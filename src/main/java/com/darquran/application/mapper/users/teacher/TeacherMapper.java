package com.darquran.application.mapper.users.teacher;

import com.darquran.application.dto.users.teacher.TeacherRequest;
import com.darquran.application.dto.users.teacher.TeacherResponse;
import com.darquran.domain.model.entities.users.Teacher;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface TeacherMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "adresse", ignore = true)
    Teacher toEntity(TeacherRequest dto);

    TeacherResponse toResponse(Teacher entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "adresse", ignore = true)
    void updateEntityFromRequest(TeacherRequest request, @MappingTarget Teacher entity);
}

