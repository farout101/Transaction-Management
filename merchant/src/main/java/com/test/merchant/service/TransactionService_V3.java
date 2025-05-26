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
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
public class TransactionService_V3 {

    Logger log = LoggerFactory.getLogger(TransactionService_V3.class);

    private final Cache<Long, TransactionEntity> transactionCache;
    private final TransactionRepo transactionRepo;
    private final UserRepo userRepo;
    private final WebClient webClient;
    private final DbTransaction dbTransaction;
    private final TaskScheduler taskScheduler;

    private static final int MAX_ATTEMPTS = 3;
    private static final int[] WAIT_SECONDS = {10, 5, 5};

    public void scheduleTransactionCheck(Long transactionId) {
        AtomicInteger attempt = new AtomicInteger(0);
        scheduleCheck(transactionId, attempt);
    }

    private void scheduleCheck(Long txnId, AtomicInteger attemptCounter) {
        int currentAttempt = attemptCounter.getAndIncrement(); // get as 0 and then 1

        if (currentAttempt >= MAX_ATTEMPTS) {
            log.warn("Transaction {} reached max attempts. Marking as failed.", txnId);
            markTransactionFailed(txnId);
            return;
        }

        int delaySeconds = WAIT_SECONDS[currentAttempt];
        log.info("Scheduling transaction {} check in {} seconds (attempt {}/{})", txnId, delaySeconds, currentAttempt + 1, MAX_ATTEMPTS);

        taskScheduler.schedule(() -> {
            if (checkIfConfirmed(txnId)) {
                log.info("Transaction {} already confirmed or finalized. Skipping further checks.", txnId);
                return;
            }

            // Fetch status from external server
            ExternalStatusResponse response = fetchStatusFromExternalServer(txnId);
            if (response == null) {
                log.warn("Failed to fetch external status for txn {}. Retrying...", txnId);
                scheduleCheck(txnId, attemptCounter); // retry next
                return;
            }

            log.info("Fetched external status for txn {}: {}", txnId, response.status());

            if ("SUCCESS".equalsIgnoreCase(response.status())) {
                dbTransaction.confirmTransaction(new ExternalConfirmationDto(txnId.toString(), "SUCCESS"));
                log.info("Transaction {} confirmed by external status check.", txnId);
            }
            else if ("FAIL".equalsIgnoreCase(response.status())) {
                log.warn("Transaction {} marked as FAIL by external status. Stopping retries.", txnId);
                markTransactionFailed(txnId);
            }
            else {
                scheduleCheck(txnId, attemptCounter);
            }

        }, Instant.now().plusSeconds(delaySeconds));

    }

    private ExternalStatusResponse fetchStatusFromExternalServer(Long transactionId) {
        try {
            return webClient
                    .get()
                    .uri("http://localhost:8080/payment/{id}/status", transactionId)
                    .retrieve()
                    .bodyToMono(ExternalStatusResponse.class)
                    .block();  // since the scheduler is Async I think the block is possible:)
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

    public ResponseEntity<String> createTransaction(TransactionDto dto) {
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

        transactionRepo.save(txn); // Persist to DB
        transactionCache.put(txn.getTransactionId(), txn);

        scheduleTransactionCheck(txn.getTransactionId()); // Schedule check
        return ResponseEntity.ok("Transaction stored and scheduled for confirmation.");
    }

    public boolean receiverExist(Long receiverId) {
        return userRepo.existsById(receiverId);
    }
}
