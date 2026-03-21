package com.darquran.application.service.impl;

import com.darquran.application.dto.resources.ResourceRequest;
import com.darquran.application.dto.resources.ResourceResponse;
import com.darquran.application.mapper.resources.ResourceMapper;
import com.darquran.application.service.ResourceService;
import com.darquran.domain.model.entities.course.Lesson;
import com.darquran.domain.model.entities.course.Resource;
import com.darquran.domain.repository.LessonRepository;
import com.darquran.domain.repository.ResourceRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ResourceServiceImpl implements ResourceService {

    private final ResourceRepository resourceRepository;
    private final LessonRepository lessonRepository;
    private final ResourceMapper mapper;

    @Override
    @Transactional
    public ResourceResponse addResource(ResourceRequest request) {
        Lesson lesson = lessonRepository.findById(request.getLessonId())
                .orElseThrow(() -> new EntityNotFoundException("Lesson not found with id: " + request.getLessonId()));

        Resource resource = mapper.toEntity(request);
        resource.setLesson(lesson);
        return mapper.toResponse(resourceRepository.save(resource));
    }

    @Override
    public List<ResourceResponse> getResourcesByLesson(String lessonId) {
        if (!lessonRepository.existsById(lessonId)) {
            throw new EntityNotFoundException("Lesson not found with id: " + lessonId);
        }
        return resourceRepository.findByLessonId(lessonId)
                .stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ResourceResponse getResourceById(String id) {
        return resourceRepository.findById(id)
                .map(mapper::toResponse)
                .orElseThrow(() -> new EntityNotFoundException("Resource not found with id: " + id));
    }

    @Override
    @Transactional
    public void deleteResource(String id) {
        if (!resourceRepository.existsById(id)) {
            throw new EntityNotFoundException("Resource not found with id: " + id);
        }
        resourceRepository.deleteById(id);
    }
}

