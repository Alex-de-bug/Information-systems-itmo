package com.alwx.backend.configs;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.minio.MinioClient;

@Configuration
public class MinioConfig {
    @Value("${MINIO_URL}")
    private String url;
    
    @Value("${MINIO_ACCESS_KEY}")
    private String accessKey;
    
    @Value("${MINIO_SECRET_KEY}")
    private String accessSecret;

    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder()
                .endpoint(url)
                .credentials(accessKey, accessSecret)
                .build();
    }
}
