package com.darquran.application.dto.resources;

import com.darquran.domain.model.enums.resources.ResourceType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResourceResponse {

    private String id;
    private String name;
    private String fileUrl;
    private ResourceType type;
    private Long size;
    private LocalDateTime createdAt;
    private String lessonId;
}
