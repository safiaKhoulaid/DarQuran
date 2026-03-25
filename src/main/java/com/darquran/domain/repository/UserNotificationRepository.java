package com.darquran.domain.repository;

import com.darquran.domain.model.entities.notification.UserNotification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserNotificationRepository extends JpaRepository<UserNotification, String> {

    Page<UserNotification> findByUser_IdOrderByCreatedAtDesc(String userId, Pageable pageable);

    List<UserNotification> findByUser_IdAndReadFalse(String userId);

    long countByUser_IdAndReadFalse(String userId);

    Optional<UserNotification> findByIdAndUser_Id(String id, String userId);
}
