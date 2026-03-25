package com.darquran.application.service;

import com.darquran.application.dto.storage.FileUploadResponse;
import org.springframework.web.multipart.MultipartFile;

public interface StorageService {
    FileUploadResponse uploadPublicFile(MultipartFile file, String folder);
}
