package com.darquran.infrastructure.config.storage;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(S3StorageProperties.class)
public class S3StorageConfig {
}
