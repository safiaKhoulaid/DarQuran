package com.darquran.domain.repository;

import com.darquran.domain.model.entities.course.Lesson;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LessonRepository extends JpaRepository<Lesson, String> {

    @EntityGraph(attributePaths = {"resources"})
    List<Lesson> findByCourseIdOrderByOrderIndexAsc(String courseId);
}
