package com.darquran.application.service;

import com.darquran.application.dto.lessons.LessonRequest;
import com.darquran.application.dto.lessons.LessonResponse;

import java.util.List;

/**
 * Contrat du service de gestion des leçons.
 */
public interface LessonService {

    LessonResponse createLesson(LessonRequest request);

    List<LessonResponse> getLessonsByCourse(String courseId);

    LessonResponse getLessonById(String id);

    LessonResponse updateLesson(String id, LessonRequest request);

    void deleteLesson(String id);
}
