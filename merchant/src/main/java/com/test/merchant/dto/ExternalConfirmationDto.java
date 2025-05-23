package com.test.merchant.dto;

public record ExternalConfirmationDto(
        String transactionId,
        String status
) {}
