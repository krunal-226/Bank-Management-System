package com.bank3d;

import com.bank3d.components.UITheme;
import com.bank3d.models.Account;
import com.bank3d.models.BankSystem;
import com.bank3d.models.SavingsAccount;
import com.bank3d.views.DashboardView;
import com.bank3d.views.LoginView;

import javax.swing.*;
import java.awt.*;

public class Main {
    private static BankSystem bank = new BankSystem();
    private static Account currentAccount = null;

    private static JFrame frame;
    private static CardLayout cardLayout;
    private static JPanel mainContainer;
    private static LoginView loginView;
    private static DashboardView dashboardView;

    public static void main(String[] args) {
        // High DPI & Anti-Aliasing properties
        System.setProperty("sun.java2d.uiScale.enabled", "true");
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");

        SwingUtilities.invokeLater(() -> {
            // 1. Pre-create sample account matching user's original code
            bank.addAccount(new SavingsAccount("101", "1234", "Krunal", 5000.0));

            // 2. Setup Main Application Frame
            frame = new JFrame("Vault-X 3D Bank Management System");
            frame.setSize(1240, 840);
            frame.setMinimumSize(new Dimension(1024, 720));
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLocationRelativeTo(null);
            frame.getContentPane().setBackground(UITheme.BG_DARK);

            cardLayout = new CardLayout();
            mainContainer = new JPanel(cardLayout);
            mainContainer.setOpaque(false);

            // 3. Setup Views
            loginView = new LoginView(bank, loggedInAccount -> {
                currentAccount = loggedInAccount;
                if (dashboardView == null) {
                    dashboardView = new DashboardView(frame, currentAccount, () -> logout());
                    mainContainer.add(dashboardView, "DASHBOARD");
                } else {
                    dashboardView.updateAccount(currentAccount);
                }
                cardLayout.show(mainContainer, "DASHBOARD");
            });

            mainContainer.add(loginView, "LOGIN");

            frame.add(mainContainer);
            frame.setVisible(true);
        });
    }

    private static void logout() {
        currentAccount = null;
        cardLayout.show(mainContainer, "LOGIN");
    }
}
