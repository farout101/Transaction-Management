package com.test.merchant.service;

import com.test.merchant.dto.TransactionDto;
import com.test.merchant.model.TransactionEntity;
import com.test.merchant.model.Transaction_Status;
import com.test.merchant.repository.TransactionRepo;
import com.test.merchant.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepo transactionRepo;
    private final UserRepo userRepo;

    public List<TransactionEntity> getTransactions() {
        return transactionRepo.findAll();
    }

    public ResponseEntity<String> createTransaction(TransactionDto transactionDto) {
        TransactionEntity transactionEntity = new TransactionEntity();
        transactionEntity.setSenderId(Long.valueOf(transactionDto.senderUserId()));
        transactionEntity.setSenderGroup(transactionDto.senderUserGroup());
        transactionEntity.setAmount(String.valueOf(transactionDto.amount()));
        if(receiverExist(Long.valueOf(transactionDto.receiverUserId()))) {
            transactionEntity.setReceiverId(Long.valueOf(transactionDto.receiverUserId()));
        } else {
            return ResponseEntity.badRequest().body("Receiver does not exist");
        }
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
}
