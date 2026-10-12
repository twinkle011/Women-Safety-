package com.shesafe;

import com.shesafe.database.DatabaseConnection;
import com.shesafe.model.User;
import com.shesafe.service.LocationService;
import com.shesafe.service.NotificationService;
import com.shesafe.service.SOSAlertService;
import com.shesafe.service.SessionManager;
import com.shesafe.service.ValidationException;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.sql.SQLException;

/**
 * Screen 4: DashboardScreen
 * The main operational hub of SheSafe.
 * Phase 4: Integrates SOS distress button with confirmation, optional location sharing,
 * notification dispatch, duplicate alert protection, and SOS history viewing.
 */
public class DashboardScreen extends JFrame {

    private User currentUser;
    private final SOSAlertService sosService = new SOSAlertService();
    private final LocationService locationService = new LocationService();

    private JButton btnSos;

    public DashboardScreen() {
        if (!SessionManager.requireLogin(this)) {
            return;
        }
        this.currentUser = SessionManager.getCurrentUser();
        UITheme.configureFrame(this, "Safety Dashboard", 900, 670);
        initUI();
    }

    public DashboardScreen(User user) {
        if (user != null) {
            SessionManager.login(user);
        }
        if (!SessionManager.requireLogin(this)) {
            return;
        }
        this.currentUser = SessionManager.getCurrentUser();
        UITheme.configureFrame(this, "Safety Dashboard", 900, 670);
        initUI();
    }

    public DashboardScreen(String userName) {
        this(new User(1, userName, userName.toLowerCase().replace(" ", "") + "@shesafe.com", "9876543210", null, null));
    }

    private void initUI() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(UITheme.BG_LIGHT);

        // TOP HEADER BAR
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(UITheme.PURPLE_PRIMARY);
        headerPanel.setBorder(new EmptyBorder(16, 25, 16, 25));

        JPanel leftHeader = new JPanel();
        leftHeader.setLayout(new BoxLayout(leftHeader, BoxLayout.Y_AXIS));
        leftHeader.setOpaque(false);

        JLabel lblTitle = new JLabel("🛡 SheSafe Personal Security Dashboard");
        lblTitle.setFont(UITheme.FONT_HEADER);
        lblTitle.setForeground(Color.WHITE);

        String userDisplay = currentUser.getFullName() + " (User ID #" + currentUser.getUserId() + ")";
        JLabel lblWelcome = new JLabel("Welcome back, " + userDisplay + "  •  Active Protection: ENABLED");
        lblWelcome.setFont(UITheme.FONT_BODY);
        lblWelcome.setForeground(new Color(248, 187, 208));
        lblWelcome.setBorder(new EmptyBorder(4, 0, 0, 0));

        leftHeader.add(lblTitle);
        leftHeader.add(lblWelcome);

        JButton btnLogout = UITheme.createSecondaryButton("🚪 Logout");
        btnLogout.addActionListener(e -> handleLogout());

        headerPanel.add(leftHeader, BorderLayout.WEST);
        headerPanel.add(btnLogout, BorderLayout.EAST);

