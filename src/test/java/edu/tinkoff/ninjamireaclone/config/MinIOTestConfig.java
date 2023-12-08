package edu.tinkoff.ninjamireaclone.config;

import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;
import org.testcontainers.containers.MinIOContainer;
import org.testcontainers.containers.PostgreSQLContainer;

import java.time.Duration;
import java.util.Objects;

public class MinIOTestConfig {
    private static volatile MinIOContainer minIOContainer = null;

    private static MinIOContainer getMinIOContainer() {
        var instance = minIOContainer;
        if (Objects.isNull(instance)) {
            synchronized (PostgreSQLContainer.class) {
                instance = minIOContainer;
                if (Objects.isNull(instance)) {
                    minIOContainer = instance = new MinIOContainer("minio/minio")
                            .withUserName("test-user")
                            .withPassword("test-pass")
                            .withStartupTimeout(Duration.ofSeconds(60))
                            .withReuse(true);
                    minIOContainer.start();
                }
            }
        }
        return instance;
    }

    @Component("MinioInitializer")
    public static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        @Override
        public void initialize(ConfigurableApplicationContext applicationContext) {
            var minIOContainer = getMinIOContainer();

            var s3url = minIOContainer.getS3URL();
            var username = minIOContainer.getUserName();
            var password = minIOContainer.getPassword();

            TestPropertyValues.of(
                    "storage.region=us-east-1",
                    "storage.bucket-name=test",
                    "storage.endpoint=" + s3url,
                    "storage.access-key=" + username,
                    "storage.secret-key=" + password
            ).applyTo(applicationContext.getEnvironment());

        }
    }
}