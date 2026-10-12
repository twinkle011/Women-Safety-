package com.shesafe;

import com.shesafe.model.User;
import com.shesafe.service.SessionManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;

/**
 * Screen 6: SafetyTipsScreen
 * Displays essential personal safety guidelines, awareness practices, and emergency directories.
 * Phase 3: Operates purely in-memory (no database required) and adheres to safety protocol standards.
 */
public class SafetyTipsScreen extends JFrame {

    private User currentUser;

    public SafetyTipsScreen() {
        if (!SessionManager.requireLogin(this)) {
            return;
        }
        this.currentUser = SessionManager.getCurrentUser();
        UITheme.configureFrame(this, "Safety Guidelines & Tips", 880, 620);
        initUI();
    }

    public SafetyTipsScreen(User user) {
        if (user != null) {
            SessionManager.login(user);
        }
        if (!SessionManager.requireLogin(this)) {
            return;
        }
        this.currentUser = SessionManager.getCurrentUser();
        UITheme.configureFrame(this, "Safety Guidelines & Tips", 880, 620);
        initUI();
    }

    public SafetyTipsScreen(String userName) {
        this(new User(1, userName, userName.toLowerCase().replace(" ", "") + "@shesafe.com", "9876543210", null, null));
    }

    private void initUI() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(UITheme.BG_LIGHT);

        // Header Banner
        JPanel header = UITheme.createHeaderBanner(
                "Personal Safety Guidelines & Preparedness",
                "Knowledge and proactive measures are your strongest protection. Review these verified protocols."
        );

        // Scrollable Tips Container
        JPanel tipsContainer = new JPanel();
        tipsContainer.setLayout(new BoxLayout(tipsContainer, BoxLayout.Y_AXIS));
        tipsContainer.setBackground(UITheme.BG_LIGHT);
        tipsContainer.setBorder(new EmptyBorder(20, 30, 20, 30));

        // Category 1: Travel & Transit Preparedness
        tipsContainer.add(createTipCard(
                "🚗 1. Travel Planning & Transit Awareness",
                UITheme.PURPLE_PRIMARY,
                "• <b>Keep trusted people informed about your travel plans:</b> Share your expected arrival time, vehicle registration number, and route before starting a trip.<br>"
                + "• <b>Use trusted transport options when possible:</b> Prioritize licensed public transit, verified ride-sharing platforms with in-app tracking, or pre-arranged transport.<br>"
                + "• <b>Verify child locks:</b> When entering an auto or cab, confirm that doors and handles open from the inside."
        ));
        tipsContainer.add(Box.createRigidArea(new Dimension(0, 14)));

        // Category 2: Device & Power Preparedness
        tipsContainer.add(createTipCard(
                "📱 2. Mobile Device Readiness & Contact Directory",
                new Color(0, 121, 107), // Deep Teal
                "• <b>Keep your phone charged and accessible:</b> Carry a portable power bank and keep your phone in an easily reachable pocket—not buried in a bag.<br>"
                + "• <b>Save important emergency numbers:</b> Store local police precincts and family contacts on speed dial and home screen lock widgets.<br>"
                + "• <b>Hardware shortcuts:</b> Familiarize yourself with emergency SOS hotkeys (such as 3x/5x rapid power button tap on modern smartphones)."
        ));
        tipsContainer.add(Box.createRigidArea(new Dimension(0, 14)));

        // Category 3: Situational Awareness & Instincts
        tipsContainer.add(createTipCard(
                "👁 3. Situational Vigilance & Environmental Awareness",
                UITheme.PINK_ACCENT,
                "• <b>Stay aware of your surroundings:</b> Avoid using noise-canceling headphones or staring at screens while walking through isolated streets or transit hubs.<br>"
                + "• <b>Trust your intuition:</b> If someone follows you or an area feels unsafe, enter a brightly lit shop or public place immediately.<br>"
                + "• <b>Carry non-lethal defense tools:</b> Keep a safety whistle, loud alarm beacon, or defense spray within easy reach."
        ));
        tipsContainer.add(Box.createRigidArea(new Dimension(0, 14)));

        // Category 4: Emergency Response & Helplines
        tipsContainer.add(createTipCard(
                "🚨 4. Contact Local Emergency Services Immediately",
                UITheme.SOS_RED,
                "• <b>Contact local emergency services when necessary:</b> Never hesitate to alert emergency authorities early before a situation escalates.<br>"
                + "• <b>National Unified Emergency: 112</b> (Police, Fire, and Ambulance response across India)<br>"
                + "• <b>Women Helpline: 1091</b> (24/7 dedicated distress and counseling helpline)<br>"
                + "• <b>Women Anti-Harassment Powerline: 1090</b> | <b>NCW Domestic Helpline: 181</b>"
        ));

        JScrollPane scrollPane = new JScrollPane(tipsContainer);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        // Bottom Navigation Bar
        JPanel bottomBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 15));
        bottomBar.setBackground(UITheme.BG_LIGHT);

        JButton btnBack = UITheme.createBackButton();
        btnBack.addActionListener(e -> {
            new DashboardScreen().setVisible(true);
            dispose();
        });

        bottomBar.add(btnBack);

        // Assemble Screen
        mainPanel.add(header, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(bottomBar, BorderLayout.SOUTH);

        setContentPane(mainPanel);
    }

    private JPanel createTipCard(String title, Color headerColor, String htmlContent) {
        JPanel card = UITheme.createCardPanel();
        card.setLayout(new BorderLayout());
        card.setMaximumSize(new Dimension(800, 190));
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(UITheme.BORDER_COLOR, 1, true),
                new EmptyBorder(14, 18, 14, 18)
        ));

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(UITheme.FONT_SUBHEADER);
        titleLbl.setForeground(headerColor);
        titleLbl.setBorder(new EmptyBorder(0, 0, 8, 0));

        JLabel bodyLbl = new JLabel("<html><body style='font-family: Segoe UI; font-size: 11pt; color: #333333; line-height: 1.45;'>"
                + htmlContent + "</body></html>");

        card.add(titleLbl, BorderLayout.NORTH);
        card.add(bodyLbl, BorderLayout.CENTER);
        return card;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SafetyTipsScreen("Priya Sharma").setVisible(true));
    }
}
