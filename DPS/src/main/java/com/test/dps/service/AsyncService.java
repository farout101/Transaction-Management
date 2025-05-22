package com.test.dps.service;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class AsyncService {

    @Async
    public void postResponseTask() {
        System.out.println("Async task running after response");
    }
}