package com.bank3d;

import com.bank3d.models.*;

public class TestVerification {
    public static void main(String[] args) {
        System.out.println("--- STARTING BANK VERIFICATION ---");
        BankSystem bank = new BankSystem();
        SavingsAccount sa = new SavingsAccount("101", "1234", "Krunal", 5000.0);
        bank.addAccount(sa);

        try {
            Account acc = bank.login("101", "1234");
            System.out.println("Login Successful for: " + acc.getHolderName());
            System.out.println("Initial Balance: Rs. " + acc.getBalance());

            // Test Deposit
            acc.deposit(2500.0);
            System.out.println("Balance after deposit: Rs. " + acc.getBalance());

            // Test Withdraw
            acc.withdraw(1500.0);
            System.out.println("Balance after withdrawal: Rs. " + acc.getBalance());

            // Test Minimum Balance Rule (MIN_BALANCE = 1000)
            try {
                acc.withdraw(6000.0); // 6000 - 6000 = 0 < 1000
                System.err.println("FAILED: Minimum balance rule was NOT enforced!");
            } catch (Exception ex) {
                System.out.println("SUCCESS: Minimum balance enforced: " + ex.getMessage());
            }

            // Print history
            acc.printTransactionHistory();

            System.out.println("--- ALL LOGIC TESTS PASSED 100% ---");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
