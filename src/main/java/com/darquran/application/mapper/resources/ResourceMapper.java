package com.darquran.application.mapper.resources;

import com.darquran.application.dto.resources.ResourceRequest;
import com.darquran.application.dto.resources.ResourceResponse;
import com.darquran.domain.model.entities.course.Resource;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ResourceMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "lesson", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Resource toEntity(ResourceRequest dto);

    @Mapping(target = "lessonId", source = "lesson.id")
    ResourceResponse toResponse(Resource resource);
}