        // CENTER MAIN CONTENT
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);
        contentPanel.setBorder(new EmptyBorder(18, 25, 18, 25));

        // 1. PROMINENT RED SOS EMERGENCY TRIGGER CARD
        JPanel sosCard = UITheme.createCardPanel();
        sosCard.setLayout(new BorderLayout());
        sosCard.setMaximumSize(new Dimension(850, 150));
        sosCard.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(255, 205, 210), 2, true),
                new EmptyBorder(14, 22, 14, 22)
        ));

        JPanel sosInfoPanel = new JPanel();
        sosInfoPanel.setLayout(new BoxLayout(sosInfoPanel, BoxLayout.Y_AXIS));
        sosInfoPanel.setOpaque(false);

        JLabel lblSosTitle = new JLabel("EMERGENCY DISTRESS TRIGGER (SOS)");
        lblSosTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblSosTitle.setForeground(UITheme.SOS_RED);

        JLabel lblSosDesc = new JLabel("<html>Tap in an emergency to initiate distress notifications to saved contacts<br>with optional location coordinates. Confirmation is requested.</html>");
        lblSosDesc.setFont(UITheme.FONT_BODY);
        lblSosDesc.setForeground(UITheme.TEXT_DARK);
        lblSosDesc.setBorder(new EmptyBorder(6, 0, 0, 0));

        sosInfoPanel.add(lblSosTitle);
        sosInfoPanel.add(lblSosDesc);

        btnSos = UITheme.createSosButton("🚨 PRESS SOS");
        btnSos.setPreferredSize(new Dimension(240, 65));
        btnSos.addActionListener(e -> handleSosInitiation());

        sosCard.add(sosInfoPanel, BorderLayout.CENTER);
        sosCard.add(btnSos, BorderLayout.EAST);

        contentPanel.add(sosCard);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 16)));

        // 2. FEATURE TILES GRID (2x2 Grid for spacious layout)
        JPanel gridPanel = new JPanel(new GridLayout(2, 2, 16, 14));
        gridPanel.setOpaque(false);
        gridPanel.setMaximumSize(new Dimension(850, 290));

        // Tile 1: Emergency Contacts
        gridPanel.add(createActionCard(
                "📞 Emergency Contacts",
                "Manage your trusted guardians in MySQL for emergency distress alerts.",
                "Open Contacts",
                UITheme.PURPLE_PRIMARY,
                e -> {
                    new EmergencyContactsScreen().setVisible(true);
                    dispose();
                }
        ));

        // Tile 2: Safety Tips
        gridPanel.add(createActionCard(
                "💡 Personal Safety Tips",
                "Review verified travel precautions, awareness tactics, and helpline numbers.",
                "View Guidelines",
                new Color(0, 150, 136),
                e -> {
                    new SafetyTipsScreen().setVisible(true);
                    dispose();
                }
        ));

        // Tile 3: Incident Reporting
        gridPanel.add(createActionCard(
                "📝 Incident Reporting",
                "Formally document safety violations and unsafe spots into MySQL database.",
                "Report Incident",
                UITheme.PINK_ACCENT,
                e -> {
                    new IncidentReportingScreen().setVisible(true);
                    dispose();
                }
        ));

        // Tile 4: SOS Alert History [NEW IN PHASE 4]
        gridPanel.add(createActionCard(
                "🚨 SOS Alert History",
                "View past emergency events, delivery results, and contact notification logs.",
                "View History",
                UITheme.SOS_RED,
                e -> {
                    new SOSHistoryScreen().setVisible(true);
                    dispose();
                }
        ));

        contentPanel.add(gridPanel);

        // BOTTOM STATUS BAR
        JPanel bottomBar = new JPanel(new BorderLayout());
        bottomBar.setBackground(Color.WHITE);
        bottomBar.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(UITheme.BORDER_COLOR, 1),
                new EmptyBorder(8, 25, 8, 25)
        ));

        JLabel helplineInfo = new JLabel("⚡ Emergency Helplines: National Police: 112 | Women Helpline: 1091 | Ambulance: 108");
        helplineInfo.setFont(UITheme.FONT_BODY_BOLD);
        helplineInfo.setForeground(UITheme.PURPLE_PRIMARY);

        JLabel statusBadge = new JLabel("● Session Active: " + currentUser.getEmail());
        statusBadge.setFont(UITheme.FONT_SMALL);
        statusBadge.setForeground(UITheme.SUCCESS_GREEN);

        bottomBar.add(helplineInfo, BorderLayout.WEST);
        bottomBar.add(statusBadge, BorderLayout.EAST);

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        mainPanel.add(bottomBar, BorderLayout.SOUTH);

        setContentPane(mainPanel);
    }

    private JPanel createActionCard(String title, String desc, String btnText, Color accentColor, ActionListener action) {
        JPanel card = UITheme.createCardPanel();
        card.setLayout(new BorderLayout());
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(UITheme.BORDER_COLOR, 1, true),
                new EmptyBorder(12, 16, 12, 16)
        ));

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(UITheme.FONT_SUBHEADER);
        titleLbl.setForeground(accentColor);

        JLabel descLbl = new JLabel("<html><p style='width: 320px;'>" + desc + "</p></html>");
        descLbl.setFont(UITheme.FONT_SMALL);
        descLbl.setForeground(UITheme.TEXT_MUTED);
        descLbl.setBorder(new EmptyBorder(4, 0, 8, 0));

        textPanel.add(titleLbl);
        textPanel.add(descLbl);

        JButton actionBtn = new JButton(btnText);
        actionBtn.setFont(UITheme.FONT_BODY_BOLD);
        actionBtn.setForeground(Color.WHITE);
        actionBtn.setBackground(accentColor);
        actionBtn.setFocusPainted(false);
        actionBtn.setBorder(new EmptyBorder(7, 12, 7, 12));
        actionBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        actionBtn.addActionListener(action);

        card.add(textPanel, BorderLayout.CENTER);
        card.add(actionBtn, BorderLayout.SOUTH);

        return card;
    }

    /**
     * Executes the SOS emergency initiation flow:
     * 1. Confirmation dialog with Confirm and Cancel options.
     * 2. Location authorization & selection dialog.
     * 3. Alert dispatch with duplicate prevention.
     * 4. Accurate, transparent delivery feedback.
     */
    private void handleSosInitiation() {
        // Step 1: Confirmation Dialog
        int confirm = JOptionPane.showConfirmDialog(this,
                "🚨 EMERGENCY SOS CONFIRMATION 🚨\n\n"
                + "Are you sure you want to trigger an emergency alert?\n"
                + "This will record an SOS distress event in MySQL and notify your saved emergency contacts.\n\n"
                + "Do you wish to proceed?",
                "Confirm Emergency Alert",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm != JOptionPane.YES_OPTION) {
            return; // Cancelled by user
        }

        // Step 2: Location Authorization & Selection
        LocationService.LocationData locationData = promptLocationAuthorization();

        // Step 3: Dispatch SOS through SOSAlertService in background to prevent UI freezing
        btnSos.setEnabled(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        SwingWorker<SOSAlertService.SOSDispatchResult, Void> worker = new SwingWorker<SOSAlertService.SOSDispatchResult, Void>() {
            @Override
            protected SOSAlertService.SOSDispatchResult doInBackground() throws Exception {
                return sosService.triggerSOS(locationData);
            }

            @Override
            protected void done() {
                btnSos.setEnabled(true);
                setCursor(Cursor.getDefaultCursor());
                try {
                    SOSAlertService.SOSDispatchResult result = get();
                    // Step 4: Display Comprehensive, Transparent Feedback
                    displaySosDispatchFeedback(result);
                } catch (Exception ex) {
                    Throwable cause = ex.getCause() != null ? ex.getCause() : ex;
                    if (cause instanceof ValidationException) {
                        JOptionPane.showMessageDialog(DashboardScreen.this,
                                cause.getMessage(),
                                "Alert Blocked",
                                JOptionPane.WARNING_MESSAGE);
                    } else if (cause instanceof SQLException) {
                        JOptionPane.showMessageDialog(DashboardScreen.this,
                                DatabaseConnection.getConnectionErrorMessage((SQLException) cause),
                                "Database Alert Error",
                                JOptionPane.ERROR_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(DashboardScreen.this,
                                "Unexpected error during dispatch: " + cause.getMessage(),
                                "System Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        };
        worker.execute();
    }

    /**
     * Solicits user permission and preferred location method (detected IP vs manual address).
     */
    private LocationService.LocationData promptLocationAuthorization() {
        // Quick background check for estimated location
        LocationService.LocationData detected = locationService.detectEstimatedLocation();

        String[] options = {"Share Detected Location", "Enter Manual Location", "Do Not Share Location"};
        int choice = JOptionPane.showOptionDialog(this,
                "LOCATION SHARING PERMISSION\n\n"
                + "Desktop applications do not have mobile GPS chip access.\n"
                + "Detected Network Location: " + detected.displayLocation + "\n\n"
                + "Would you like to include this location in the emergency broadcast?",
                "Authorize Location Sharing",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]);

        if (choice == 0) {
            return detected;
        } else if (choice == 1) {
            String manual = JOptionPane.showInputDialog(this,
                    "Enter your current location, street, or landmark:",
                    "Specify Current Location",
                    JOptionPane.QUESTION_MESSAGE);
            if (manual != null && !manual.trim().isEmpty()) {
                return locationService.createManualLocation(manual.trim());
            }
        }
        return LocationService.LocationData.none();
    }

    /**
     * Renders detailed delivery results, clearly distinguishing succeeded vs failed vs test notifications.
     */
    private void displaySosDispatchFeedback(SOSAlertService.SOSDispatchResult result) {
        StringBuilder sb = new StringBuilder();
        sb.append("🚨 SOS ALERT DISPATCH RESULT\n\n");
        sb.append("• Alert Event ID: #").append(result.alertId).append("\n");
        sb.append("• Overall Status: [").append(result.overallStatus).append("]\n");
        sb.append("• Contacts Contacted: ").append(result.successfulDispatches)
          .append(" of ").append(result.totalContacts).append("\n");

        if (result.locationUsed != null && !result.locationUsed.isEmpty()) {
            sb.append("• Location Attached: ").append(result.locationUsed).append("\n");
        }
        if (result.mapLink != null && !result.mapLink.isEmpty()) {
            sb.append("• Map Link: ").append(result.mapLink).append("\n");
        }

        sb.append("\nRECIPIENT DELIVERY SUMMARY:\n");
        sb.append("--------------------------------------------------\n");

        if (result.deliveryLogs.isEmpty()) {
            sb.append("No saved emergency contacts found in MySQL.\n");
            sb.append("Please add trusted guardians in the Emergency Contacts screen.\n");
        } else {
            for (NotificationService.NotificationResult log : result.deliveryLogs) {
                sb.append("• ").append(log.contactName).append(" (").append(log.target).append("):\n")
                  .append("  Status: [").append(log.deliveryStatus).append("] via ").append(log.channel).append("\n")
                  .append("  Details: ").append(log.details).append("\n");
            }
        }

        sb.append("--------------------------------------------------\n");
        sb.append("Stored in MySQL: shesafe_db.sos_alerts (Alert #").append(result.alertId).append(")\n");
        sb.append("View full history anytime under 'SOS Alert History'.");

        int msgType = "DELIVERED".equals(result.overallStatus) ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.WARNING_MESSAGE;
        JOptionPane.showMessageDialog(this, sb.toString(), "SOS Alert Status", msgType);
    }

    private void handleLogout() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to log out of SheSafe?",
                "Confirm Logout",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            SessionManager.logout();
            new LoginScreen().setVisible(true);
            dispose();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new DashboardScreen("Priya Sharma").setVisible(true));
    }
}
