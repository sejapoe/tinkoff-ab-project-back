package edu.tinkoff.ninjamireaclone.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

import java.net.URI;

@Configuration
public class StorageConfig {
    @Bean
    public AwsCredentials credentials(StorageProperties storageProperties) {
        return AwsBasicCredentials.create(
                storageProperties.getAccessKey(),
                storageProperties.getSecretKey()
        );
    }

    @Bean
    public S3Client s3Client(AwsCredentials awsCredentials) {
        return S3Client
                .builder()
                .credentialsProvider(() -> awsCredentials)
                .endpointOverride(URI.create("https://storage.yandexcloud.net"))
                .region(Region.of("ru-central-1"))
                .build();
    }
}
