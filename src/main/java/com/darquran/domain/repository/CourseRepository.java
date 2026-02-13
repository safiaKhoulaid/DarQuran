package com.darquran.domain.repository;

import com.darquran.domain.model.entities.course.Course;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseRepository extends JpaRepository<Course, String> {
}
