package com.test.dps.component.waiting_transaction;


import com.test.dps.model.Transaction;
import com.test.dps.repo.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class WaitingTransactionProducer {

    @Autowired
    private WaitingTransactionQueue waitingTransactionQueue;

    @Autowired
    private TransactionRepository transactionRepository;

    @Scheduled(initialDelay = 5000, fixedDelay = 1000)
    public void fetchWaitingTransactions() {
        List<Transaction> waitingTransaction = transactionRepository.getAllWaitingTransaction();
        waitingTransaction.forEach((transaction) -> {
            transactionRepository.updateTransactionStatus(transaction.getId(), "PENDING");
            waitingTransactionQueue.submit(transaction);
        });
        System.out.println("Submitted " + waitingTransaction.size() + " transactions to queue.");
    }
}
