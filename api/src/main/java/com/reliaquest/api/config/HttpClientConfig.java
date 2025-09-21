package com.reliaquest.api.config;

import java.time.Duration;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.web.client.RestTemplate;

/**
 * Configuration class for HTTP client setup.
 * Includes timeout configuration and retry mechanism for scalability.
 */
@Configuration
@EnableRetry
public class HttpClientConfig {

    /**
     * Creates a configured RestTemplate bean with appropriate timeouts.
     * These timeouts help with scalability and prevent hanging requests.
     *
     * @param builder the RestTemplateBuilder
     * @return configured RestTemplate
     */
    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.setConnectTimeout(Duration.ofSeconds(10))
                .setReadTimeout(Duration.ofSeconds(30))
                .build();
    }
}
