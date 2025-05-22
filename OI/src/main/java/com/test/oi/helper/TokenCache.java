package com.test.oi.helper;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class TokenCache {

    private final Cache<String, String> cache;

    public TokenCache() {
        this.cache = Caffeine.newBuilder()
                .expireAfterWrite(5, TimeUnit.MINUTES)
                .maximumSize(10)
                .build();
    }

    public void putToken(String key, String token) {
        cache.put(key, token);
    }

    public String getToken(String key) {
        return cache.getIfPresent(key);
    }
}
