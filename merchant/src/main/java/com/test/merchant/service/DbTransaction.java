package com.test.merchant.service;

import com.github.benmanes.caffeine.cache.Cache;
import com.test.merchant.dto.ExternalConfirmationDto;
import com.test.merchant.model.TransactionEntity;
import com.test.merchant.model.Transaction_Status;
import com.test.merchant.model.User;
import com.test.merchant.repository.TransactionRepo;
import com.test.merchant.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class DbTransaction {

    private final Cache<Long, TransactionEntity> transactionCache;
    private final TransactionRepo transactionRepo;
    private final UserRepo userRepo;

    @Transactional
    public void confirmTransaction(ExternalConfirmationDto dto) {
        Long txnId = Long.valueOf(dto.transactionId());
        TransactionEntity txn = transactionCache.getIfPresent(txnId);

        if (txn == null) return;

        Transaction_Status status = parseStatus(dto.status());
        if (status == null) return;

        User receiver = userRepo.findById(txn.getReceiverId())
                .orElseThrow(() -> new RuntimeException("Receiver user not found for ID: " + txn.getReceiverId()));

        if (status == Transaction_Status.SUCCESS) {
            addToUserBalance(receiver, txn.getAmount());
        }

        txn.setStatus(status);
        txn.setUpdatedAt(LocalDateTime.now().toString());

        transactionRepo.save(txn);
        userRepo.save(receiver);
        transactionCache.invalidate(txnId);
    }

    private Transaction_Status parseStatus(String status) {
        try {
            return Transaction_Status.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null; // invalid status
        }
    }

    private void addToUserBalance(User user, String txnAmountStr) {
        double current = Double.parseDouble(user.getAmount());
        double txnAmount = Double.parseDouble(txnAmountStr);
        user.setAmount(String.valueOf(current + txnAmount));
    }
}
