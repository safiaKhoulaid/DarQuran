package com.darquran.application.service.impl;

import com.darquran.application.dto.lessons.LessonRequest;
import com.darquran.application.dto.lessons.LessonResponse;
import com.darquran.application.mapper.lessons.LessonMapper;
import com.darquran.application.service.LessonService;
import com.darquran.domain.model.entities.course.Course;
import com.darquran.domain.model.entities.course.Lesson;
import com.darquran.domain.repository.CourseRepository;
import com.darquran.domain.repository.LessonRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LessonServiceImpl implements LessonService {

    private final LessonRepository lessonRepository;
    private final CourseRepository courseRepository;
    private final LessonMapper mapper;

    @Override
    @Transactional
    public LessonResponse createLesson(LessonRequest request) {
        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new EntityNotFoundException("Course not found with id: " + request.getCourseId()));

        Lesson lesson = mapper.toEntity(request);
        lesson.setCourse(course);
        return mapper.toResponse(lessonRepository.save(lesson));
    }

    @Override
    public List<LessonResponse> getLessonsByCourse(String courseId) {
        if (!courseRepository.existsById(courseId)) {
            throw new EntityNotFoundException("Course not found with id: " + courseId);
        }
        return lessonRepository.findByCourseIdOrderByOrderIndexAsc(courseId)
                .stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public LessonResponse getLessonById(String id) {
        return lessonRepository.findById(id)
                .map(mapper::toResponse)
                .orElseThrow(() -> new EntityNotFoundException("Lesson not found with id: " + id));
    }

    @Override
    @Transactional
    public LessonResponse updateLesson(String id, LessonRequest request) {
        Lesson lesson = lessonRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Lesson not found with id: " + id));
        mapper.updateEntityFromRequest(request, lesson);
        return mapper.toResponse(lessonRepository.save(lesson));
    }

    @Override
    @Transactional
    public void deleteLesson(String id) {
        if (!lessonRepository.existsById(id)) {
            throw new EntityNotFoundException("Lesson not found with id: " + id);
        }
        lessonRepository.deleteById(id);
    }
}

