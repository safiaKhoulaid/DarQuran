package com.darquran.domain.repository;

import com.darquran.domain.model.entities.course.Resource;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ResourceRepository extends JpaRepository<Resource, String> {
    List<Resource> findByLessonId(String lessonId);
}
