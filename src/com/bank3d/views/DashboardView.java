package com.bank3d.views;

import com.bank3d.components.*;
import com.bank3d.models.Account;
import com.bank3d.models.Transaction;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class DashboardView extends JLayeredPane {
    private final JFrame parentFrame;
    private Account account;
    private final Runnable onLogout;

    private JPanel contentPanel;
    private Interactive3DCard card3D;
    private CoinParticleSystem coinParticles;
    private IsometricChart3D chart3D;
    private JLabel balanceLabel;
    private JTable ledgerTable;
    private DefaultTableModel tableModel;
    private JLabel clockLabel;

    public DashboardView(JFrame parentFrame, Account account, Runnable onLogout) {
        this.parentFrame = parentFrame;
        this.account = account;
        this.onLogout = onLogout;

        setOpaque(false);
        initUI();
    }

    public void updateAccount(Account acc) {
        this.account = acc;
        if (card3D != null) card3D.setAccount(acc);
        refreshBalanceDisplay();
        refreshLedgerTable();
    }

    @Override
    public void doLayout() {
        super.doLayout();
        int w = getWidth();
        int h = getHeight();
        if (contentPanel != null) contentPanel.setBounds(0, 0, w, h);
        if (coinParticles != null) coinParticles.setBounds(0, 0, w, h);
    }

    private void initUI() {
        contentPanel = new JPanel(new BorderLayout(15, 15));
        contentPanel.setOpaque(false);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(18, 24, 18, 24));

        contentPanel.add(createHeaderBar(), BorderLayout.NORTH);

        JPanel centerGrid = new JPanel(new GridLayout(1, 2, 20, 0));
        centerGrid.setOpaque(false);
        centerGrid.add(createCardSection());
        centerGrid.add(createActionsSection());
        contentPanel.add(centerGrid, BorderLayout.CENTER);

        JPanel bottomGrid = new JPanel(new GridLayout(1, 2, 20, 0));
        bottomGrid.setOpaque(false);
        bottomGrid.setPreferredSize(new Dimension(1100, 240));
        bottomGrid.add(createLedgerSection());
        bottomGrid.add(createChartSection());
        contentPanel.add(bottomGrid, BorderLayout.SOUTH);

        coinParticles = new CoinParticleSystem();
        coinParticles.setOpaque(false);

        add(contentPanel, Integer.valueOf(100));
        add(coinParticles, Integer.valueOf(200));

        addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                doLayout();
            }
        });
    }

    private JPanel createHeaderBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setOpaque(false);

        JPanel brandPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        brandPanel.setOpaque(false);

        JLabel logo = new JLabel("🛡️ VAULT-X 3D BANK");
        logo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        logo.setForeground(UITheme.TEXT_PRIMARY);

        JLabel userGreeting = new JLabel(" | Welcome, " + (account != null ? account.getHolderName() : "User"));
        userGreeting.setFont(UITheme.FONT_SUBTITLE);
        userGreeting.setForeground(UITheme.ACCENT_CYAN);

        brandPanel.add(logo);
        brandPanel.add(userGreeting);

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        rightPanel.setOpaque(false);

        clockLabel = new JLabel();
        clockLabel.setFont(UITheme.FONT_MONO);
        clockLabel.setForeground(UITheme.TEXT_SECONDARY);
        Timer clockTimer = new Timer(1000, e -> {
            clockLabel.setText(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) + " IST");
        });
        clockTimer.start();
        clockLabel.setText(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) + " IST");

        ModernButton logoutBtn = new ModernButton("🚪 Logout", ModernButton.Style.DANGER);
        logoutBtn.setPreferredSize(new Dimension(110, 36));
        logoutBtn.addActionListener(e -> {
            if (onLogout != null) onLogout.run();
        });

        rightPanel.add(clockLabel);
        rightPanel.add(logoutBtn);

        bar.add(brandPanel, BorderLayout.WEST);
        bar.add(rightPanel, BorderLayout.EAST);
        return bar;
    }

    private JPanel createCardSection() {
        GlassPanel panel = new GlassPanel(20);
        panel.setLayout(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));

        card3D = new Interactive3DCard(account);
        panel.add(card3D, BorderLayout.CENTER);

        JPanel ctrlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 14, 0));
        ctrlPanel.setOpaque(false);

        ModernButton flipBtn = new ModernButton("🔄 3D Flip Card (Front / Back)", ModernButton.Style.SECONDARY);
        flipBtn.setPreferredSize(new Dimension(230, 36));
        flipBtn.addActionListener(e -> card3D.toggleFlip());

        JLabel tip = new JLabel("Move mouse over card to tilt in 3D");
        tip.setFont(UITheme.FONT_SMALL);
        tip.setForeground(UITheme.TEXT_MUTED);

        ctrlPanel.add(flipBtn);
        ctrlPanel.add(tip);

        panel.add(ctrlPanel, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createActionsSection() {
        GlassPanel panel = new GlassPanel(20);
        panel.setLayout(new BorderLayout(15, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        JPanel balBox = new JPanel(new GridLayout(3, 1, 2, 2));
        balBox.setOpaque(false);

        JLabel balTitle = new JLabel("AVAILABLE TOTAL BALANCE");
        balTitle.setFont(UITheme.FONT_SMALL);
        balTitle.setForeground(UITheme.TEXT_SECONDARY);

        balanceLabel = new JLabel();
        balanceLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        balanceLabel.setForeground(UITheme.TEXT_PRIMARY);
        refreshBalanceDisplay();

        JLabel tierLbl = new JLabel("Account #101 • Savings Account Tier 1 (4.5% p.a.)");
        tierLbl.setFont(UITheme.FONT_SMALL);
        tierLbl.setForeground(UITheme.ACCENT_GREEN);

        balBox.add(balTitle);
        balBox.add(balanceLabel);
        balBox.add(tierLbl);
        panel.add(balBox, BorderLayout.NORTH);

        JPanel btnGrid = new JPanel(new GridLayout(2, 2, 14, 14));
        btnGrid.setOpaque(false);

        ModernButton depositBtn = new ModernButton("💰 Deposit Cash", ModernButton.Style.SUCCESS);
        depositBtn.addActionListener(e -> {
            ModernDialogs.showDepositDialog(parentFrame, account, () -> {
                coinParticles.burstDeposit(35);
                if (card3D != null) card3D.regenerateCardCaches();
                refreshBalanceDisplay();
                refreshLedgerTable();
            });
        });

        ModernButton withdrawBtn = new ModernButton("🏧 Withdraw Cash", ModernButton.Style.GOLD);
        withdrawBtn.addActionListener(e -> {
            ModernDialogs.showWithdrawDialog(parentFrame, account, () -> {
                coinParticles.burstWithdraw(35);
                if (card3D != null) card3D.regenerateCardCaches();
                refreshBalanceDisplay();
                refreshLedgerTable();
            });
        });

        ModernButton balanceBtn = new ModernButton("🔍 Check Balance", ModernButton.Style.SECONDARY);
        balanceBtn.addActionListener(e -> {
            JOptionPane.showMessageDialog(parentFrame,
                "Current Account Balance: Rs. " + String.format("%,.2f", account.getBalance()) +
                "\nAccount Holder: " + account.getHolderName() +
                "\nAccount Number: " + account.getAccountNumber(),
                "Real-time Balance Check", JOptionPane.INFORMATION_MESSAGE);
        });

        ModernButton statementBtn = new ModernButton("📜 View Statement", ModernButton.Style.PRIMARY);
        statementBtn.addActionListener(e -> {
            ModernDialogs.showStatementDialog(parentFrame, account);
        });

        btnGrid.add(depositBtn);
        btnGrid.add(withdrawBtn);
        btnGrid.add(balanceBtn);
        btnGrid.add(statementBtn);

        panel.add(btnGrid, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createLedgerSection() {
        GlassPanel panel = new GlassPanel(18);
        panel.setLayout(new BorderLayout(8, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));

        JLabel title = new JLabel("Real-time Transaction Feed");
        title.setFont(UITheme.FONT_BOLD);
        title.setForeground(UITheme.TEXT_PRIMARY);
        panel.add(title, BorderLayout.NORTH);

        String[] cols = { "Type", "Amount", "Time" };
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };

        ledgerTable = new JTable(tableModel);
        ledgerTable.setOpaque(false);
        ledgerTable.setBackground(new Color(15, 23, 42));
        ledgerTable.setForeground(UITheme.TEXT_PRIMARY);
        ledgerTable.setRowHeight(28);
        ledgerTable.setGridColor(new Color(255, 255, 255, 12));
        ledgerTable.getTableHeader().setBackground(new Color(25, 35, 55));
        ledgerTable.getTableHeader().setForeground(UITheme.TEXT_SECONDARY);

        ledgerTable.getColumnModel().getColumn(1).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val, boolean isSel, boolean hasFoc, int r, int c) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(t, val, isSel, hasFoc, r, c);
                l.setOpaque(false);
                if (val != null && val.toString().startsWith("+")) {
                    l.setForeground(UITheme.ACCENT_GREEN);
                } else {
                    l.setForeground(UITheme.ACCENT_RED);
                }
                return l;
            }
        });

        JScrollPane scroll = new JScrollPane(ledgerTable);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        panel.add(scroll, BorderLayout.CENTER);

        refreshLedgerTable();
        return panel;
    }

    private JPanel createChartSection() {
        GlassPanel panel = new GlassPanel(18);
        panel.setLayout(new BorderLayout(8, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));

        chart3D = new IsometricChart3D();
        panel.add(chart3D, BorderLayout.CENTER);
        return panel;
    }

    public void refreshBalanceDisplay() {
        if (balanceLabel != null && account != null) {
            balanceLabel.setText("Rs. " + String.format("%,.2f", account.getBalance()));
        }
    }

    public void refreshLedgerTable() {
        if (tableModel == null || account == null) return;
        tableModel.setRowCount(0);
        List<Transaction> list = account.getTransactionHistory();
        double depSum = 0;
        double witSum = 0;

        for (Transaction t : list) {
            tableModel.addRow(new Object[]{
                t.getType(),
                t.getFormattedAmount(),
                t.getFormattedDate()
            });

            if (t.getType().equalsIgnoreCase("Deposit") || t.getType().equalsIgnoreCase("Account Created")) {
                depSum += t.getAmount();
            } else {
                witSum += t.getAmount();
            }
        }

        if (chart3D != null) {
            chart3D.updateData(depSum, witSum);
        }
    }
}
