package com.test.dps.component.waiting_transaction;

import com.test.dps.component.AbstractTransactionWorker;
import com.test.dps.component.pending_transaction.PendingTransactionQueue;
import com.test.dps.dto.GetTransactionResponse;
import com.test.dps.model.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;


@Component
public class WaitingTransactionWorker extends AbstractTransactionWorker {

    @Autowired
    private WaitingTransactionQueue waitingTransactionQueue;

    @Autowired
    private WebClient webClient;

    @Autowired
    private PendingTransactionQueue pendingTransactionQueue;

    @Value("${request.aplus.url}")
    private String aPlusUrl;


    protected void threadLooping() throws InterruptedException {
        while (true) {
            if(waitingTransactionQueue.isEmpty()) {
                //System.out.println("waitingTransactionQueue is Empty, Not working rn");
                Thread.sleep(2000);
                continue;
            }
            try {
                Transaction transaction = waitingTransactionQueue.take();
                processTransaction(transaction);
            } catch (Exception e) {
                System.err.println("Worker error: " + e.getMessage());
            }
        }
    }


    private void processTransaction(Transaction transaction) {
        webClient.post()
                .uri(aPlusUrl)
                .bodyValue(transaction)
                .exchangeToMono(clientResponse -> {
                    if (clientResponse.statusCode().is2xxSuccessful()) {
                        System.out.println("Success! Status: " + clientResponse.statusCode() + transaction);
                        return clientResponse.bodyToMono(GetTransactionResponse.class);
                    } else {
                        System.err.println("Failed! Status: " + clientResponse.statusCode() + transaction);
                        pendingTransactionQueue.submit(transaction);
                        return Mono.empty();
                    }
                })
                .doOnSuccess(res -> {
                    pendingTransactionQueue.submit(transaction);
                })
                .doOnError(err -> {
                    System.err.println("Error processing transaction: " + err.getMessage());
                    waitingTransactionQueue.submit(transaction);
                })
                .onErrorResume(e -> Mono.empty())
                .subscribe();
    }

}
