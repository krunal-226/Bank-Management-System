package com.bank3d.views;

import com.bank3d.components.*;
import com.bank3d.models.Account;
import com.bank3d.models.BankSystem;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

public class LoginView extends JPanel {
    private final BankSystem bank;
    private final Consumer<Account> onLoginSuccess;

    private Animated3DVault vault;
    private ModernTextField userText;
    private JPasswordField pinText;
    private ModernButton loginButton;
    private ModernButton demoButton;
    private JLabel statusLabel;

    public LoginView(BankSystem bank, Consumer<Account> onLoginSuccess) {
        this.bank = bank;
        this.onLoginSuccess = onLoginSuccess;
        setLayout(new BorderLayout());
        setOpaque(false);

        initUI();
    }

    private void initUI() {
        JPanel centerContainer = new JPanel(new GridBagLayout());
        centerContainer.setOpaque(false);

        // 3D Vault on the Left
        vault = new Animated3DVault();
        vault.setPreferredSize(new Dimension(380, 380));

        // Glassmorphic Login Card on the Right
        GlassPanel loginCard = new GlassPanel(24);
        loginCard.setPreferredSize(new Dimension(420, 480));
        loginCard.setLayout(new BoxLayout(loginCard, BoxLayout.Y_AXIS));
        loginCard.setBorder(BorderFactory.createEmptyBorder(32, 36, 32, 36));

        // Header Title
        JLabel brandLabel = new JLabel("VAULT-X 3D BANK");
        brandLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        brandLabel.setForeground(UITheme.TEXT_PRIMARY);
        brandLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subLabel = new JLabel("Next-Gen Secure ATM & Management System");
        subLabel.setFont(UITheme.FONT_SMALL);
        subLabel.setForeground(UITheme.ACCENT_CYAN);
        subLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Input Fields
        JLabel userLbl = new JLabel("Account Number:");
        userLbl.setFont(UITheme.FONT_BOLD);
        userLbl.setForeground(UITheme.TEXT_SECONDARY);
        userLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        userText = new ModernTextField("e.g. 101");
        userText.setText("101");
        userText.setMaximumSize(new Dimension(350, 42));

        JLabel pinLbl = new JLabel("4-Digit Security PIN:");
        pinLbl.setFont(UITheme.FONT_BOLD);
        pinLbl.setForeground(UITheme.TEXT_SECONDARY);
        pinLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        pinText = new JPasswordField("1234");
        pinText.setFont(UITheme.FONT_MONO);
        pinText.setOpaque(false);
        pinText.setForeground(UITheme.TEXT_PRIMARY);
        pinText.setCaretColor(UITheme.ACCENT_CYAN);
        pinText.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(255, 255, 255, 30), 1, true),
            BorderFactory.createEmptyBorder(8, 14, 8, 14)
        ));
        pinText.setMaximumSize(new Dimension(350, 42));

        // Action Buttons
        loginButton = new ModernButton("Unlock Vault & Access Account", ModernButton.Style.PRIMARY);
        loginButton.setMaximumSize(new Dimension(350, 46));
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        demoButton = new ModernButton("⚡ 1-Click Demo Login (Krunal #101)", ModernButton.Style.GOLD);
        demoButton.setMaximumSize(new Dimension(350, 38));
        demoButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        statusLabel = new JLabel(" ");
        statusLabel.setFont(UITheme.FONT_SMALL);
        statusLabel.setForeground(UITheme.ACCENT_RED);
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Button Listeners
        loginButton.addActionListener(e -> attemptLogin());
        demoButton.addActionListener(e -> {
            userText.setText("101");
            pinText.setText("1234");
            attemptLogin();
        });

        // Assemble Login Card
        loginCard.add(brandLabel);
        loginCard.add(Box.createVerticalStrut(4));
        loginCard.add(subLabel);
        loginCard.add(Box.createVerticalStrut(28));
        loginCard.add(userLbl);
        loginCard.add(Box.createVerticalStrut(6));
        loginCard.add(userText);
        loginCard.add(Box.createVerticalStrut(14));
        loginCard.add(pinLbl);
        loginCard.add(Box.createVerticalStrut(6));
        loginCard.add(pinText);
        loginCard.add(Box.createVerticalStrut(8));
        loginCard.add(statusLabel);
        loginCard.add(Box.createVerticalStrut(14));
        loginCard.add(loginButton);
        loginCard.add(Box.createVerticalStrut(10));
        loginCard.add(demoButton);

        // Put Left 3D Vault and Right Card into center container
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 20, 20, 20);
        centerContainer.add(vault, gbc);
        centerContainer.add(loginCard, gbc);

        add(centerContainer, BorderLayout.CENTER);
    }

    private void attemptLogin() {
        String accNum = userText.getText().trim();
        String pin = new String(pinText.getPassword()).trim();

        try {
            Account acc = bank.login(accNum, pin);
            statusLabel.setForeground(UITheme.ACCENT_GREEN);
            statusLabel.setText("PIN Verified. Unlocking 3D Mechanical Vault...");

            loginButton.setEnabled(false);
            demoButton.setEnabled(false);

            // Play 3D Vault Unlock sequence
            vault.triggerUnlockSequence(() -> {
                onLoginSuccess.accept(acc);
                loginButton.setEnabled(true);
                demoButton.setEnabled(true);
                statusLabel.setText(" ");
            });

        } catch (Exception ex) {
            statusLabel.setForeground(UITheme.ACCENT_RED);
            statusLabel.setText(ex.getMessage());
        }
    }
}
