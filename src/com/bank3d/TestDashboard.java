package com.bank3d;

import com.bank3d.models.*;
import com.bank3d.views.*;
import javax.swing.*;

public class TestDashboard {
    public static void main(String[] args) {
        try {
            System.out.println("Testing DashboardView creation...");
            BankSystem bank = new BankSystem();
            SavingsAccount sa = new SavingsAccount("101", "1234", "Krunal", 5000.0);
            bank.addAccount(sa);

            JFrame f = new JFrame("Test");
            f.setSize(1240, 840);
            DashboardView dv = new DashboardView(f, sa, () -> {});
            f.add(dv);
            System.out.println("DashboardView created successfully with zero errors!");
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
}
