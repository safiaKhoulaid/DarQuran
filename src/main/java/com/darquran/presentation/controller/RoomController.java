package com.darquran.presentation.controller;

import com.darquran.application.dto.room.RoomRequest;
import com.darquran.application.dto.room.RoomResponse;
import com.darquran.application.service.RoomService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;

    @PostMapping
    public ResponseEntity<RoomResponse> create(@Valid @RequestBody RoomRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(roomService.create(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RoomResponse> getById(@PathVariable("id") String id) {
        return ResponseEntity.ok(roomService.getById(id));
    }

    @GetMapping
    public ResponseEntity<List<RoomResponse>> getAll() {
        return ResponseEntity.ok(roomService.getAll());
    }

    @PutMapping("/{id}")
    public ResponseEntity<RoomResponse> update(
            @PathVariable("id") String id,
            @Valid @RequestBody RoomRequest request) {
        return ResponseEntity.ok(roomService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") String id) {
        roomService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

