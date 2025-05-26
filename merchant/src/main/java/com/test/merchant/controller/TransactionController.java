package com.test.merchant.controller;

import com.test.merchant.dto.ExternalConfirmationDto;
import com.test.merchant.dto.TransactionDto;
import com.test.merchant.model.TransactionEntity;
import com.test.merchant.service.DbTransaction;
import com.test.merchant.service.TransactionService_Old;
import com.test.merchant.service.TransactionService_V3;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/merchant/v1")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService_Old transactionService_old;
    private final TransactionService_V3 transactionService_v3;
    private final DbTransaction dbTransaction;

    @GetMapping("/transactions")
    public ResponseEntity<List<TransactionEntity>> findAll() {
        return ResponseEntity.ok(transactionService_old.getTransactions());
    }

    @PostMapping("/request")
    public ResponseEntity<String> requestTransaction(@RequestBody TransactionDto dto) {
        return transactionService_v3.createTransaction(dto);
    }

    @PostMapping("/external/confirm")
    public ResponseEntity<String> confirmTransaction(@RequestBody ExternalConfirmationDto dto) {
        dbTransaction.confirmTransaction(dto);
        return ResponseEntity.ok("Transaction updated.");
    }

}
