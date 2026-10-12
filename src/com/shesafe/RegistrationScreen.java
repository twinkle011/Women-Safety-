package com.shesafe;

import com.shesafe.database.DatabaseConnection;
import com.shesafe.model.User;
import com.shesafe.service.AuthService;
import com.shesafe.service.ValidationException;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.SQLException;

/**
 * Screen 2: RegistrationScreen
 * Handles user account registration.
 * Phase 3: Integrates with AuthService for full input validation,
 * BCrypt password hashing, and MySQL persistence.
 */
public class RegistrationScreen extends JFrame {

    private JTextField txtFullName;
    private JTextField txtEmail;
    private JTextField txtPhone;
    private JPasswordField txtPassword;
    private JPasswordField txtConfirmPassword;

    private final AuthService authService = new AuthService();

    public RegistrationScreen() {
        UITheme.configureFrame(this, "User Registration", 850, 640);
        initUI();
    }

    private void initUI() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(UITheme.BG_LIGHT);

        // Header Banner
        JPanel header = UITheme.createHeaderBanner(
                "Create Your SheSafe Account",
                "Join the protected network. Credentials are encrypted and stored in MySQL."
        );

        // Center Form Container
        JPanel centerWrapper = new JPanel(new GridBagLayout());
        centerWrapper.setOpaque(false);
        centerWrapper.setBorder(new EmptyBorder(15, 20, 15, 20));

        JPanel formCard = UITheme.createCardPanel();
        formCard.setLayout(new GridBagLayout());
        formCard.setPreferredSize(new Dimension(520, 430));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 10, 6, 10);

        // Form Fields
        txtFullName = UITheme.createTextField(22);
        txtEmail = UITheme.createTextField(22);
        txtPhone = UITheme.createTextField(22);
        txtPassword = UITheme.createPasswordField(22);
        txtConfirmPassword = UITheme.createPasswordField(22);

        // Row 0: Full Name
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.35;
        formCard.add(createFieldLabel("Full Name:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 0.65;
        formCard.add(txtFullName, gbc);

        // Row 1: Email Address
        gbc.gridx = 0; gbc.gridy = 1;
        formCard.add(createFieldLabel("Email Address:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1;
        formCard.add(txtEmail, gbc);

        // Row 2: Phone Number
        gbc.gridx = 0; gbc.gridy = 2;
        formCard.add(createFieldLabel("Phone Number (10 digits):"), gbc);
        gbc.gridx = 1; gbc.gridy = 2;
        formCard.add(txtPhone, gbc);

        // Row 3: Password
        gbc.gridx = 0; gbc.gridy = 3;
        formCard.add(createFieldLabel("Password (min 6 chars):"), gbc);
        gbc.gridx = 1; gbc.gridy = 3;
        formCard.add(txtPassword, gbc);

        // Row 4: Confirm Password
        gbc.gridx = 0; gbc.gridy = 4;
        formCard.add(createFieldLabel("Confirm Password:"), gbc);
        gbc.gridx = 1; gbc.gridy = 4;
        formCard.add(txtConfirmPassword, gbc);

        // Row 5: Action Buttons
        JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonRow.setOpaque(false);

        JButton btnBack = UITheme.createSecondaryButton("← Back");
        JButton btnRegister = UITheme.createAccentButton("Complete Registration");

        btnBack.addActionListener(e -> navigateBackToWelcome());
        btnRegister.addActionListener(e -> handleRegistration());

        buttonRow.add(btnBack);
        buttonRow.add(btnRegister);

        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        gbc.insets = new Insets(18, 10, 8, 10);
        formCard.add(buttonRow, gbc);

        // Row 6: Quick Login Link
        JPanel linkPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        linkPanel.setOpaque(false);
        JLabel lblPrompt = new JLabel("Already registered?");
        lblPrompt.setFont(UITheme.FONT_SMALL);
        lblPrompt.setForeground(UITheme.TEXT_MUTED);

        JButton btnGoLogin = new JButton("Login here");
        btnGoLogin.setFont(UITheme.FONT_SMALL);
        btnGoLogin.setForeground(UITheme.PURPLE_PRIMARY);
        btnGoLogin.setBorderPainted(false);
        btnGoLogin.setContentAreaFilled(false);
        btnGoLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnGoLogin.addActionListener(e -> {
            new LoginScreen().setVisible(true);
            dispose();
        });

        linkPanel.add(lblPrompt);
        linkPanel.add(btnGoLogin);

        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2;
        gbc.insets = new Insets(4, 10, 5, 10);
        formCard.add(linkPanel, gbc);

        centerWrapper.add(formCard);

        mainPanel.add(header, BorderLayout.NORTH);
        mainPanel.add(centerWrapper, BorderLayout.CENTER);

        setContentPane(mainPanel);
    }

    private JLabel createFieldLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(UITheme.FONT_BODY_BOLD);
        label.setForeground(UITheme.TEXT_DARK);
        return label;
    }

    /**
     * Executes registration through AuthService with comprehensive validation.
     */
    private void handleRegistration() {
        String name = txtFullName.getText();
        String email = txtEmail.getText();
        String phone = txtPhone.getText();
        String password = new String(txtPassword.getPassword());
        String confirmPassword = new String(txtConfirmPassword.getPassword());

        try {
            User registeredUser = authService.registerUser(name, email, phone, password, confirmPassword);

            JOptionPane.showMessageDialog(this,
                    "Registration Successful!\n\nWelcome to SheSafe, " + registeredUser.getFullName() + "!\n"
                    + "Your account has been registered in MySQL (User ID #" + registeredUser.getUserId() + ").\n"
                    + "Please sign in with your credentials.",
                    "Account Created",
                    JOptionPane.INFORMATION_MESSAGE);

            // Navigate to Login Screen with email prefilled
            new LoginScreen(registeredUser.getEmail()).setVisible(true);
            dispose();

        } catch (ValidationException ve) {
            JOptionPane.showMessageDialog(this,
                    ve.getMessage(),
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
        } catch (SQLException se) {
            JOptionPane.showMessageDialog(this,
                    DatabaseConnection.getConnectionErrorMessage(se),
                    "Database Registration Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void navigateBackToWelcome() {
        new WelcomeScreen().setVisible(true);
        dispose();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new RegistrationScreen().setVisible(true));
    }
}
