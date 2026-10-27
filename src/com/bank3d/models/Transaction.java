package com.bank3d.models;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Transaction {
    private final String type;
    private final double amount;
    private final LocalDateTime timestamp;

    public Transaction(String type, double amount) {
        this.type = type;
        this.amount = amount;
        this.timestamp = LocalDateTime.now();
    }

    public String getType() { return type; }
    public double getAmount() { return amount; }
    public LocalDateTime getTimestamp() { return timestamp; }

    public String getFormattedDate() {
        return timestamp.format(DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm:ss"));
    }

    public String getFormattedAmount() {
        boolean isPositive = type.equalsIgnoreCase("Deposit") || type.equalsIgnoreCase("Account Created");
        return String.format("%sRs. %,.2f", isPositive ? "+" : "-", amount);
    }

    @Override
    public String toString() {
        return "[" + getFormattedDate() + "] " + type + ": Rs. " + String.format("%,.2f", amount);
    }
}
