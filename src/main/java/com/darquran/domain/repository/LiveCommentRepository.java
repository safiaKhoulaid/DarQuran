package com.darquran.domain.repository;

import com.darquran.domain.model.entities.live.LiveComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LiveCommentRepository extends JpaRepository<LiveComment, String> {

    List<LiveComment> findByLiveSessionIdOrderByCreatedAtAsc(String liveSessionId);

    Page<LiveComment> findByLiveSessionIdOrderByCreatedAtDesc(String liveSessionId, Pageable pageable);
}
