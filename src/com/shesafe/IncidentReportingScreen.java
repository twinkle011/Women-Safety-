package com.shesafe;

import com.shesafe.database.DatabaseConnection;
import com.shesafe.model.IncidentReport;
import com.shesafe.model.User;
import com.shesafe.service.IncidentService;
import com.shesafe.service.SessionManager;
import com.shesafe.service.ValidationException;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Screen 7: IncidentReportingScreen
 * Captures, validates, and stores formal incident reports into MySQL.
 * Phase 3: Connected to IncidentService with input validation, error handling,
 * and user-isolated historical incident review.
 */
public class IncidentReportingScreen extends JFrame {

    private User currentUser;
    private final IncidentService incidentService = new IncidentService();

    private JComboBox<String> cmbIncidentType;
    private JTextField txtLocation;
    private JTextField txtDate;
    private JComboBox<String> cmbSeverity;
    private JTextArea txtDescription;

    public IncidentReportingScreen() {
        if (!SessionManager.requireLogin(this)) {
            return;
        }
        this.currentUser = SessionManager.getCurrentUser();
        UITheme.configureFrame(this, "Incident Reporting", 880, 650);
        initUI();
    }

    public IncidentReportingScreen(User user) {
        if (user != null) {
            SessionManager.login(user);
        }
        if (!SessionManager.requireLogin(this)) {
            return;
        }
        this.currentUser = SessionManager.getCurrentUser();
        UITheme.configureFrame(this, "Incident Reporting", 880, 650);
        initUI();
    }

    public IncidentReportingScreen(String userName) {
        this(new User(1, userName, userName.toLowerCase().replace(" ", "") + "@shesafe.com", "9876543210", null, null));
    }

    private void initUI() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(UITheme.BG_LIGHT);

        // Header Banner
        JPanel header = UITheme.createHeaderBanner(
                "Confidential Incident Reporting (MySQL)",
                "Document safety incidents securely. Reporter: " + currentUser.getFullName() + " (User ID #" + currentUser.getUserId() + ")"
        );

        // Center Form Container
        JPanel centerWrapper = new JPanel(new GridBagLayout());
        centerWrapper.setOpaque(false);
        centerWrapper.setBorder(new EmptyBorder(15, 25, 15, 25));

