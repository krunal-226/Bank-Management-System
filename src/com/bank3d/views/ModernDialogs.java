package com.bank3d.views;

import com.bank3d.components.*;
import com.bank3d.models.Account;
import com.bank3d.models.Transaction;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ModernDialogs {

    public static void showDepositDialog(JFrame parent, Account account, Runnable onDepositSuccess) {
        JDialog dialog = createBaseDialog(parent, "Deposit Cash to 3D Vault", 460, 360);

        GlassPanel panel = new GlassPanel(20);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));

        JLabel title = new JLabel("Deposit Cash Terminal");
        title.setFont(UITheme.FONT_TITLE);
        title.setForeground(UITheme.TEXT_PRIMARY);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("Current Balance: Rs. " + String.format("%,.2f", account.getBalance()));
        subtitle.setFont(UITheme.FONT_REGULAR);
        subtitle.setForeground(UITheme.ACCENT_CYAN);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        ModernTextField amountField = new ModernTextField("Enter deposit amount (Rs.)");
        amountField.setMaximumSize(new Dimension(380, 42));

        // Quick amount preset buttons
        JPanel presets = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 4));
        presets.setOpaque(false);
        int[] presetAmounts = { 500, 1000, 2000, 5000 };
        for (int pVal : presetAmounts) {
            ModernButton pBtn = new ModernButton("+" + pVal, ModernButton.Style.SECONDARY);
            pBtn.setPreferredSize(new Dimension(80, 32));
            pBtn.addActionListener(e -> amountField.setText(String.valueOf(pVal)));
            presets.add(pBtn);
        }

        ModernButton confirmBtn = new ModernButton("Confirm Deposit & Feed Vault", ModernButton.Style.SUCCESS);
        confirmBtn.setMaximumSize(new Dimension(380, 44));
        confirmBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        confirmBtn.addActionListener(e -> {
            try {
                String txt = amountField.getText().trim();
                if (txt.isEmpty()) {
                    showError(dialog, "Please enter a valid amount.");
                    return;
                }
                double amt = Double.parseDouble(txt);
                account.deposit(amt);
                dialog.dispose();
                if (onDepositSuccess != null) onDepositSuccess.run();
                showSuccess(parent, "Deposit Successful", "Rs. " + String.format("%,.2f", amt) + " deposited! 3D Vault updated.");
            } catch (NumberFormatException ex) {
                showError(dialog, "Please enter numeric digits only.");
            } catch (IllegalArgumentException ex) {
                showError(dialog, ex.getMessage());
            }
        });

        panel.add(title);
        panel.add(Box.createVerticalStrut(6));
        panel.add(subtitle);
        panel.add(Box.createVerticalStrut(20));
        panel.add(amountField);
        panel.add(Box.createVerticalStrut(12));
        panel.add(presets);
        panel.add(Box.createVerticalStrut(20));
        panel.add(confirmBtn);

        dialog.setContentPane(panel);
        dialog.setVisible(true);
    }

    public static void showWithdrawDialog(JFrame parent, Account account, Runnable onWithdrawSuccess) {
        JDialog dialog = createBaseDialog(parent, "Withdraw Cash from 3D Vault", 460, 370);

        GlassPanel panel = new GlassPanel(20);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));

        JLabel title = new JLabel("Cash Withdrawal Terminal");
        title.setFont(UITheme.FONT_TITLE);
        title.setForeground(UITheme.TEXT_PRIMARY);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("Available Funds: Rs. " + String.format("%,.2f", account.getBalance()));
        subtitle.setFont(UITheme.FONT_REGULAR);
        subtitle.setForeground(UITheme.ACCENT_GOLD);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        ModernTextField amountField = new ModernTextField("Enter withdrawal amount (Rs.)");
        amountField.setMaximumSize(new Dimension(380, 42));

        // Quick amount preset buttons
        JPanel presets = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 4));
        presets.setOpaque(false);
        int[] presetAmounts = { 500, 1000, 2000, 5000 };
        for (int pVal : presetAmounts) {
            ModernButton pBtn = new ModernButton("Rs." + pVal, ModernButton.Style.SECONDARY);
            pBtn.setPreferredSize(new Dimension(80, 32));
            pBtn.addActionListener(e -> amountField.setText(String.valueOf(pVal)));
            presets.add(pBtn);
        }

        ModernButton confirmBtn = new ModernButton("Dispense Cash from Vault", ModernButton.Style.GOLD);
        confirmBtn.setMaximumSize(new Dimension(380, 44));
        confirmBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        confirmBtn.addActionListener(e -> {
            try {
                String txt = amountField.getText().trim();
                if (txt.isEmpty()) {
                    showError(dialog, "Please enter a valid amount.");
                    return;
                }
                double amt = Double.parseDouble(txt);
                account.withdraw(amt);
                dialog.dispose();
                if (onWithdrawSuccess != null) onWithdrawSuccess.run();
                showSuccess(parent, "Cash Dispensed", "Please collect Rs. " + String.format("%,.2f", amt) + " from the terminal dispenser.");
            } catch (NumberFormatException ex) {
                showError(dialog, "Please enter numeric digits only.");
            } catch (Exception ex) {
                showError(dialog, ex.getMessage());
            }
        });

        panel.add(title);
        panel.add(Box.createVerticalStrut(6));
        panel.add(subtitle);
        panel.add(Box.createVerticalStrut(20));
        panel.add(amountField);
        panel.add(Box.createVerticalStrut(12));
        panel.add(presets);
        panel.add(Box.createVerticalStrut(20));
        panel.add(confirmBtn);

        dialog.setContentPane(panel);
        dialog.setVisible(true);
    }

    public static void showStatementDialog(JFrame parent, Account account) {
        JDialog dialog = createBaseDialog(parent, "Account Statement Ledger", 680, 460);

        GlassPanel panel = new GlassPanel(20);
        panel.setLayout(new BorderLayout(15, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        // Header
        JPanel top = new JPanel(new GridLayout(2, 1, 4, 4));
        top.setOpaque(false);
        JLabel title = new JLabel("Official Account Statement // " + account.getAccountNumber());
        title.setFont(UITheme.FONT_TITLE);
        title.setForeground(UITheme.TEXT_PRIMARY);
        JLabel sub = new JLabel("Account Holder: " + account.getHolderName() + " | Current Balance: Rs. " + String.format("%,.2f", account.getBalance()));
        sub.setFont(UITheme.FONT_REGULAR);
        sub.setForeground(UITheme.ACCENT_CYAN);
        top.add(title);
        top.add(sub);
        panel.add(top, BorderLayout.NORTH);

        // Table
        String[] cols = { "Type", "Amount", "Date & Time" };
        List<Transaction> list = account.getTransactionHistory();
        Object[][] rowData = new Object[list.size()][3];
        for (int i = 0; i < list.size(); i++) {
            Transaction t = list.get(i);
            rowData[i][0] = t.getType();
            rowData[i][1] = t.getFormattedAmount();
            rowData[i][2] = t.getFormattedDate();
        }

        DefaultTableModel model = new DefaultTableModel(rowData, cols) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        JTable table = new JTable(model);
        table.setOpaque(false);
        table.setBackground(new Color(15, 23, 42));
        table.setForeground(UITheme.TEXT_PRIMARY);
        table.setFont(UITheme.FONT_REGULAR);
        table.setRowHeight(32);
        table.setGridColor(new Color(255, 255, 255, 15));
        table.getTableHeader().setBackground(new Color(30, 41, 59));
        table.getTableHeader().setForeground(UITheme.TEXT_SECONDARY);
        table.getTableHeader().setFont(UITheme.FONT_BOLD);

        // Custom cell renderer for styled badges
        table.getColumnModel().getColumn(1).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val, boolean isSel, boolean hasFoc, int r, int c) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(t, val, isSel, hasFoc, r, c);
                l.setOpaque(false);
                String str = val != null ? val.toString() : "";
                if (str.startsWith("+")) {
                    l.setForeground(UITheme.ACCENT_GREEN);
                } else if (str.startsWith("-")) {
                    l.setForeground(UITheme.ACCENT_RED);
                } else {
                    l.setForeground(UITheme.TEXT_PRIMARY);
                }
                return l;
            }
        });

        JScrollPane scroll = new JScrollPane(table);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(255, 255, 255, 20)));
        panel.add(scroll, BorderLayout.CENTER);

        // Bottom actions
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        bottom.setOpaque(false);

        ModernButton printBtn = new ModernButton("Print to IDE Console", ModernButton.Style.SECONDARY);
        printBtn.setPreferredSize(new Dimension(180, 38));
        printBtn.addActionListener(e -> {
            account.printTransactionHistory();
            JOptionPane.showMessageDialog(dialog, "Statement printed to your IDE console as requested!", "Printed", JOptionPane.INFORMATION_MESSAGE);
        });

        ModernButton closeBtn = new ModernButton("Close Statement", ModernButton.Style.PRIMARY);
        closeBtn.setPreferredSize(new Dimension(150, 38));
        closeBtn.addActionListener(e -> dialog.dispose());

        bottom.add(printBtn);
        bottom.add(closeBtn);
        panel.add(bottom, BorderLayout.SOUTH);

        dialog.setContentPane(panel);
        dialog.setVisible(true);
    }

    public static void showSuccess(Component parent, String title, String msg) {
        JOptionPane.showMessageDialog(parent, msg, title, JOptionPane.INFORMATION_MESSAGE);
    }

    public static void showError(Component parent, String msg) {
        JOptionPane.showMessageDialog(parent, msg, "Transaction Error", JOptionPane.ERROR_MESSAGE);
    }

    private static JDialog createBaseDialog(JFrame parent, String title, int w, int h) {
        JDialog dialog = new JDialog(parent, title, true);
        dialog.setSize(w, h);
        dialog.setLocationRelativeTo(parent);
        dialog.setResizable(false);
        return dialog;
    }
}
