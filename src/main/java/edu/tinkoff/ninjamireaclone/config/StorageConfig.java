package edu.tinkoff.ninjamireaclone.config;

import io.minio.MinioClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StorageConfig {
    @Bean
    public MinioClient minioClient(StorageProperties storageProperties) {
        return MinioClient.builder()
                .credentials(storageProperties.getAccessKey(), storageProperties.getSecretKey())
                .region(storageProperties.getRegion())
                .endpoint(storageProperties.getEndpoint())
                .build();
    }
}
