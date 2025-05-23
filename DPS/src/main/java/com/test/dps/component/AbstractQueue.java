package com.test.dps.component;

import com.test.dps.model.Transaction;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;


public abstract class AbstractQueue {
    protected final BlockingQueue<Transaction> queue = new LinkedBlockingQueue<>();

    public boolean isEmpty() {
        return queue.isEmpty();
    }

    public void submit(Transaction transaction) {
        queue.offer(transaction); // or .put(transaction) if you want blocking behavior
    }

    public Transaction take() throws InterruptedException {
        return queue.take();
    }
}
