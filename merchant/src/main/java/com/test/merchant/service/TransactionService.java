package com.test.merchant.service;

import com.test.merchant.model.TransactionEntity;
import com.test.merchant.repository.TransactionRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepo transactionRepo;

    public List<TransactionEntity> getTransactions() {
        return transactionRepo.findAll();
    }
}
