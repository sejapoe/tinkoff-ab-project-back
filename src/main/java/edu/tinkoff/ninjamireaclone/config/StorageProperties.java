package edu.tinkoff.ninjamireaclone.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "storage")
public class StorageProperties {
    private String accessKey;
    private String secretKey;
    private String bucketName;
    private String endpoint;
    private String region;
}
