package com.test.merchant.controller;

import com.test.merchant.model.TransactionEntity;
import com.test.merchant.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/merchant/v1")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @GetMapping("/transactions")
    public ResponseEntity<List<TransactionEntity>> findAll() {
        return ResponseEntity.ok(transactionService.getTransactions());
    }
}
