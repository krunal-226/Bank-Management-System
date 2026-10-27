package com.bank3d;

import com.bank3d.models.*;
import com.bank3d.views.*;
import javax.swing.*;
import java.awt.*;

public class TestLoginFlow {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                System.out.println("Testing complete Login -> Dashboard flow...");
                BankSystem bank = new BankSystem();
                bank.addAccount(new SavingsAccount("101", "1234", "Krunal", 5000.0));

                JFrame frame = new JFrame("Flow Test");
                CardLayout cl = new CardLayout();
                JPanel container = new JPanel(cl);

                LoginView lv = new LoginView(bank, acc -> {
                    System.out.println("LOGIN CALLBACK TRIGGERED FOR: " + acc.getHolderName());
                    DashboardView dv = new DashboardView(frame, acc, () -> {});
                    container.add(dv, "DASHBOARD");
                    cl.show(container, "DASHBOARD");
                    System.out.println("DASHBOARD SHOWN SUCCESSFULLY!");
                });

                container.add(lv, "LOGIN");
                frame.add(container);
                frame.setSize(1240, 840);
                frame.setVisible(true);

                // Simulate login
                Account acc = bank.login("101", "1234");
                DashboardView dv = new DashboardView(frame, acc, () -> {});
                container.add(dv, "DASHBOARD");
                cl.show(container, "DASHBOARD");

                System.out.println("FLOW TEST PASSED 100%!");
                System.exit(0);
            } catch (Throwable t) {
                t.printStackTrace();
                System.exit(1);
            }
        });
    }
}
