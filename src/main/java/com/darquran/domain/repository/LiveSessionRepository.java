package com.darquran.domain.repository;

import com.darquran.domain.model.entities.live.LiveSession;
import com.darquran.domain.model.enums.live.LiveAccessType;
import com.darquran.domain.model.enums.live.LiveSessionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface LiveSessionRepository extends JpaRepository<LiveSession, String> {

    Optional<LiveSession> findByStreamKey(String streamKey);

    Page<LiveSession> findByStatus(LiveSessionStatus status, Pageable pageable);

    Page<LiveSession> findByStatusAndAccessType(LiveSessionStatus status, LiveAccessType accessType, Pageable pageable);

    List<LiveSession> findByScheduledStartAtBetweenAndStatus(
            LocalDateTime start,
            LocalDateTime end,
            LiveSessionStatus status);

    List<LiveSession> findByTeacherIdAndStatus(String teacherId, LiveSessionStatus status);

    boolean existsByStreamKey(String streamKey);
}
