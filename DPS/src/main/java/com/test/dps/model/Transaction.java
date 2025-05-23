package com.test.dps.model;

import java.time.LocalDateTime;


public class Transaction {
    private int id;
    private String senderUserId;
    private String senderUserGroup;
    private String receiverUserId;
    private String receiverUserGroup;
    private String status;
    private LocalDateTime dateTime;
    private double amount;

    public Transaction() {}

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getReceiverUserGroup() {
        return receiverUserGroup;
    }

    public void setReceiverUserGroup(String receiverUserGroup) {
        this.receiverUserGroup = receiverUserGroup;
    }

    public String getReceiverUserId() {
        return receiverUserId;
    }

    public void setReceiverUserId(String receiverUserId) {
        this.receiverUserId = receiverUserId;
    }

    public String getSenderUserGroup() {
        return senderUserGroup;
    }

    public void setSenderUserGroup(String senderUserGroup) {
        this.senderUserGroup = senderUserGroup;
    }

    public String getSenderUserId() {
        return senderUserId;
    }

    public void setSenderUserId(String senderUserId) {
        this.senderUserId = senderUserId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
