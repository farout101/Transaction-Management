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

import java.math.BigDecimal;
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

        if (txn != null && "SUCCESS".equalsIgnoreCase(dto.status())) {
            User user = userRepo.findById(txn.getReceiverId())
                    .orElseThrow(() -> new RuntimeException("Receiver user not found for ID: " + txn.getReceiverId()));

            Double userBalance = Double.valueOf((user.getAmount()));
            Double txnAmount = Double.valueOf(txn.getAmount());
            user.setAmount(String.valueOf(userBalance + txnAmount));

            // for transaction
            txn.setStatus(Transaction_Status.SUCCESS);
            txn.setUpdatedAt(LocalDateTime.now().toString());

            // save
            transactionRepo.save(txn);
            userRepo.save(user);

            // clear cache
            transactionCache.invalidate(txnId);
        }
    }

}
