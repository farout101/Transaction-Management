package com.test.merchant.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class TransactionEntity {

    @Id
    private Long transactionId;

    private Long senderId;
    private String senderGroup;
    private String amount;
    private Long receiverId;
    private String receiverGroup;

    @Enumerated(EnumType.STRING)
    private Transaction_Status status;

    private String createdAt;
    private String updatedAt;

//    @ManyToOne
//    @JoinColumn(name = "user_id")
//    private User user;
}

