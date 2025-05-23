package com.test.oi.dto;

import lombok.Data;

@Data
public class PaymentRequest {
    private Long senderId;
    private String amount;
}
