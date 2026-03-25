package com.darquran.application.dto.notification;

import com.darquran.domain.model.enums.notification.UserNotificationType;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO sérialisable JSON (évite les ambiguïtés Lombok @Value + boolean {@code read} avec Jackson).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserNotificationResponse {

    private String id;
    private UserNotificationType type;
    private String title;
    private String body;
    private String linkUrl;

    @JsonProperty("read")
    private boolean read;

    private LocalDateTime createdAt;
}
