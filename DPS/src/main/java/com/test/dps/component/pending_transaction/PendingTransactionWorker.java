package com.test.dps.component.pending_transaction;

import com.test.dps.component.AbstractTransactionWorker;
import com.test.dps.dto.GetTransactionResponse;
import com.test.dps.model.Transaction;
import com.test.dps.repo.TransactionRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.function.Consumer;


@Component
@AllArgsConstructor
@RequiredArgsConstructor
public class PendingTransactionWorker extends AbstractTransactionWorker {



    @Autowired
    private PendingTransactionQueue pendingTransactionQueue;

    @Autowired
    private TransactionRepository transactionRepository;

    @Value("${request.aplus.url}")
    private String aPlusUrl;

    @Value("${request.aplus.url.path.confirm}")
    private String confirmTransactionAPlusUrlPath;

    @Value("${request.oi.url}")
    private String oiUrl;

    @Value("${request.oi.url.path.confirm}")
    private String confirmTransactionOiUrlPath;

    protected void threadLooping() throws InterruptedException {
            if(pendingTransactionQueue.isEmpty()) {
                System.out.println("pendingTransactionQueue is Empty, Not working rn");
                Thread.sleep(2000);
            } else {
                try {
                    Transaction transaction = pendingTransactionQueue.take();
                    processTransaction(transaction);
                } catch (Exception e) {
                    System.err.println("Worker error: " + e.getMessage());
                }
            }
    }

    private void processTransaction(Transaction transaction) {
        makePostTransactionRequest(
                oiUrl + confirmTransactionOiUrlPath,
                transaction,
                (e) -> e.getMessage(),
                () -> {callToAPlus(transaction);}
        );

    }


    private void callToAPlus(Transaction transaction) {
        makePostTransactionRequest(
                aPlusUrl + confirmTransactionAPlusUrlPath,
                transaction,
                (e) -> e.getMessage(),
                () -> {}
                );
    }

}
