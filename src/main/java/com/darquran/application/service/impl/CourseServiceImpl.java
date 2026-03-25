package com.darquran.application.service.impl;

import com.darquran.application.dto.courses.CourseRequest;
import com.darquran.application.dto.courses.CourseResponse;
import com.darquran.application.mapper.courses.CourseMapper;
import com.darquran.application.service.CourseService;
import com.darquran.application.service.UserNotificationService;
import com.darquran.domain.model.entities.course.Course;
import com.darquran.domain.model.enums.courses.CourseStatus;
import com.darquran.domain.repository.CourseRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {

    private final CourseRepository repository;
    private final CourseMapper mapper;
    private final UserNotificationService userNotificationService;

    @Override
    @Transactional
    public CourseResponse createCourse(CourseRequest request) {
        Course course = mapper.toEntity(request);
        String baseSlug = request.getTitle().toLowerCase().replace(" ", "-");
        course.setSlug(baseSlug + "-" + java.util.UUID.randomUUID().toString().substring(0, 5));
        Course saved = repository.save(course);
        if (saved.getStatus() == CourseStatus.PUBLISHED) {
            userNotificationService.dispatchCoursePublished(saved);
        }
        return mapper.toResponse(saved);
    }

    @Override
    public CourseResponse getCourseById(String id) {
        return repository.findById(id)
                .map(mapper::toResponse)
                .orElseThrow(() -> new EntityNotFoundException("Course not found with id: " + id));
    }

    @Override
    public Page<CourseResponse> getAllCourses(Pageable pageable) {
        return repository.findAll(pageable).map(mapper::toResponse);
    }

    @Override
    @Transactional
    public CourseResponse updateCourse(String id, CourseRequest request) {
        Course course = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Course not found with id: " + id));
        CourseStatus previousStatus = course.getStatus();
        mapper.updateEntityFromRequest(request, course);
        Course saved = repository.save(course);
        if (saved.getStatus() == CourseStatus.PUBLISHED && previousStatus != CourseStatus.PUBLISHED) {
            userNotificationService.dispatchCoursePublished(saved);
        }
        return mapper.toResponse(saved);
    }

    @Override
    @Transactional
    public void deleteCourse(String id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("Course not found with id: " + id);
        }
        repository.deleteById(id);
    }
}

