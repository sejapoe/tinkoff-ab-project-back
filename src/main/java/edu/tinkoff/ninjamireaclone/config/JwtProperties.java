package edu.tinkoff.ninjamireaclone.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@Data
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    private String secret;
    private Duration lifetime;
}
