package com.reservex.config;

import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import java.time.Duration;

/**
 * Redis cache configuration for Spring Cache.
 *
 * Why this class?
 * - Enables Redis as the backing store for Spring's @Cacheable annotations.
 * - Configures cache expiration (TTL).
 * - Configures JSON serialization for cached objects.
 *
 * Without this configuration:
 * - Spring may use default serialization.
 * - Cached objects may not be stored/read correctly.
 * - Cache entries would never expire unless manually removed.
 */
@Configuration
public class RedisCacheConfig {

    @Bean
    public CacheManager cacheManager(
            RedisConnectionFactory connectionFactory
    ) {

        RedisCacheConfiguration config =
                RedisCacheConfiguration.defaultCacheConfig()
                  
                        // Cache entries automatically expire after 10 minutes.
                        // Prevents stale data from living forever in Redis.
                        .entryTtl(Duration.ofMinutes(10))

                        // Store cached objects as JSON.
                        // Allows DTOs to be serialized/deserialized cleanly.
                        .serializeValuesWith(
                                RedisSerializationContext
                                        .SerializationPair
                                        .fromSerializer(
                                                new GenericJackson2JsonRedisSerializer()
                                        )
                        );

        // Creates Redis-backed CacheManager used by:
        // @Cacheable
        // @CacheEvict
        // @CachePut
        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(config)
                .build();
    }
}