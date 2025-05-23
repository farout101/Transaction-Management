package com.test.dps.service;

import com.test.dps.dto.StartTransactionRequest;
import com.test.dps.model.Transaction;
import com.test.dps.repo.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class TransactionService {

    @Autowired
    TransactionRepository transactionRepository;

    public List<Transaction> getAllTransaction() {
        return transactionRepository.getAllTransaction();
    }

    public Map<String, Object> makeTransaction(StartTransactionRequest startTransactionRequest) {
        Transaction transaction = new Transaction();
        transaction.setSenderUserId(startTransactionRequest.senderId().toString());
        transaction.setSenderUserGroup(startTransactionRequest.senderGroup().io_group());
        transaction.setAmount(Double.parseDouble(startTransactionRequest.amount()));
        transaction.setReceiverUserId(startTransactionRequest.receiverId().toString());
        transaction.setReceiverUserGroup("A+ Wallet");

        Map<String, Object> mso =  transactionRepository.createTransaction(transaction);
        mso.put("token", mso.get("ID"));
        return mso;
    }

    public Transaction getSingleTransaction(int id) {
        return transactionRepository.getSingleTransaction(id);
    }
}
