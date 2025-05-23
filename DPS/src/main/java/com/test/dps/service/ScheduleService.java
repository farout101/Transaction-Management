package com.test.dps.service;

import com.test.dps.dto.GetTransactionResponse;
import com.test.dps.model.Transaction;
import com.test.dps.repo.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class ScheduleService {

    @Autowired
    TransactionRepository transactionRepository;

    @Autowired
    RestTemplate restTemplate;

    @Value("${request.url}")
    String url;

    @Autowired
    WebClient webClient;

    @Scheduled(initialDelay = 5000, fixedDelay = 1000)
    public void makeRequest() {
        List<Transaction> waitingTransaction = transactionRepository.getAllWaitingTransaction();
        if (waitingTransaction.isEmpty()) {
            return;
        }

        AtomicInteger success = new AtomicInteger(0);
        AtomicInteger failed = new AtomicInteger(0);

        List<Mono<Void>> calls = waitingTransaction.stream()
                .map(transaction ->
                        webClient.post()
                                .uri(url) // or full URL
                                .bodyValue(transaction)
                                .retrieve()
                                .bodyToMono(GetTransactionResponse.class)
                                .doOnSuccess(response -> {
                                    System.out.println("Calling the merchant successful");
                                    transactionRepository.updateTransactionStatus(transaction.getId(), "PENDING");
                                    success.incrementAndGet();
                                })
                                .doOnError(error -> {
                                    System.err.println("Error while calling merchant: " + error.getMessage());
                                    failed.incrementAndGet();
                                })
                                .onErrorResume(error -> Mono.empty())
                                .then()
                )
                .toList();

        Mono.when(calls)
                .doOnTerminate(() -> {
                    System.out.printf("Summary: %d successful, %d failed%n", success.get(), failed.get());
                })
                .block();
    }


}
