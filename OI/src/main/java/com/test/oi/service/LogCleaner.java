package com.test.oi.service;

import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Paths;

@Component
public class LogCleaner {

    public void clean() throws Exception {
        Files.deleteIfExists(Paths.get("logs/oi.log"));
    }
}