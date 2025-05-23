package com.test.dps.dto;

public record StartTransactionRequest(
        Long senderId,
        OiGroup senderGroup,
        String amount,
        Long receiverId
) {}
