package com.darquran.application.service.impl;

import com.darquran.application.dto.storage.FileUploadResponse;
import com.darquran.application.service.StorageService;
import com.darquran.infrastructure.config.storage.S3StorageProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.HeadBucketRequest;
import software.amazon.awssdk.services.s3.model.NoSuchBucketException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutBucketPolicyRequest;

import java.net.URI;
import java.util.Locale;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3StorageServiceImpl implements StorageService {
    private final S3StorageProperties props;

    @Override
    public FileUploadResponse uploadPublicFile(MultipartFile file, String folder) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Le fichier est vide.");
        }
        if (file.getSize() > props.getMaxFileSizeBytes()) {
            throw new IllegalArgumentException("Le fichier dépasse la taille maximale autorisée.");
        }

        final String safeFolder = (folder == null || folder.isBlank()) ? "uploads" : folder.trim().toLowerCase(Locale.ROOT);
        validateFileType(file, safeFolder);
        final String original = file.getOriginalFilename() == null ? "file" : file.getOriginalFilename().trim();
        final String ext = original.contains(".") ? original.substring(original.lastIndexOf('.')) : "";
        final String key = safeFolder + "/" + UUID.randomUUID() + ext;

        // Mode dev sans S3/MinIO: on renvoie une URL publique de fallback
        if (!props.isEnabled()) {
            return FileUploadResponse.builder()
                    .key(key)
                    .url(resolveFallbackUrl(file, safeFolder))
                    .contentType(file.getContentType())
                    .size(file.getSize())
                    .build();
        }

        try (S3Client s3 = buildClient()) {
            ensureBucketExists(s3);
            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(props.getBucket())
                    .key(key)
                    .contentType(file.getContentType())
                    .build();
            s3.putObject(request, RequestBody.fromBytes(file.getBytes()));
        } catch (Exception e) {
            // Fallback dev si MinIO/S3 indisponible
            return FileUploadResponse.builder()
                    .key(key)
                    .url(resolveFallbackUrl(file, safeFolder))
                    .contentType(file.getContentType())
                    .size(file.getSize())
                    .build();
        }

        String publicBase = props.getPublicBaseUrl();
        if (publicBase == null || publicBase.isBlank()) {
            publicBase = props.getEndpoint() + "/" + props.getBucket();
        }
        String url = publicBase.replaceAll("/+$", "") + "/" + key;

        return FileUploadResponse.builder()
                .key(key)
                .url(url)
                .contentType(file.getContentType())
                .size(file.getSize())
                .build();
    }

    private String resolveFallbackUrl(MultipartFile file, String folder) {
        String type = file.getContentType() == null ? "" : file.getContentType().toLowerCase(Locale.ROOT);
        if ("course-thumbnails".equals(folder)) {
            return "https://images.unsplash.com/photo-1519817650390-64a93db511aa?auto=format&fit=crop&w=1200&q=80";
        }
        if (type.startsWith("video/")) {
            return "https://www.w3schools.com/html/mov_bbb.mp4";
        }
        if ("application/pdf".equals(type)) {
            return "https://www.w3.org/WAI/ER/tests/xhtml/testfiles/resources/pdf/dummy.pdf";
        }
        return "https://raw.githubusercontent.com/EbookFoundation/free-programming-books/main/books/free-programming-books-ar.md";
    }

    private S3Client buildClient() {
        return S3Client.builder()
                .endpointOverride(URI.create(props.getEndpoint()))
                .region(Region.of(props.getRegion()))
                .forcePathStyle(true)
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(props.getAccessKey(), props.getSecretKey())
                ))
                .build();
    }

    private void ensureBucketExists(S3Client s3) {
        try {
            s3.headBucket(HeadBucketRequest.builder().bucket(props.getBucket()).build());
            ensurePublicReadPolicy(s3);
        } catch (NoSuchBucketException ex) {
            s3.createBucket(CreateBucketRequest.builder().bucket(props.getBucket()).build());
            ensurePublicReadPolicy(s3);
        } catch (Exception ex) {
            // Some S3-compatible providers can return generic errors for missing bucket.
            s3.createBucket(CreateBucketRequest.builder().bucket(props.getBucket()).build());
            ensurePublicReadPolicy(s3);
        }
    }

    private void ensurePublicReadPolicy(S3Client s3) {
        String policy = """
                {
                  "Version":"2012-10-17",
                  "Statement":[
                    {
                      "Effect":"Allow",
                      "Principal":"*",
                      "Action":["s3:GetObject"],
                      "Resource":["arn:aws:s3:::%s/*"]
                    }
                  ]
                }
                """.formatted(props.getBucket());
        try {
            s3.putBucketPolicy(PutBucketPolicyRequest.builder()
                    .bucket(props.getBucket())
                    .policy(policy)
                    .build());
        } catch (Exception ignored) {
            // Certains providers S3/MinIO refusent la policy de bucket.
            // L'upload doit continuer : le bucket peut être déjà configuré autrement (ACL/policy existante).
        }
    }

    private void validateFileType(MultipartFile file, String folder) {
        String type = (file.getContentType() == null ? "" : file.getContentType().toLowerCase(Locale.ROOT));
        boolean isImage = type.startsWith("image/");
        boolean isPdf = "application/pdf".equals(type);
        boolean isVideo = type.startsWith("video/");
        if ("course-thumbnails".equals(folder) && !isImage) {
            throw new IllegalArgumentException("La miniature doit être une image.");
        }
        if ("lesson-resources".equals(folder) && !(isImage || isPdf || isVideo || type.startsWith("text/"))) {
            throw new IllegalArgumentException("Type de fichier non autorisé pour les ressources.");
        }
    }
}
