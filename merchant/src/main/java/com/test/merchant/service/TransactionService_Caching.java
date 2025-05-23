package com.test.merchant.service;

import com.github.benmanes.caffeine.cache.Cache;
import com.test.merchant.dto.ExternalConfirmationDto;
import com.test.merchant.dto.TransactionDto;
import com.test.merchant.model.TransactionEntity;
import com.test.merchant.model.Transaction_Status;
import com.test.merchant.repository.TransactionRepo;
import com.test.merchant.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TransactionService_Caching {

    Logger log = LoggerFactory.getLogger(TransactionService_Caching.class);

    private final Cache<Long, TransactionEntity> transactionCache;
    private final TransactionRepo transactionRepo;
    private final UserRepo userRepo;

    @Async
    public void asyncWaitForServer(Long transactionId) throws InterruptedException {
        int[] waitDurations = {36, 12, 12};
        for (int waitDuration : waitDurations) {
            if (checkIfConfirmed(transactionId)) return;
            Thread.sleep(waitDuration * 1000);
        }
        markTransactionFailed(transactionId);
    }

    private boolean checkIfConfirmed(Long transactionId) {
        TransactionEntity txn = transactionCache.getIfPresent(transactionId);
        return txn != null && txn.getStatus() != Transaction_Status.PENDING;
    }

    private void markTransactionFailed(Long transactionId) {
        TransactionEntity txn = transactionCache.getIfPresent(transactionId);
        if (txn != null) {
            txn.setStatus(Transaction_Status.FAIL);
            txn.setUpdatedAt(LocalDateTime.now().toString());
            transactionRepo.save(txn);
            transactionCache.invalidate(transactionId);
        }
    }

    public ResponseEntity<String> createTransaction(TransactionDto dto) throws InterruptedException {
        if (!receiverExist(Long.valueOf(dto.receiverUserId()))) {
            return ResponseEntity.badRequest().body("Receiver does not exist");
        }

        TransactionEntity txn = new TransactionEntity();
        txn.setTransactionId(Long.valueOf(dto.transactionId()));
        txn.setSenderId(Long.valueOf(dto.senderUserId()));
        txn.setReceiverId(Long.valueOf(dto.receiverUserId()));
        txn.setSenderGroup(dto.senderUserGroup());
        txn.setReceiverGroup(dto.receiverUserGroup());
        txn.setAmount(String.valueOf(dto.amount()));
        txn.setStatus(Transaction_Status.PENDING);
        txn.setCreatedAt(LocalDateTime.now().toString());
        txn.setUpdatedAt(LocalDateTime.now().toString());

        transactionCache.put(txn.getTransactionId(), txn);

        return ResponseEntity.ok("Transaction temporarily stored.");
    }

    public void confirmTransaction(ExternalConfirmationDto dto) {
        if(dto.status().equals("SUCCESS")) {
            TransactionEntity txn = transactionCache.getIfPresent(Long.valueOf(dto.transactionId()));
            if (txn != null) {
                txn.setStatus(Transaction_Status.SUCCESS);
                txn.setUpdatedAt(LocalDateTime.now().toString());
                transactionRepo.save(txn);
                transactionCache.invalidate(Long.valueOf(dto.transactionId()));
            }
        }
    }

    public boolean receiverExist(Long receiverId) {
        return userRepo.existsById(receiverId);
    }
}
