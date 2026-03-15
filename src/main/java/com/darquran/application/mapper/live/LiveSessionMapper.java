package com.darquran.application.mapper.live;

import com.darquran.application.dto.live.LiveSessionRequest;
import com.darquran.application.dto.live.LiveSessionResponse;
import com.darquran.domain.model.entities.live.LiveSession;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface LiveSessionMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "hlsPlaybackUrl", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "recordingUrl", ignore = true)
    @Mapping(target = "startedAt", ignore = true)
    @Mapping(target = "endedAt", ignore = true)
    @Mapping(target = "notificationSent", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "teacher", ignore = true)
    @Mapping(target = "comments", ignore = true)
    LiveSession toEntity(LiveSessionRequest request);

    @Mapping(target = "teacherId", source = "teacher.id")
    @Mapping(target = "teacherName", source = "teacher", qualifiedByName = "teacherFullName")
    @Mapping(target = "commentCount", expression = "java(entity.getComments() != null ? entity.getComments().size() : 0)")
    LiveSessionResponse toResponse(LiveSession entity);

    @Named("teacherFullName")
    default String teacherFullName(com.darquran.domain.model.entities.users.Teacher teacher) {
        if (teacher == null) return null;
        return teacher.getPrenom() + " " + teacher.getNom();
    }

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "hlsPlaybackUrl", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "recordingUrl", ignore = true)
    @Mapping(target = "startedAt", ignore = true)
    @Mapping(target = "endedAt", ignore = true)
    @Mapping(target = "notificationSent", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "teacher", ignore = true)
    @Mapping(target = "comments", ignore = true)
    void updateEntityFromRequest(LiveSessionRequest request, @MappingTarget LiveSession entity);
}
