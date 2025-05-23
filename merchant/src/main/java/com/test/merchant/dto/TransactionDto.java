package com.test.merchant.dto;

import java.time.LocalDateTime;

public record TransactionDto (
     Integer transactionId,
     String senderUserId,
     String senderUserGroup,
     String receiverUserId,
     String receiverUserGroup,
     String status,
     LocalDateTime dateTime,
     double amount
) {}
