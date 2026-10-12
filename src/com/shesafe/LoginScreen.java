package com.shesafe;

import com.shesafe.database.DatabaseConnection;
import com.shesafe.model.User;
import com.shesafe.service.AuthService;
import com.shesafe.service.AuthenticationException;
import com.shesafe.service.ValidationException;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.SQLException;

/**
 * Screen 3: LoginScreen
 * Authenticates users and initiates the security session.
 * Phase 3: Uses AuthService to verify BCrypt password hashes and initialize SessionManager.
 */
public class LoginScreen extends JFrame {

    private JTextField txtEmail;
    private JPasswordField txtPassword;

    private final AuthService authService = new AuthService();

    public LoginScreen() {
        this("");
    }

    public LoginScreen(String prefilledEmail) {
        UITheme.configureFrame(this, "User Login", 850, 580);
        initUI(prefilledEmail);
    }

    private void initUI(String prefilledEmail) {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(UITheme.BG_LIGHT);

        // Header Banner
        JPanel header = UITheme.createHeaderBanner(
                "Welcome Back to SheSafe",
                "Sign in securely to activate your protection network and emergency tools."
        );

        // Center Login Card
        JPanel centerWrapper = new JPanel(new GridBagLayout());
        centerWrapper.setOpaque(false);
        centerWrapper.setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel loginCard = UITheme.createCardPanel();
        loginCard.setLayout(new GridBagLayout());
        loginCard.setPreferredSize(new Dimension(480, 360));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 15, 10, 15);

        // Form Fields
        txtEmail = UITheme.createTextField(22);
        if (prefilledEmail != null && !prefilledEmail.isEmpty()) {
            txtEmail.setText(prefilledEmail);
        } else {
            txtEmail.setText("priya@shesafe.com"); // Pre-filled convenience
        }

        txtPassword = UITheme.createPasswordField(22);
        txtPassword.setText("password123");

        // Row 0: Email Label & Input
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.35;
        JLabel lblEmail = new JLabel("Email Address:");
        lblEmail.setFont(UITheme.FONT_BODY_BOLD);
        lblEmail.setForeground(UITheme.TEXT_DARK);
        loginCard.add(lblEmail, gbc);

        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 0.65;
        loginCard.add(txtEmail, gbc);

        // Row 1: Password Label & Input
        gbc.gridx = 0; gbc.gridy = 1;
        JLabel lblPass = new JLabel("Password:");
        lblPass.setFont(UITheme.FONT_BODY_BOLD);
        lblPass.setForeground(UITheme.TEXT_DARK);
        loginCard.add(lblPass, gbc);

        gbc.gridx = 1; gbc.gridy = 1;
        loginCard.add(txtPassword, gbc);

        // Row 2: Database Status Hint
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        JLabel dbHint = new JLabel("<html><i>Phase 3: Verifies BCrypt password hash against MySQL 'users' table.</i></html>");
        dbHint.setFont(UITheme.FONT_SMALL);
        dbHint.setForeground(UITheme.TEXT_MUTED);
        loginCard.add(dbHint, gbc);

        // Row 3: Buttons
        JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonRow.setOpaque(false);

        JButton btnBack = UITheme.createSecondaryButton("← Back");
        JButton btnLogin = UITheme.createPrimaryButton("Login to SheSafe");

        btnBack.addActionListener(e -> {
            new WelcomeScreen().setVisible(true);
            dispose();
        });

        btnLogin.addActionListener(e -> handleLogin());

        buttonRow.add(btnBack);
        buttonRow.add(btnLogin);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        gbc.insets = new Insets(18, 15, 10, 15);
        loginCard.add(buttonRow, gbc);

        // Row 4: Register Navigation Prompt
        JPanel linkPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        linkPanel.setOpaque(false);
        JLabel lblPrompt = new JLabel("Don't have an account?");
        lblPrompt.setFont(UITheme.FONT_SMALL);
        lblPrompt.setForeground(UITheme.TEXT_MUTED);

        JButton btnGoRegister = new JButton("Create an Account");
        btnGoRegister.setFont(UITheme.FONT_SMALL);
        btnGoRegister.setForeground(UITheme.PINK_ACCENT);
        btnGoRegister.setBorderPainted(false);
        btnGoRegister.setContentAreaFilled(false);
        btnGoRegister.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnGoRegister.addActionListener(e -> {
            new RegistrationScreen().setVisible(true);
            dispose();
        });

        linkPanel.add(lblPrompt);
        linkPanel.add(btnGoRegister);

        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        gbc.insets = new Insets(5, 15, 5, 15);
        loginCard.add(linkPanel, gbc);

        centerWrapper.add(loginCard);

        mainPanel.add(header, BorderLayout.NORTH);
        mainPanel.add(centerWrapper, BorderLayout.CENTER);

        setContentPane(mainPanel);
    }

    private void handleLogin() {
        String email = txtEmail.getText();
        String password = new String(txtPassword.getPassword());

        try {
            User authenticatedUser = authService.loginUser(email, password);

            JOptionPane.showMessageDialog(this,
                    "Login Successful!\n\nWelcome, " + authenticatedUser.getFullName() + ".\n"
                    + "Session active for User ID #" + authenticatedUser.getUserId() + ".",
                    "Authentication Successful",
                    JOptionPane.INFORMATION_MESSAGE);

            // Open Dashboard with active session
            new DashboardScreen().setVisible(true);
            dispose();

        } catch (ValidationException ve) {
            JOptionPane.showMessageDialog(this,
                    ve.getMessage(),
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
        } catch (AuthenticationException ae) {
            JOptionPane.showMessageDialog(this,
                    ae.getMessage(),
                    "Authentication Failed",
                    JOptionPane.ERROR_MESSAGE);
        } catch (SQLException se) {
            JOptionPane.showMessageDialog(this,
                    DatabaseConnection.getConnectionErrorMessage(se),
                    "Database Connection Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginScreen().setVisible(true));
    }
}
