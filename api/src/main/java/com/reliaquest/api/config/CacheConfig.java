package com.reliaquest.api.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Caching configuration for improved performance and scalability.
 * Uses in-memory caching to reduce API calls to the mock server.
 */
@Configuration
@EnableCaching
public class CacheConfig {

    /**
     * Creates a cache manager for employee data caching.
     * This improves scalability by reducing external API calls.
     *
     * @return the cache manager
     */
    @Bean
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager("employees", "employee");
    }
}
