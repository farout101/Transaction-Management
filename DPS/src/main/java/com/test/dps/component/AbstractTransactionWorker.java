package com.test.dps.component;

import com.test.dps.component.waiting_transaction.WaitingTransactionQueue;
import com.test.dps.model.Transaction;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class AbstractTransactionWorker {

    @Value("${thread.worker.count}")
    private int threadCount;

    @PostConstruct
    public void startWorkers() {
        System.out.println("--------------------------------" + this.getClass().toString() + " threadCount: "+threadCount);
        // adjust based on your system
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    this.threadLooping();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }

    protected abstract void threadLooping() throws InterruptedException;
}
