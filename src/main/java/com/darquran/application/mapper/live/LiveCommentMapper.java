package com.darquran.application.mapper.live;

import com.darquran.application.dto.live.LiveCommentRequest;
import com.darquran.application.dto.live.LiveCommentResponse;
import com.darquran.domain.model.entities.live.LiveComment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface LiveCommentMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "liveSession", ignore = true)
    @Mapping(target = "author", ignore = true)
    LiveComment toEntity(LiveCommentRequest request);

    @Mapping(target = "liveSessionId", source = "liveSession.id")
    @Mapping(target = "authorId", source = "author.id")
    @Mapping(target = "authorDisplayName", source = ".", qualifiedByName = "authorDisplayName")
    LiveCommentResponse toResponse(LiveComment entity);

    @Named("authorDisplayName")
    default String authorDisplayName(LiveComment entity) {
        if (entity.getAuthor() != null) {
            return entity.getAuthor().getPrenom() + " " + entity.getAuthor().getNom();
        }
        return entity.getAuthorDisplayName();
    }
}
