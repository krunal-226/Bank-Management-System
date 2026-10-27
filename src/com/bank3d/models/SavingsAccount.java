package com.bank3d.models;

public class SavingsAccount extends Account {
    private static final double MIN_BALANCE = 1000.0;

    public SavingsAccount(String accountNumber, String pin, String holderName, double initialBalance) {
        super(accountNumber, pin, holderName, initialBalance);
    }

    public static double getMinBalance() {
        return MIN_BALANCE;
    }

    // Overriding withdraw method to enforce minimum balance rule (Polymorphism)
    @Override
    public void withdraw(double amount) throws Exception {
        if ((balance - amount) < MIN_BALANCE) {
            throw new Exception("Savings Account rule violated! You must maintain a minimum balance of Rs. " + MIN_BALANCE);
        }
        super.withdraw(amount);
    }
}
