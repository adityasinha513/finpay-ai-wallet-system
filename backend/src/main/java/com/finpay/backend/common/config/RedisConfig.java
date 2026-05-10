package com.finpay.backend.common.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * Redis-backed {@link CacheManager} when {@code spring.cache.type=redis} (see {@code application.yaml}).
 * Caching is enabled on {@link com.finpay.backend.BackendApplication}; this bean supplies the
 * distributed implementation so {@code @Cacheable} / {@code @CacheEvict} hit Redis in production.
 * <p>
 * Cache names {@code wallets} and {@code walletBalance} match {@link com.finpay.backend.wallet.service.WalletService}.
 * TTLs: wallet snapshot slightly longer than balance-only reads. {@code transactionAware()} aligns eviction with DB commits.
 */
@Configuration
@ConditionalOnProperty(
        name = "spring.cache.type",
        havingValue = "redis"
)
public class RedisConfig {

    @Bean
    public CacheManager cacheManager(
            RedisConnectionFactory connectionFactory
    ) {

        RedisCacheConfiguration defaults = RedisCacheConfiguration
                .defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(10))
                .disableCachingNullValues()
                .serializeKeysWith(
                        RedisSerializationContext.SerializationPair
                                .fromSerializer(new StringRedisSerializer())
                )
                .serializeValuesWith(
                        RedisSerializationContext.SerializationPair
                                .fromSerializer(
                                        new GenericJackson2JsonRedisSerializer()
                                )
                );

        Map<String, RedisCacheConfiguration> perCache = new HashMap<>();
        perCache.put(
                "wallets",
                defaults.entryTtl(Duration.ofMinutes(15))
        );
        perCache.put(
                "walletBalance",
                defaults.entryTtl(Duration.ofMinutes(5))
        );

        return RedisCacheManager
                .builder(connectionFactory)
                .cacheDefaults(defaults)
                .withInitialCacheConfigurations(perCache)
                .transactionAware()
                .build();
    }
}
