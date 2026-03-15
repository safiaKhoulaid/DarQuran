package com.darquran.application.dto.live;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LiveCommentResponse {

    private String id;
    private String content;
    private LocalDateTime createdAt;
    private String liveSessionId;
    private String authorId;
    private String authorDisplayName;
}
