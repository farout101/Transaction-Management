package com.test.dps.service;

import com.test.dps.dto.Transaction;
import com.test.dps.repo.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class TransactionService {

    @Autowired
    TransactionRepository transactionRepository;

    RestTemplate restTemplate;

    @Value("${request.url}")
    String url;

    public List<Transaction> getAllTransaction() {
        return transactionRepository.getAllTransaction();
    }

    public Map<String, Object> makeTransaction(Transaction transaction) {
        return transactionRepository.createTransaction(transaction);
    }

    public Transaction getSingleTransaction(int id) {
        return transactionRepository.getSingleTransaction(id);
    }
}
