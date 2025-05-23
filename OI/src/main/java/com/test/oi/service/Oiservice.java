package com.test.oi.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.test.oi.dto.TransactionRequest;
import com.test.oi.helper.TokenCache;
import com.test.oi.model.IndividualUser;
import com.test.oi.repository.IndiUserRepo;
import com.test.oi.repository.OiRepo;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class Oiservice {

    private final OiRepo oiRepo;
    private final IndiUserRepo indiUserRepo;
    private final TokenCache tokenCache;
    private final RestTemplate restTemplate;

    @Value("${target.url}")
    String targetUrl;

    Logger log = LoggerFactory.getLogger(Oiservice.class);

    public String pay(Long id, String amount) {
        IndividualUser user = indiUserRepo.findById(id).orElse(null);

        if (user != null) {
            String currentAmount = user.getAmount();
            // Assuming amount is a string that can be converted to a number
            double newAmount = Double.parseDouble(currentAmount) - Double.parseDouble(amount);
            user.setAmount(String.valueOf(newAmount));
            indiUserRepo.save(user);
            log.info("Payment successful. User ID: {}, New Amount: {}", id, newAmount);
            return "Payment successful. New amount: " + newAmount;
        } else {
            log.debug("User not found. ID: {}", id);
            return "User not found.";
        }
    }

    public ResponseEntity<String> requestToken(TransactionRequest request, String route) throws Exception {
        // Prepare headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Prepare body
        HttpEntity<TransactionRequest> entity = new HttpEntity<>(request, headers);

        // Send request
        ResponseEntity<String> response = restTemplate.postForEntity(
                targetUrl + route,
                entity,
                String.class);

        // Extract and cache token
        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response.getBody());
            String token = root.path("token").asText();
            System.out.println("-----------------------------------------------------"+token);
            tokenCache.putToken("latestToken", token);  // Use a custom key if needed
        }

        return response;
    }

    public String getCachedToken() {
        return tokenCache.getToken("latestToken");
    }
}
