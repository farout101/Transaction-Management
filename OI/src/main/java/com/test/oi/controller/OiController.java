package com.test.oi.controller;

import com.test.oi.dto.AcceptTransactionDTO;
import com.test.oi.dto.PaymentRequest;
import com.test.oi.dto.TransactionRequest;
import com.test.oi.service.LogCleaner;
import com.test.oi.service.Oiservice;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/oi/api/v1")
@RequiredArgsConstructor
public class OiController {

    private final Oiservice Oiservice;
    private final LogCleaner logCleaner;
    private final Oiservice oiservice;

    // Make a request to the DPI server to ask for transaction token
    @PostMapping("/request")
    public ResponseEntity<String> requestToken(@RequestBody TransactionRequest request) throws Exception {
        String route = "/payment/start";
        return oiservice.requestToken(request, route);
    }

    // Get the latest transaction status
    @GetMapping("/{id}/latest")
    public ResponseEntity<String> getToken(@PathVariable int id) {
        String token = oiservice.getCachedToken(id).toString();
        if (token == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(token);
    }

    // Transaction is successful and take the money for the users
    @PostMapping("/pay")
    public ResponseEntity<String> pay(@RequestBody AcceptTransactionDTO request) {
        System.out.println("-------------------------------------------Successful-Transaction-id="
                + request.id());
        return ResponseEntity.ok(Oiservice.pay(request.id()));
    }

    //Clean the log
    @GetMapping("/clean")
    public ResponseEntity<String> clean() throws Exception {
        logCleaner.clean();
        return ResponseEntity.ok("Cleaned");
    }

}
