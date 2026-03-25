package com.darquran.application.dto.notification;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Enveloppe JSON pour la liste paginée des notifications (évite les erreurs de sérialisation Jackson sur {@link Page}).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserNotificationPageResponse {

    private List<UserNotificationResponse> content;
    private long totalElements;
    private int totalPages;
    private int number;
    private int size;
    private boolean first;
    private boolean last;

    public static UserNotificationPageResponse from(Page<UserNotificationResponse> page) {
        return UserNotificationPageResponse.builder()
                .content(page.getContent())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .number(page.getNumber())
                .size(page.getSize())
                .first(page.isFirst())
                .last(page.isLast())
                .build();
    }
}
