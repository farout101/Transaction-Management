package com.test.oi.controller;

import com.test.oi.service.Oiservice;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/oi/api/v1")
@RequiredArgsConstructor
public class OiController {

    private final Oiservice Oiservice;

    @PostMapping("/{id}/{amount}")
    public ResponseEntity<String> Pay(@PathVariable Long id,@PathVariable String amount) {
        return ResponseEntity.ok(Oiservice.pay(id, amount));
    }
}
