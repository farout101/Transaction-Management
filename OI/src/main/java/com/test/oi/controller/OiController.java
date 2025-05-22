package com.test.oi.controller;

import com.test.oi.dto.PaymentRequest;
import com.test.oi.dto.TransactionRequest;
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

    private final RestTemplate restTemplate;

    @PostMapping("/request")
    public ResponseEntity<String> request(@RequestBody TransactionRequest request) {

        String targetUrl = "http://localhost:8080/target-endpoint";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<TransactionRequest> entity = new HttpEntity<>(request, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(
                targetUrl, entity, String.class);

        return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
    }


    @PostMapping("/pay")
    public ResponseEntity<String> pay(@RequestBody PaymentRequest request) {
        return ResponseEntity.ok(Oiservice.pay(request.getId(), request.getAmount()));
    }

}
