package com.darquran.application.dto.resources;

import com.darquran.domain.model.enums.resources.ResourceType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResourceRequest {

    @NotBlank(message = "Resource name is required")
    private String name;

    @NotBlank(message = "File URL is required")
    private String fileUrl;

    @NotNull(message = "Resource type is required")
    private ResourceType type;

    private Long size;

    @NotBlank(message = "Lesson ID is required")
    private String lessonId;
}
