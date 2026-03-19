package com.darquran.presentation.controller;

import com.darquran.application.dto.users.admin.AdminRequest;
import com.darquran.application.dto.users.admin.AdminResponse;
import com.darquran.application.service.AdminService;
import com.darquran.domain.model.enums.Section;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admins")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @PostMapping
    public ResponseEntity<AdminResponse> create(
            @Validated(AdminRequest.OnCreate.class) @RequestBody AdminRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(adminService.create(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AdminResponse> getById(@PathVariable String id) {
        return ResponseEntity.ok(adminService.getById(id));
    }

    @GetMapping
    public ResponseEntity<List<AdminResponse>> getAllBySection(@RequestParam(required = false) Section section) {
        return ResponseEntity.ok(adminService.getAllBySection(section));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AdminResponse> update(
            @PathVariable String id,
            @RequestBody AdminRequest request) {
        return ResponseEntity.ok(adminService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        adminService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

