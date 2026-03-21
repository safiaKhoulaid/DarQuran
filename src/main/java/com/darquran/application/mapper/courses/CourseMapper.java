package com.darquran.application.mapper.courses;

import com.darquran.application.dto.courses.CourseRequest;
import com.darquran.application.dto.courses.CourseResponse;
import com.darquran.domain.model.entities.course.Course;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CourseMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "lessons", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Course toEntity(CourseRequest dto);

    @Mapping(target = "numberOfLessons", expression = "java(course.getLessons().size())")
    CourseResponse toResponse(Course course);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "lessons", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromRequest(CourseRequest request, @MappingTarget Course course);
}
