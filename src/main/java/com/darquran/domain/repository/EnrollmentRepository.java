package com.darquran.domain.repository;

import com.darquran.domain.model.entities.school.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EnrollmentRepository extends JpaRepository<Enrollment, String> {

    Optional<Enrollment> findByStudentIdAndCourseId(String studentId, String courseId);

    List<Enrollment> findByStudentId(String studentId);

    List<Enrollment> findByCourseId(String courseId);

    List<Enrollment> findByCourseIdIn(java.util.List<String> courseIds);
}

