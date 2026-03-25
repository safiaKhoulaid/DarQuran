package com.darquran.presentation.controller;

import com.darquran.application.dto.schedule.ScheduleSlotRequest;
import com.darquran.application.dto.schedule.ScheduleSlotResponse;
import com.darquran.application.service.ScheduleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/schedule-slots")
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleService scheduleService;

    @PostMapping
    public ResponseEntity<ScheduleSlotResponse> create(@Valid @RequestBody ScheduleSlotRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(scheduleService.create(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ScheduleSlotResponse> getById(@PathVariable("id") String id) {
        return ResponseEntity.ok(scheduleService.getById(id));
    }

    @GetMapping
    public ResponseEntity<List<ScheduleSlotResponse>> getAll() {
        return ResponseEntity.ok(scheduleService.getAll());
    }

    @GetMapping("/room/{roomId}")
    public ResponseEntity<List<ScheduleSlotResponse>> getByRoom(@PathVariable("roomId") String roomId) {
        return ResponseEntity.ok(scheduleService.getByRoom(roomId));
    }

    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<ScheduleSlotResponse>> getByCourse(@PathVariable("courseId") String courseId) {
        return ResponseEntity.ok(scheduleService.getByCourse(courseId));
    }

    @GetMapping("/teacher/{teacherId}")
    public ResponseEntity<List<ScheduleSlotResponse>> getByTeacher(@PathVariable("teacherId") String teacherId) {
        return ResponseEntity.ok(scheduleService.getByTeacher(teacherId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") String id) {
        scheduleService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

