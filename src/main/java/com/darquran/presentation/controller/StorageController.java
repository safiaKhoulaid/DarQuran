package com.darquran.presentation.controller;

import com.darquran.application.dto.storage.FileUploadResponse;
import com.darquran.application.service.StorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/storage")
@RequiredArgsConstructor
public class StorageController {
    private final StorageService storageService;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<FileUploadResponse> upload(
            @RequestPart("file") MultipartFile file,
            @RequestParam(name = "folder", defaultValue = "uploads") String folder) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(storageService.uploadPublicFile(file, folder));
    }
}
