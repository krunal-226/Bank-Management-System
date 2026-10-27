package com.bank3d.models;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TransactionRecord {
    public enum Type { DEPOSIT, WITHDRAWAL, INITIAL }

    private final String id;
    private final Type type;
    private final double amount;
    private final double balanceAfter;
    private final LocalDateTime timestamp;
    private final String description;

    public TransactionRecord(String id, Type type, double amount, double balanceAfter, String description) {
        this.id = id;
        this.type = type;
        this.amount = amount;
        this.balanceAfter = balanceAfter;
        this.timestamp = LocalDateTime.now();
        this.description = description;
    }

    public String getId() { return id; }
    public Type getType() { return type; }
    public double getAmount() { return amount; }
    public double getBalanceAfter() { return balanceAfter; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public String getDescription() { return description; }

    public String getFormattedDate() {
        return timestamp.format(DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm:ss"));
    }

    public String getFormattedAmount() {
        String sign = (type == Type.DEPOSIT || type == Type.INITIAL) ? "+" : "-";
        return String.format("%sRs. %,.2f", sign, amount);
    }
}
