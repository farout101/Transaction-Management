package com.test.merchant.service;

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

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TransactionService_Old {

    Logger log = LoggerFactory.getLogger(TransactionService_Old.class);

    private final TransactionRepo transactionRepo;
    private final UserRepo userRepo;

    public List<TransactionEntity> getTransactions() {
        return transactionRepo.findAll();
    }

    @Async
    public void asyncWaitForServer(Long transactionId) throws InterruptedException {
        int[] waitDurations = {36, 12, 12};
        for (int waitDuration : waitDurations) {
            boolean responded = waitForResponse(transactionId, waitDuration);
            if (responded) return; // Response received, done.
        }
        markTransactionFailed(transactionId); // No response => fail.
    }

    private boolean waitForResponse(Long transactionId, int timeoutSeconds) throws InterruptedException {
        for (int i = 0; i < timeoutSeconds; i++) {
            if (checkIfResponseReceived(transactionId)) {
                return true;
            }
            Thread.sleep(1000); // sleep 1s
        }
        return false;
    }

    private boolean checkIfResponseReceived(Long transactionId) {
        Optional<TransactionEntity> txn = transactionRepo.findById(transactionId);
        return txn.isPresent() && !txn.get().getStatus().equals(Transaction_Status.PENDING);
    }


    private void markTransactionFailed(Long transactionId) {
        Optional<TransactionEntity> txn = transactionRepo.findById(transactionId);
        if (txn.isPresent()) {
            TransactionEntity transactionEntity = txn.get();
            transactionEntity.setStatus(Transaction_Status.FAIL);
            transactionRepo.save(transactionEntity);
            log.info("Transaction {} marked as failed.", transactionId);
        } else {
            log.warn("Transaction {} not found for failure marking.", transactionId);
        }
    }

    public ResponseEntity<String> createTransaction(TransactionDto transactionDto) {
        TransactionEntity transactionEntity = new TransactionEntity();
        if(receiverExist(Long.valueOf(transactionDto.receiverUserId()))) {
            transactionEntity.setReceiverId(Long.valueOf(transactionDto.receiverUserId()));
        } else {
            return ResponseEntity.badRequest().body("Receiver does not exist");
        }
        transactionEntity.setTransactionId(Long.valueOf(transactionDto.transactionId()));
        transactionEntity.setSenderId(Long.valueOf(transactionDto.senderUserId()));
        transactionEntity.setSenderGroup(transactionDto.senderUserGroup());
        transactionEntity.setAmount(String.valueOf(transactionDto.amount()));
        transactionEntity.setReceiverGroup(transactionDto.receiverUserGroup());
        transactionEntity.setStatus(Transaction_Status.PENDING);
        transactionEntity.setCreatedAt(java.time.LocalDateTime.now().toString());
        transactionEntity.setUpdatedAt(java.time.LocalDateTime.now().toString());
        transactionRepo.save(transactionEntity);
        return ResponseEntity.ok("Transaction Created");
    }


    public boolean receiverExist(Long receiverId) {
        return userRepo.existsById(receiverId);
    }

    public void confirmTransaction(ExternalConfirmationDto dto) {
        TransactionEntity txn = transactionRepo.findById(Long.valueOf(dto.transactionId())).orElseThrow();
        txn.setStatus(Transaction_Status.SUCCESS);
        transactionRepo.save(txn);
    }
}
