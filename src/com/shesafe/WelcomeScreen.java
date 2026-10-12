package com.shesafe;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Screen 1: WelcomeScreen
 * The initial landing screen of the SheSafe application.
 * Introduces the brand, tagline, and provides entry points for Login and Registration.
 */
public class WelcomeScreen extends JFrame {

    public WelcomeScreen() {
        UITheme.configureFrame(this, "Welcome", 850, 600);
        initUI();
    }

    private void initUI() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(UITheme.BG_LIGHT);

        // TOP HEADER BRANDING
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(UITheme.PURPLE_PRIMARY);
        headerPanel.setBorder(new EmptyBorder(25, 40, 25, 40));

        JLabel appTitle = new JLabel("🛡 SheSafe", JLabel.CENTER);
        appTitle.setFont(new Font("Segoe UI", Font.BOLD, 36));
        appTitle.setForeground(Color.WHITE);

        JLabel tagline = new JLabel("Your Safety, Our Priority", JLabel.CENTER);
        tagline.setFont(new Font("Segoe UI", Font.ITALIC, 17));
        tagline.setForeground(new Color(248, 187, 208)); // Light Rose
        tagline.setBorder(new EmptyBorder(6, 0, 0, 0));

        headerPanel.add(appTitle, BorderLayout.NORTH);
        headerPanel.add(tagline, BorderLayout.SOUTH);

        // CENTER HERO & FEATURE CARDS
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setOpaque(false);
        centerPanel.setBorder(new EmptyBorder(30, 60, 20, 60));

        JLabel welcomeIntro = new JLabel("Welcome to SheSafe Emergency & Personal Protection System", JLabel.CENTER);
        welcomeIntro.setFont(new Font("Segoe UI", Font.BOLD, 18));
        welcomeIntro.setForeground(UITheme.TEXT_DARK);
        welcomeIntro.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel welcomeSub = new JLabel("Empowering women with instant alerts, verified contacts, and safety resources.", JLabel.CENTER);
        welcomeSub.setFont(UITheme.FONT_BODY);
        welcomeSub.setForeground(UITheme.TEXT_MUTED);
        welcomeSub.setAlignmentX(Component.CENTER_ALIGNMENT);
        welcomeSub.setBorder(new EmptyBorder(6, 0, 25, 0));

        // 3 Key Feature Highlight Cards in a Row
        JPanel featuresPanel = new JPanel(new GridLayout(1, 3, 18, 0));
        featuresPanel.setOpaque(false);
        featuresPanel.setMaximumSize(new Dimension(720, 140));

        featuresPanel.add(createFeatureCard("🚨 Instant SOS", "Trigger fast emergency alerts and simulated dispatch with one click."));
        featuresPanel.add(createFeatureCard("👥 Trusted Circle", "Maintain instant access to family, friends, and official helplines."));
        featuresPanel.add(createFeatureCard("📝 Incident Reports", "Record safety events and access verified emergency guidelines."));

        centerPanel.add(welcomeIntro);
        centerPanel.add(welcomeSub);
        centerPanel.add(featuresPanel);

        // BOTTOM ACTION PANEL (Login & Register Buttons)
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 25, 25));
        bottomPanel.setBackground(UITheme.BG_LIGHT);
        bottomPanel.setBorder(new EmptyBorder(10, 20, 30, 20));

        JButton btnLogin = UITheme.createPrimaryButton("   🔑 Login to Account   ");
        btnLogin.setPreferredSize(new Dimension(220, 48));

        JButton btnRegister = UITheme.createAccentButton("   ✨ Create New Account   ");
        btnRegister.setPreferredSize(new Dimension(230, 48));

        // Action Listeners
        btnLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new LoginScreen().setVisible(true);
                dispose(); // Close Welcome Screen
            }
        });

        btnRegister.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new RegistrationScreen().setVisible(true);
                dispose(); // Close Welcome Screen
            }
        });

        bottomPanel.add(btnLogin);
        bottomPanel.add(btnRegister);

        // Assemble Screen
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        setContentPane(mainPanel);
    }

    private JPanel createFeatureCard(String title, String desc) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(UITheme.BORDER_COLOR, 1, true),
                new EmptyBorder(16, 16, 16, 16)
        ));

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(UITheme.FONT_SUBHEADER);
        titleLbl.setForeground(UITheme.PURPLE_PRIMARY);
        titleLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel descLbl = new JLabel("<html><p style='width: 150px;'>" + desc + "</p></html>");
        descLbl.setFont(UITheme.FONT_SMALL);
        descLbl.setForeground(UITheme.TEXT_MUTED);
        descLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        descLbl.setBorder(new EmptyBorder(8, 0, 0, 0));

        card.add(titleLbl);
        card.add(descLbl);
        return card;
    }

    // Direct launch for testing
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new WelcomeScreen().setVisible(true));
    }
}
