package com.darquran.presentation.controller;

import com.darquran.application.dto.users.teacher.TeacherRequest;
import com.darquran.application.dto.users.teacher.TeacherResponse;
import com.darquran.application.service.TeacherService;
import com.darquran.domain.model.enums.Section;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/teachers")
@RequiredArgsConstructor
public class TeacherController {

    private final TeacherService teacherService;

    @PostMapping
    public ResponseEntity<TeacherResponse> create(@Valid @RequestBody TeacherRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(teacherService.create(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TeacherResponse> getById(@PathVariable String id) {
        return ResponseEntity.ok(teacherService.getById(id));
    }

    @GetMapping
    public ResponseEntity<List<TeacherResponse>> getAllBySection(@RequestParam(name = "section", required = false) Section section ) {
        return ResponseEntity.ok(teacherService.getAllBySection(section));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TeacherResponse> update(
            @PathVariable String id,
            @RequestBody TeacherRequest request) {
        return ResponseEntity.ok(teacherService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        teacherService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

