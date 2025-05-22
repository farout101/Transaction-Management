package com.test.dps.dto;

public class Transaction {
    private int id;
    private String userId;
    private String ioGroup;
    private String targetIoGroup;
    private double amount;

    public Transaction() {}

    public Transaction(int id, String userId, String ioGroup, String targetIoGroup, double amount) {
        this.id = id;
        this.userId = userId;
        this.ioGroup = ioGroup;
        this.targetIoGroup = targetIoGroup;
        this.amount = amount;
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getIoGroup() {
        return ioGroup;
    }

    public void setIoGroup(String ioGroup) {
        this.ioGroup = ioGroup;
    }

    public String getTargetIoGroup() {
        return targetIoGroup;
    }

    public void setTargetIoGroup(String targetIoGroup) {
        this.targetIoGroup = targetIoGroup;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "id=" + id +
                ", userId='" + userId + '\'' +
                ", ioGroup='" + ioGroup + '\'' +
                ", targetIoGroup='" + targetIoGroup + '\'' +
                ", amount=" + amount +
                '}';
    }
}
