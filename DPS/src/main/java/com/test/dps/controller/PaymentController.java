package com.test.dps.controller;

import com.test.dps.dto.Transaction;
import com.test.dps.repo.TransactionRepository;
import com.test.dps.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class PaymentController {

    TransactionService transactionService;

    @GetMapping("all")
    public List<Transaction> getAllTransactions() {
        return transactionService.getAllTransaction();
    }

    @PostMapping("make_payment")
    public int makeTransaction(@RequestBody Transaction transaction) {
        return transactionService.makeTransaction(transaction);
    }
}
