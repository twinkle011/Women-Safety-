package com.shesafe;

import com.shesafe.database.DatabaseConnection;
import com.shesafe.model.SOSAlert;
import com.shesafe.model.SOSNotification;
import com.shesafe.model.User;
import com.shesafe.service.SOSAlertService;
import com.shesafe.service.SessionManager;
import com.shesafe.service.ValidationException;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Screen: SOSHistoryScreen
 * Displays previous emergency distress alerts logged by the authenticated user.
 * Phase 4: Loads from MySQL 'sos_alerts' table with individual delivery breakdowns.
 */
public class SOSHistoryScreen extends JFrame {

    private User currentUser;
    private final SOSAlertService alertService = new SOSAlertService();

    private JTable alertsTable;
    private DefaultTableModel tableModel;

    public SOSHistoryScreen() {
        if (!SessionManager.requireLogin(this)) {
            return;
        }
        this.currentUser = SessionManager.getCurrentUser();
        UITheme.configureFrame(this, "Emergency SOS History", 900, 620);
        initUI();
    }

    public SOSHistoryScreen(User user) {
        if (user != null) {
            SessionManager.login(user);
        }
        if (!SessionManager.requireLogin(this)) {
            return;
        }
        this.currentUser = SessionManager.getCurrentUser();
        UITheme.configureFrame(this, "Emergency SOS History", 900, 620);
        initUI();
    }

    private void initUI() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(UITheme.BG_LIGHT);

        // Header Banner
        JPanel header = UITheme.createHeaderBanner(
                "🚨 Emergency SOS Alert History (MySQL)",
                "Distress records for user: " + currentUser.getFullName() + " (User ID #" + currentUser.getUserId() + ")"
        );

        // Content Area
        JPanel contentPanel = new JPanel(new BorderLayout(0, 15));
        contentPanel.setOpaque(false);
        contentPanel.setBorder(new EmptyBorder(20, 25, 20, 25));

        // Setup Table
        String[] columns = {"Alert ID", "Date & Time", "Location", "Contacts Notified", "Delivery Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        alertsTable = new JTable(tableModel);
        alertsTable.setFont(UITheme.FONT_BODY);
        alertsTable.setRowHeight(32);
        alertsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        alertsTable.setGridColor(UITheme.BORDER_COLOR);
        alertsTable.setShowGrid(true);

        JTableHeader tableHeader = alertsTable.getTableHeader();
        tableHeader.setFont(UITheme.FONT_BODY_BOLD);
        tableHeader.setBackground(UITheme.PURPLE_LIGHT);
        tableHeader.setForeground(Color.WHITE);
        tableHeader.setPreferredSize(new Dimension(tableHeader.getWidth(), 35));

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        alertsTable.getColumnModel().getColumn(0).setPreferredWidth(60);
        alertsTable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        alertsTable.getColumnModel().getColumn(1).setPreferredWidth(140);
        alertsTable.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
        alertsTable.getColumnModel().getColumn(2).setPreferredWidth(280);
        alertsTable.getColumnModel().getColumn(3).setPreferredWidth(110);
        alertsTable.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
        alertsTable.getColumnModel().getColumn(4).setPreferredWidth(110);
        alertsTable.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);

        loadAlertHistory();

        JScrollPane scrollPane = new JScrollPane(alertsTable);
        scrollPane.setBorder(new LineBorder(UITheme.BORDER_COLOR, 1, true));
        scrollPane.getViewport().setBackground(Color.WHITE);

        // Action Toolbar
        JPanel actionToolbar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actionToolbar.setOpaque(false);

        JButton btnRefresh = UITheme.createSecondaryButton("🔄 Refresh History");
        JButton btnViewDetails = UITheme.createPrimaryButton("🔍 View Delivery Breakdown");
        JButton btnBack = UITheme.createBackButton();

        btnRefresh.addActionListener(e -> loadAlertHistory());
        btnViewDetails.addActionListener(e -> handleViewDeliveryDetails());
        btnBack.addActionListener(e -> {
            new DashboardScreen().setVisible(true);
            dispose();
        });

        actionToolbar.add(btnRefresh);
        actionToolbar.add(btnViewDetails);
        actionToolbar.add(btnBack);

        contentPanel.add(scrollPane, BorderLayout.CENTER);
        contentPanel.add(actionToolbar, BorderLayout.SOUTH);

        mainPanel.add(header, BorderLayout.NORTH);
        mainPanel.add(contentPanel, BorderLayout.CENTER);

        setContentPane(mainPanel);
    }

    private void loadAlertHistory() {
        tableModel.setRowCount(0);
        try {
            List<SOSAlert> alerts = alertService.getAlertHistoryForCurrentUser();
            if (alerts.isEmpty()) {
                // Table remains empty; user will see clean empty view
                return;
            }

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            for (SOSAlert a : alerts) {
                String timeStr = a.getAlertTime() != null ? sdf.format(a.getAlertTime()) : "N/A";
                String locStr = a.getLocation() != null ? a.getLocation() : "Location Not Shared";

                tableModel.addRow(new Object[]{
                        "#" + a.getAlertId(),
                        timeStr,
                        locStr,
                        a.getContactsNotified(),
                        a.getStatus()
                });
            }
        } catch (ValidationException ve) {
            JOptionPane.showMessageDialog(this, ve.getMessage(), "Session Error", JOptionPane.WARNING_MESSAGE);
        } catch (SQLException se) {
            JOptionPane.showMessageDialog(this,
                    DatabaseConnection.getConnectionErrorMessage(se),
                    "Database Load Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleViewDeliveryDetails() {
        int selectedRow = alertsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select an SOS alert row from the table.",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String alertIdStr = tableModel.getValueAt(selectedRow, 0).toString().replace("#", "");
        int alertId = Integer.parseInt(alertIdStr);

        try {
            List<SOSNotification> notifs = alertService.getNotificationBreakdown(alertId);

            if (notifs.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "No individual recipient logs found for Alert #" + alertId + ".\n"
                        + "(This may occur if the user had 0 registered emergency contacts at the time.)",
                        "Notification Logs",
                        JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            StringBuilder sb = new StringBuilder();
            sb.append("RECIPIENT NOTIFICATION BREAKDOWN FOR ALERT #").append(alertId).append(":\n");
            sb.append("=======================================================================\n\n");

            for (SOSNotification n : notifs) {
                sb.append("• Recipient: ").append(n.getRecipientName()).append("\n")
                  .append("  Target:    ").append(n.getRecipientTarget()).append(" (via ").append(n.getChannel()).append(")\n")
                  .append("  Status:    [").append(n.getDeliveryStatus()).append("]\n")
                  .append("  Details:   ").append(n.getStatusDetails()).append("\n")
                  .append("  Timestamp: ").append(n.getSentAt()).append("\n")
                  .append("-----------------------------------------------------------------------\n");
            }

            JTextArea area = new JTextArea(sb.toString(), 15, 52);
            area.setFont(UITheme.FONT_BODY);
            area.setEditable(false);
            JScrollPane scroll = new JScrollPane(area);

            JOptionPane.showMessageDialog(this, scroll, "Delivery Log (Alert #" + alertId + ")", JOptionPane.PLAIN_MESSAGE);

        } catch (SQLException se) {
            JOptionPane.showMessageDialog(this,
                    DatabaseConnection.getConnectionErrorMessage(se),
                    "Database Query Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SOSHistoryScreen("Priya Sharma").setVisible(true));
    }
}
