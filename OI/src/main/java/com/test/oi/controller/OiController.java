package com.test.oi.controller;

import com.test.oi.dto.PaymentRequest;
import com.test.oi.dto.TransactionRequest;
import com.test.oi.helper.TokenCache;
import com.test.oi.service.LogCleaner;
import com.test.oi.service.Oiservice;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/oi/api/v1")
@RequiredArgsConstructor
public class OiController {

    private final Oiservice Oiservice;
    private final LogCleaner logCleaner;
    private final Oiservice oiservice;

    @PostMapping("/request")
    public ResponseEntity<String> requestToken(@RequestBody TransactionRequest request) throws Exception {
        return oiservice.requestToken(request);
    }

    @GetMapping("/latest")
    public ResponseEntity<String> getToken() {
        String token = oiservice.getCachedToken();
        if (token == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(token);
    }


    @PostMapping("/pay")
    public ResponseEntity<String> pay(@RequestBody PaymentRequest request) {
        return ResponseEntity.ok(Oiservice.pay(request.getSenderId(), request.getAmount()));
    }

    @GetMapping("/clean")
    public ResponseEntity<String> clean() throws Exception {
        logCleaner.clean();
        return ResponseEntity.ok("Cleaned");
    }

}
