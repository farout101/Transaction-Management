package com.test.merchant.service;

import com.github.benmanes.caffeine.cache.Cache;
import com.test.merchant.dto.ExternalConfirmationDto;
import com.test.merchant.dto.ExternalStatusResponse;
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
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TransactionService_V2 {

    Logger log = LoggerFactory.getLogger(TransactionService_V2.class);

    private final Cache<Long, TransactionEntity> transactionCache;
    private final TransactionRepo transactionRepo;
    private final UserRepo userRepo;
    private final WebClient webClient;
    private final DbTransaction dbTransaction;

    @Async
    public void asyncWaitForServer(Long transactionId) throws InterruptedException {
        int[] waitDurations = {10, 5, 5};

        for (int waitDuration : waitDurations) {
            Thread.sleep(waitDuration * 1000);

            // internal check
            if (checkIfConfirmed(transactionId)) {
                log.info("Transaction {} was already confirmed internally. Breaking the wait loop.", transactionId);
                return;
            }

            // external check
            ExternalStatusResponse externalStatus = fetchStatusFromExternalServer(transactionId);
            if (externalStatus == null) {
                log.warn("External status check failed for transaction {}", transactionId);
                continue;
            }

            log.info("External status for transaction {} is {}", transactionId, externalStatus.status());

            if ("SUCCESS".equalsIgnoreCase(externalStatus.status())) {
                dbTransaction.confirmTransaction(new ExternalConfirmationDto(transactionId.toString(), "SUCCESS"));
                log.info("Transaction {} confirmed by external server. Breaking the wait loop.", transactionId);
                return;
            }
        }

        markTransactionFailed(transactionId);
        log.info("Transaction {} marked as FAILED after retrying. Ending the wait loop.", transactionId);
    }


    private ExternalStatusResponse fetchStatusFromExternalServer(Long transactionId) {
        try {
            return webClient
                    .get()
                    .uri("http://localhost:8080/payment/{id}/status", transactionId) // Update URL Thinan
                    .retrieve()
                    .bodyToMono(ExternalStatusResponse.class)
                    .block(); // it's okey cuz it's async I guess
        } catch (Exception e) {
            log.warn("Error fetching external status for txn {}: {}", transactionId, e.getMessage());
            return null;
        }
    }


    private boolean checkIfConfirmed(Long transactionId) {
        TransactionEntity txn = transactionCache.getIfPresent(transactionId);

        if (txn == null) {
            txn = transactionRepo.findById(transactionId).orElse(null);
        }

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
        if (!receiverExist(Long.valueOf(dto.receiverUserId())))
            return ResponseEntity.badRequest().body("Receiver does not exist");

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

    public boolean receiverExist(Long receiverId) {
        return userRepo.existsById(receiverId);
    }
}
