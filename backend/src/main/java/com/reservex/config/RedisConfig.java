package com.reservex.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import java.time.Duration;

@Configuration
@EnableCaching
public class RedisConfig {

    @Bean
public StringRedisTemplate stringRedisTemplate(
        RedisConnectionFactory connectionFactory
) {
    return new StringRedisTemplate(
            connectionFactory
    );
}

    @Bean
    public CacheManager cacheManager(
            RedisConnectionFactory connectionFactory
    ) {

        GenericJackson2JsonRedisSerializer serializer =
                new GenericJackson2JsonRedisSerializer();

        RedisCacheConfiguration configuration =
                RedisCacheConfiguration.defaultCacheConfig()

                        // Cache entries automatically expire after 10 minutes.
                        // Prevents stale data from living forever in Redis.
                        .entryTtl(Duration.ofMinutes(10))

                        // Store cached objects as JSON.
                        // Allows DTOs to be serialized/deserialized cleanly.
                        .serializeValuesWith(
                                RedisSerializationContext
                                        .SerializationPair
                                        .fromSerializer(serializer)
                        );

        // Creates Redis-backed CacheManager used by:
        // @Cacheable
        // @CacheEvict
        // @CachePut
        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(configuration)
                .transactionAware()
                .build();
    }
}