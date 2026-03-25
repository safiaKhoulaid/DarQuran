package com.darquran.infrastructure.config.storage;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "app.storage.s3")
public class S3StorageProperties {
    private boolean enabled = false;
    private String endpoint;
    private String region = "us-east-1";
    private String accessKey;
    private String secretKey;
    private String bucket = "darquran-media";
    private String publicBaseUrl;
    private long maxFileSizeBytes = 52428800;
}
