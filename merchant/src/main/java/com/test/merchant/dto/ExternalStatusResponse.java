package com.test.merchant.dto;

public record ExternalStatusResponse (
    String transactionId,
    String status
) {}

