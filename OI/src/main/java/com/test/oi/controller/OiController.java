package com.test.oi.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/oi/api/v1")
public class OiController {

    @GetMapping("")
    public ResponseEntity<String> getOi() {
        return ResponseEntity.ok("");
    }
}