        JPanel formCard = UITheme.createCardPanel();
        formCard.setLayout(new GridBagLayout());
        formCard.setPreferredSize(new Dimension(660, 480));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 12, 6, 12);

        // 1. Incident Type
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.3;
        formCard.add(createLabel("Incident Category:"), gbc);

        String[] incidentTypes = {
                "Street Harassment / Eve Teasing",
                "Stalking / Following",
                "Physical Assault / Threat",
                "Verbal Abuse / Intimidation",
                "Suspicious Person / Activity",
                "Unsafe Public Area (Poor Lighting / Isolated)",
                "Cab / Public Transport Misconduct",
                "Other Security Concern"
        };
        cmbIncidentType = new JComboBox<>(incidentTypes);
        cmbIncidentType.setFont(UITheme.FONT_BODY);
        cmbIncidentType.setBackground(Color.WHITE);
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 0.7;
        formCard.add(cmbIncidentType, gbc);

        // 2. Incident Location
        gbc.gridx = 0; gbc.gridy = 1;
        formCard.add(createLabel("Incident Location:"), gbc);

        txtLocation = UITheme.createTextField(25);
        txtLocation.setToolTipText("e.g. Near Metro Station Gate 2, Central Park Road");
        gbc.gridx = 1; gbc.gridy = 1;
        formCard.add(txtLocation, gbc);

        // 3. Incident Date (YYYY-MM-DD)
        gbc.gridx = 0; gbc.gridy = 2;
        formCard.add(createLabel("Incident Date (YYYY-MM-DD):"), gbc);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        txtDate = UITheme.createTextField(25);
        txtDate.setText(sdf.format(new Date()));
        gbc.gridx = 1; gbc.gridy = 2;
        formCard.add(txtDate, gbc);

        // 4. Severity Level (Informational)
        gbc.gridx = 0; gbc.gridy = 3;
        formCard.add(createLabel("Severity Level:"), gbc);

        String[] severities = {"Low (Informational / Unsafe Lighting)", "Medium (Verbal Threat / Stalking)", "High (Immediate Danger / Physical Threat)"};
        cmbSeverity = new JComboBox<>(severities);
        cmbSeverity.setFont(UITheme.FONT_BODY);
        cmbSeverity.setBackground(Color.WHITE);
        cmbSeverity.setSelectedIndex(1);
        gbc.gridx = 1; gbc.gridy = 3;
        formCard.add(cmbSeverity, gbc);

        // 5. Description Area
        gbc.gridx = 0; gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        formCard.add(createLabel("Detailed Description:"), gbc);

        txtDescription = new JTextArea(4, 25);
        txtDescription.setFont(UITheme.FONT_BODY);
        txtDescription.setLineWrap(true);
        txtDescription.setWrapStyleWord(true);
        JScrollPane scrollDesc = new JScrollPane(txtDescription);
        scrollDesc.setBorder(new LineBorder(UITheme.BORDER_COLOR, 1, true));
        gbc.gridx = 1; gbc.gridy = 4;
        formCard.add(scrollDesc, gbc);

        // 6. Action Buttons Bar
        JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonRow.setOpaque(false);

        JButton btnBack = UITheme.createBackButton();
        JButton btnHistory = UITheme.createSecondaryButton("📜 My Past Reports");
        JButton btnSubmit = UITheme.createAccentButton("📩 Submit to MySQL");

        btnBack.addActionListener(e -> {
            new DashboardScreen().setVisible(true);
            dispose();
        });

        btnHistory.addActionListener(e -> handleViewPastReports());
        btnSubmit.addActionListener(e -> handleSubmitReport());

        buttonRow.add(btnBack);
        buttonRow.add(btnHistory);
        buttonRow.add(btnSubmit);

        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        gbc.insets = new Insets(18, 12, 6, 12);
        formCard.add(buttonRow, gbc);

        centerWrapper.add(formCard);

        mainPanel.add(header, BorderLayout.NORTH);
        mainPanel.add(centerWrapper, BorderLayout.CENTER);

        setContentPane(mainPanel);
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(UITheme.FONT_BODY_BOLD);
        label.setForeground(UITheme.TEXT_DARK);
        return label;
    }

    private void handleSubmitReport() {
        String type = (String) cmbIncidentType.getSelectedItem();
        String location = txtLocation.getText();
        String dateStr = txtDate.getText();
        String description = txtDescription.getText();

        try {
            IncidentReport report = incidentService.submitReport(type, location, dateStr, description);

            String confirmation = "INCIDENT REPORT STORED IN MYSQL!\n\n"
                    + "• Report ID: #" + report.getReportId() + "\n"
                    + "• User: " + currentUser.getFullName() + " (User ID #" + currentUser.getUserId() + ")\n"
                    + "• Category: " + type + "\n"
                    + "• Location: " + report.getLocation() + "\n"
                    + "• Date: " + report.getIncidentDate() + "\n\n"
                    + "Successfully logged in MySQL table: shesafe_db.incident_reports";

            JOptionPane.showMessageDialog(this, confirmation, "Report Saved to Database", JOptionPane.INFORMATION_MESSAGE);

            // Reset inputs
            txtLocation.setText("");
            txtDescription.setText("");
            cmbIncidentType.setSelectedIndex(0);
            cmbSeverity.setSelectedIndex(1);

        } catch (ValidationException ve) {
            JOptionPane.showMessageDialog(this, ve.getMessage(), "Incomplete Report", JOptionPane.WARNING_MESSAGE);
        } catch (SQLException se) {
            JOptionPane.showMessageDialog(this,
                    DatabaseConnection.getConnectionErrorMessage(se),
                    "Database Save Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleViewPastReports() {
        try {
            List<IncidentReport> reports = incidentService.getReportsForCurrentUser();
            if (reports.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "No incident reports found in MySQL for: " + currentUser.getFullName(),
                        "No History",
                        JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            StringBuilder sb = new StringBuilder();
            sb.append("INCIDENT HISTORY FOR: ").append(currentUser.getFullName())
              .append(" (Total: ").append(reports.size()).append(" reports)\n")
              .append("====================================================\n\n");

            for (IncidentReport r : reports) {
                sb.append("Report ID: #").append(r.getReportId())
                  .append(" | Date: ").append(r.getIncidentDate()).append("\n")
                  .append("Category: ").append(r.getIncidentType()).append("\n")
                  .append("Location: ").append(r.getLocation()).append("\n")
                  .append("Details:  ").append(r.getDescription()).append("\n")
                  .append("----------------------------------------------------\n");
            }

            JTextArea area = new JTextArea(sb.toString(), 16, 50);
            area.setEditable(false);
            area.setFont(UITheme.FONT_BODY);
            JScrollPane scroll = new JScrollPane(area);

            JOptionPane.showMessageDialog(this, scroll, "User Incident Reports (MySQL)", JOptionPane.PLAIN_MESSAGE);

        } catch (ValidationException ve) {
            JOptionPane.showMessageDialog(this, ve.getMessage(), "Session Error", JOptionPane.WARNING_MESSAGE);
        } catch (SQLException se) {
            JOptionPane.showMessageDialog(this,
                    DatabaseConnection.getConnectionErrorMessage(se),
                    "Database Query Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new IncidentReportingScreen("Priya Sharma").setVisible(true));
    }
}
