package com.test.dps.controller;

import com.test.dps.dto.Transaction;
import com.test.dps.service.AsyncService;
import com.test.dps.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class PaymentController {

    @Autowired
    TransactionService transactionService;

    @Autowired
    AsyncService asyncService;

    @GetMapping("all")
    public List<Transaction> getAllTransactions() {
        return transactionService.getAllTransaction();
    }

    @PostMapping("/payment/start")
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, Object> makeTransaction(@RequestBody Transaction transaction) {
        Map<String, Object> mso;
        try {
            mso = transactionService.makeTransaction(transaction);
        } catch (Exception e) {
            e.printStackTrace();
            throw new Error("Something went wrong");
        }
        return mso;
    }

    @ResponseBody
    @GetMapping("payment/{id}/status")
    public Transaction requestStatus(@PathVariable int id) {
        return transactionService.getSingleTransaction(id);
    }
}
