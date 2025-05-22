package com.test.oi.controller;

import com.test.oi.dto.PaymentRequest;
import com.test.oi.dto.TransactionRequest;
import com.test.oi.service.Oiservice;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

@RestController
@RequestMapping("/oi/api/v1")
@RequiredArgsConstructor
public class OiController {

    private final Oiservice Oiservice;

    private final RestTemplate restTemplate;

    @PostMapping("/request")
    public ResponseEntity<String> request(@RequestBody TransactionRequest request) {

        String targetUrl = "http://localhost:8080/something,something";

        // Set headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Wrap the request
        HttpEntity<TransactionRequest> entity = new HttpEntity<>(request, headers);

        // Make the POST request to the external API
        ResponseEntity<String> response = restTemplate.postForEntity(targetUrl, entity, String.class);

        // Return the external API response
        return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
    }


    @PostMapping("/pay")
    public ResponseEntity<String> pay(@RequestBody PaymentRequest request) {
        return ResponseEntity.ok(Oiservice.pay(request.getId(), request.getAmount()));
    }

}
