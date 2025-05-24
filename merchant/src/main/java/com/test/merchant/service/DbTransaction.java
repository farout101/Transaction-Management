package com.test.merchant.service;

import com.github.benmanes.caffeine.cache.Cache;
import com.test.merchant.dto.ExternalConfirmationDto;
import com.test.merchant.model.TransactionEntity;
import com.test.merchant.model.Transaction_Status;
import com.test.merchant.repository.TransactionRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class DbTransaction {

    private final Cache<Long, TransactionEntity> transactionCache;
    private final TransactionRepo transactionRepo;

    @Transactional
    public void confirmTransaction(ExternalConfirmationDto dto) {
        Long txnId = Long.valueOf(dto.transactionId());
        TransactionEntity txn = transactionCache.getIfPresent(txnId);
        if (txn != null && dto.status().equals("SUCCESS")) {
            txn.setStatus(Transaction_Status.SUCCESS);
            txn.setUpdatedAt(LocalDateTime.now().toString());
            transactionRepo.save(txn);
            transactionCache.invalidate(txnId);
        }
    }
}
