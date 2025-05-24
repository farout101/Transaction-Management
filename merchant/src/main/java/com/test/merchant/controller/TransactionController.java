package com.test.merchant.controller;

import com.test.merchant.dto.ExternalConfirmationDto;
import com.test.merchant.dto.TransactionDto;
import com.test.merchant.model.TransactionEntity;
import com.test.merchant.service.DbTransaction;
import com.test.merchant.service.TransactionService_V2;
import com.test.merchant.service.TransactionService_Old;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/merchant/v1")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService_Old transactionService_old;
    private final TransactionService_V2 transactionService_v2;
    private final DbTransaction dbTransaction;

    @GetMapping("/transactions")
    public ResponseEntity<List<TransactionEntity>> findAll() {
        return ResponseEntity.ok(transactionService_old.getTransactions());
    }

    @PostMapping("/request")
    public ResponseEntity<String> requestTransaction(@RequestBody TransactionDto dto) throws InterruptedException {
        ResponseEntity<String> response = transactionService_v2.createTransaction(dto);
        transactionService_v2.asyncWaitForServer(Long.valueOf(dto.transactionId()));
        return response;
    }

    @PostMapping("/external/confirm")
    public ResponseEntity<String> confirmTransaction(@RequestBody ExternalConfirmationDto dto) {
        dbTransaction.confirmTransaction(dto);
        return ResponseEntity.ok("Transaction updated.");
    }

}
