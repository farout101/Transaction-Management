package com.test.dps.component.pending_transaction;

import com.test.dps.component.AbstractTransactionWorker;
import com.test.dps.dto.GetTransactionResponse;
import com.test.dps.model.Transaction;
import com.test.dps.repo.TransactionRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;


@Component
@AllArgsConstructor
public class PendingTransactionWorker extends AbstractTransactionWorker {

    @Autowired
    private PendingTransactionQueue pendingTransactionQueue;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private WebClient webClient;

    @Value("${request.aplus.url}")
    private String aPlusUrl;

    @Value("${request.oi.url}")
    private String oiUrl;

    protected void threadLooping() throws InterruptedException {
        while (true) {
            if(pendingTransactionQueue.isEmpty()) {
                System.out.println("pendingTransactionQueue is Empty, Not working rn");
                Thread.sleep(1000);
                continue;
            }
            try {
                Transaction transaction = pendingTransactionQueue.take();
                processTransaction(transaction);
            } catch (Exception e) {
                System.err.println("Worker error: " + e.getMessage());
            }
        }
    }


    private void processTransaction(Transaction transaction) {
        webClient.post()
                .uri(oiUrl)
                .bodyValue(transaction)
                .exchangeToMono(clientResponse -> {
                    if (clientResponse.statusCode().is2xxSuccessful()) {
                        System.out.println("Success! Status: " + clientResponse.statusCode() + transaction);
                        return clientResponse.bodyToMono(GetTransactionResponse.class);
                    } else {
                        System.err.println("Failed! Status: " + clientResponse.statusCode() + transaction);
                        pendingTransactionQueue.submit(transaction); // Requeue if needed
                        return Mono.empty();
                    }
                })
                .doOnSuccess(res -> {
                    transactionRepository.updateTransactionStatus(transaction.getId(), "CASH TAKEN");
                    callToAPlus(transaction);
                })
                .doOnError(err -> {
                    System.err.println("Error processing transaction: " + err.getMessage());
                    pendingTransactionQueue.submit(transaction);
                })
                .onErrorResume(e -> Mono.empty())
                .subscribe();
    }


    private void callToAPlus(Transaction transaction) {
        webClient.post()
                .uri(aPlusUrl)
                .bodyValue(transaction)
                .exchangeToMono(clientResponse -> {
                    if (clientResponse.statusCode().is2xxSuccessful()) {
                        System.out.println("Success! Status: " + clientResponse.statusCode() + transaction);
                        return clientResponse.bodyToMono(GetTransactionResponse.class);
                    } else {
                        System.err.println("Failed! Status: " + clientResponse.statusCode() + transaction);
                        pendingTransactionQueue.submit(transaction); // Requeue if needed
                        return Mono.empty();
                    }
                })
                .doOnSuccess(res -> {
                    transactionRepository.updateTransactionStatus(transaction.getId(), "SUCCESS");
                })
                .doOnError(err -> {
                    System.err.println("Error processing transaction: " + err.getMessage());
                    pendingTransactionQueue.submit(transaction);
                })
                .onErrorResume(e -> Mono.empty())
                .subscribe();
    }
}
