package com.darquran.application.service;

import com.darquran.application.dto.notification.UserNotificationResponse;
import com.darquran.domain.model.entities.course.Course;
import com.darquran.domain.model.entities.live.LiveSession;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserNotificationService {

    Page<UserNotificationResponse> listForUser(String userId, Pageable pageable);

    long countUnread(String userId);

    void markRead(String userId, String notificationId);

    void markAllRead(String userId);

    void dispatchLiveStarted(LiveSession session);

    void dispatchCoursePublished(Course course);
}
