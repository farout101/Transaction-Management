package com.test.merchant.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.test.merchant.model.TransactionEntity;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class CacheConfig {

    @Bean
    public Cache<Long, TransactionEntity> transactionCache() {
        return Caffeine.newBuilder()
                .expireAfterWrite(200, TimeUnit.SECONDS)
                .maximumSize(10000)
                .build();
    }
}
