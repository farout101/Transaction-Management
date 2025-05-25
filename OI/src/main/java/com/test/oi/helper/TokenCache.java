package com.test.oi.helper;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.test.oi.dto.TransactionRequest;
import com.test.oi.model.OiGroup;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@Component
public class TokenCache {

    private final Cache<Integer, TransactionRequest> cache;

    public TokenCache() {
        this.cache = Caffeine.newBuilder()
                .expireAfterWrite(5, TimeUnit.MINUTES)
                .maximumSize(10)
                .build();
    }

    public void putToken(Integer key, TransactionRequest token) {
        cache.put(key, token);
    }

    public TransactionRequest getToken(Integer key) {
        return cache.getIfPresent(key);
    }
}