package com.bank3d.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Account {
    private String accountNumber;
    private String pin;
    private String holderName;
    protected double balance; // Protected so child classes can access it
    private List<Transaction> transactionHistory;

    public Account(String accountNumber, String pin, String holderName, double initialBalance) {
        this.accountNumber = accountNumber;
        this.pin = pin;
        this.holderName = holderName;
        this.balance = initialBalance;
        this.transactionHistory = new ArrayList<>();
        transactionHistory.add(new Transaction("Account Created", initialBalance));
    }

    public String getAccountNumber() { return accountNumber; }
    public boolean validatePin(String inputPin) { return this.pin.equals(inputPin); }
    public String getHolderName() { return holderName; }
    public double getBalance() { return balance; }
    public List<Transaction> getTransactionHistory() { return Collections.unmodifiableList(transactionHistory); }

    public void deposit(double amount) {
        if (amount > 0) {
            balance += amount;
            transactionHistory.add(new Transaction("Deposit", amount));
            System.out.println("Deposit successful! New Balance: Rs. " + balance);
        } else {
            System.out.println("Invalid deposit amount.");
        }
    }

    public void withdraw(double amount) throws Exception {
        if (amount <= 0) {
            throw new Exception("Withdrawal amount must be greater than zero.");
        }
        if (amount > balance) {
            throw new Exception("Insufficient balance! Current balance: Rs. " + balance);
        }
        balance -= amount;
        transactionHistory.add(new Transaction("Withdrawal", amount));
        System.out.println("Please collect your cash. Remaining Balance: Rs. " + balance);
    }

    public void printTransactionHistory() {
        System.out.println("\n--- Transaction History for " + holderName + " ---");
        for (Transaction t : transactionHistory) {
            System.out.println(t);
        }
    }
}
