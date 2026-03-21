package com.darquran.presentation.controller;

import com.darquran.application.dto.resources.ResourceRequest;
import com.darquran.application.dto.resources.ResourceResponse;
import com.darquran.application.service.ResourceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/resources")
@RequiredArgsConstructor
public class ResourceController {

    private final ResourceService resourceService;

    @PostMapping
    public ResponseEntity<ResourceResponse> addResource(@Valid @RequestBody ResourceRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(resourceService.addResource(request));
    }

    @GetMapping("/lesson/{lessonId}")
    public ResponseEntity<List<ResourceResponse>> getResourcesByLesson(@PathVariable String lessonId) {
        return ResponseEntity.ok(resourceService.getResourcesByLesson(lessonId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResourceResponse> getResourceById(@PathVariable String id) {
        return ResponseEntity.ok(resourceService.getResourceById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteResource(@PathVariable String id) {
        resourceService.deleteResource(id);
        return ResponseEntity.noContent().build();
    }
}
