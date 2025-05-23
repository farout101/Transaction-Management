package com.test.oi.dto;

import lombok.Data;

@Data
public class TransactionRequest {
    private Long fromId;
    private String amount;
    private Long toId;
}
