package com.test.dps.service;

import com.test.dps.dto.Transaction;
import com.test.dps.repo.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionService {

    TransactionRepository transactionRepository;

    RestTemplate restTemplate;

    @Value("${request.method.url}")
    String url;

    public List<Transaction> getAllTransaction() {
        return transactionRepository.getAllTransaction();
    }
}
