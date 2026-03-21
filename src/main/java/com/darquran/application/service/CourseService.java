package com.darquran.application.service;

import com.darquran.application.dto.courses.CourseRequest;
import com.darquran.application.dto.courses.CourseResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Contrat du service de gestion des cours.
 */
public interface CourseService {

    CourseResponse createCourse(CourseRequest request);

    CourseResponse getCourseById(String id);

    Page<CourseResponse> getAllCourses(Pageable pageable);

    CourseResponse updateCourse(String id, CourseRequest request);

    void deleteCourse(String id);
}

