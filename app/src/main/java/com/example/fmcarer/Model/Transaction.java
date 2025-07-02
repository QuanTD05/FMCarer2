package com.example.fmcarer.Model;

public class Transaction {
    public long timestamp;
    public int amount;
    public int durationDays;

    public Transaction() {}  // Firebase yêu cầu constructor trống

    public Transaction(long timestamp, int amount, int durationDays) {
        this.timestamp = timestamp;
        this.amount = amount;
        this.durationDays = durationDays;
    }
}
