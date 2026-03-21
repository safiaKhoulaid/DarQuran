package com.darquran.domain.repository;

import com.darquran.domain.model.entities.school.StudentGrade;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StudentGradeRepository extends JpaRepository<StudentGrade, String> {

    List<StudentGrade> findByCourseId(String courseId);

    List<StudentGrade> findByStudentId(String studentId);

    List<StudentGrade> findByStudentIdAndCourseId(String studentId, String courseId);

    List<StudentGrade> findByTeacherId(String teacherId);

    List<StudentGrade> findByCourseIdAndTeacherId(String courseId, String teacherId);
}
