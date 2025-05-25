package com.test.dps.component;

import com.test.dps.component.waiting_transaction.WaitingTransactionQueue;
import com.test.dps.model.Transaction;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public abstract class AbstractTransactionWorker {

    @Value("${thread.worker.count}")
    private int threadCount;

    @Autowired
    private WebClient webClient;

    @PostConstruct
    public void startWorkers() {
        System.out.println("--------------------------------" + this.getClass().toString() + " threadCount: "+threadCount);
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    while (true) {
                        this.threadLooping();
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }

    protected void makePostTransactionRequest(
            String URL,
            Transaction transaction,
            Consumer<Throwable> onError,
            Runnable afterCompletion
    ) {
        webClient.post()
                .uri(URL)
                .bodyValue(transaction)
                .exchangeToMono(clientResponse -> {
                    if (clientResponse.statusCode().is2xxSuccessful()) {
                        System.out.println("Success! Status: " + clientResponse.statusCode() + transaction);
                        return Mono.just("Successful");
                    } else {
                        System.err.println("Failed! Status: " + clientResponse.statusCode() + transaction);
                        return Mono.error(new HttpClientErrorException(
                                clientResponse.statusCode(), "Something went wrong with " + transaction));
                    }
                }).subscribe((a) -> {}, onError, afterCompletion);
    }

    protected abstract void threadLooping() throws InterruptedException;
}
