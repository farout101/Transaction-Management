package com.test.oi.dto;

import com.test.oi.model.OiGroup;
import lombok.Data;

@Data
public class TransactionRequest {
    private Long senderId;
    private OiGroup senderGroup;
    private String amount;
    private Long receiverId;
}
