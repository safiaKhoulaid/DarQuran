package com.darquran.application.dto.lessons;

import com.darquran.application.dto.resources.ResourceResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LessonResponse {

    private String id;
    private String title;
    private String description;
    private Integer orderIndex;
    private String courseId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<ResourceResponse> resources;
}
