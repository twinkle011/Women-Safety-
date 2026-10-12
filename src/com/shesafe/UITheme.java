package com.shesafe;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * UITheme provides consistent color palettes, typography, borders,
 * and component factory methods across all SheSafe screens.
 */
public class UITheme {

    // Modern Women's Safety Color Palette
    public static final Color PURPLE_PRIMARY    = new Color(106, 27, 154);  // Deep Royal Purple (#6A1B9A)
    public static final Color PURPLE_LIGHT      = new Color(142, 36, 170);  // Medium Purple (#8E24AA)
    public static final Color PURPLE_HOVER      = new Color(74, 20, 140);   // Dark Purple Hover (#4A148C)
    public static final Color PINK_ACCENT       = new Color(233, 30, 99);   // Vibrant Pink (#E91E63)
    public static final Color PINK_LIGHT        = new Color(252, 228, 236); // Soft Pastel Pink (#FCE4EC)
    public static final Color BG_LIGHT          = new Color(248, 245, 252); // Soft Lavender-White
    public static final Color CARD_BG           = Color.WHITE;              // Pure White Card Panel
    public static final Color TEXT_DARK         = new Color(33, 33, 33);    // High-contrast Dark Charcoal
    public static final Color TEXT_MUTED        = new Color(117, 117, 117); // Muted Gray Subtitles
    public static final Color BORDER_COLOR      = new Color(225, 215, 235); // Gentle Border
    public static final Color SOS_RED           = new Color(211, 47, 47);   // Emergency Alert Red (#D32F2F)
    public static final Color SOS_RED_HOVER     = new Color(183, 28, 28);   // Dark Emergency Red
    public static final Color SUCCESS_GREEN     = new Color(46, 125, 50);   // Safe Green (#2E7D32)

    // Standard Typography
    public static final Font FONT_HERO_TITLE    = new Font("Segoe UI", Font.BOLD, 28);
    public static final Font FONT_HEADER        = new Font("Segoe UI", Font.BOLD, 20);
    public static final Font FONT_SUBHEADER     = new Font("Segoe UI", Font.BOLD, 15);
    public static final Font FONT_BODY          = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_BODY_BOLD     = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font FONT_SMALL         = new Font("Segoe UI", Font.PLAIN, 11);

    /**
     * Creates a primary styled action button (Purple background, white text).
     */
    public static JButton createPrimaryButton(String text) {
        JButton button = new JButton(text);
        button.setFont(FONT_BODY_BOLD);
        button.setForeground(Color.WHITE);
        button.setBackground(PURPLE_PRIMARY);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(PURPLE_PRIMARY, 1, true),
                new EmptyBorder(10, 20, 10, 20)
        ));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setOpaque(true);

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(PURPLE_HOVER);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(PURPLE_PRIMARY);
            }
        });
        return button;
    }

    /**
     * Creates an accent styled button (Pink background, white text).
     */
    public static JButton createAccentButton(String text) {
        JButton button = new JButton(text);
        button.setFont(FONT_BODY_BOLD);
        button.setForeground(Color.WHITE);
        button.setBackground(PINK_ACCENT);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(PINK_ACCENT, 1, true),
                new EmptyBorder(10, 20, 10, 20)
        ));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setOpaque(true);

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(194, 24, 91));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(PINK_ACCENT);
            }
        });
        return button;
    }

    /**
     * Creates an outline / secondary navigation button (White background, purple text).
     */
    public static JButton createSecondaryButton(String text) {
        JButton button = new JButton(text);
        button.setFont(FONT_BODY_BOLD);
        button.setForeground(PURPLE_PRIMARY);
        button.setBackground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(PURPLE_PRIMARY, 1, true),
                new EmptyBorder(8, 16, 8, 16)
        ));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setOpaque(true);

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(PINK_LIGHT);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(Color.WHITE);
            }
        });
        return button;
    }

    /**
     * Creates an SOS emergency button with high visual prominence.
     */
    public static JButton createSosButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 22));
        button.setForeground(Color.WHITE);
        button.setBackground(SOS_RED);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(183, 28, 28), 2, true),
                new EmptyBorder(18, 30, 18, 30)
        ));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setOpaque(true);

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(SOS_RED_HOVER);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(SOS_RED);
            }
        });
        return button;
    }

    /**
     * Creates a standard back navigation button.
     */
    public static JButton createBackButton() {
        return createSecondaryButton("← Back");
    }

    /**
     * Creates a styled text field with comfortable padding and clean border.
     */
    public static JTextField createTextField(int columns) {
        JTextField textField = new JTextField(columns);
        textField.setFont(FONT_BODY);
        textField.setForeground(TEXT_DARK);
        textField.setBackground(Color.WHITE);
        textField.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(8, 10, 8, 10)
        ));
        return textField;
    }

    /**
     * Creates a styled password field.
     */
    public static JPasswordField createPasswordField(int columns) {
        JPasswordField passwordField = new JPasswordField(columns);
        passwordField.setFont(FONT_BODY);
        passwordField.setForeground(TEXT_DARK);
        passwordField.setBackground(Color.WHITE);
        passwordField.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(8, 10, 8, 10)
        ));
        return passwordField;
    }

    /**
     * Creates a standardized card panel with white background and subtle rounded border.
     */
    public static JPanel createCardPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(CARD_BG);
        panel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(20, 24, 20, 24)
        ));
        return panel;
    }

    /**
     * Creates a stylized header banner for screens.
     */
    public static JPanel createHeaderBanner(String title, String subtitle) {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(PURPLE_PRIMARY);
        header.setBorder(new EmptyBorder(18, 24, 18, 24));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(FONT_HEADER);
        titleLabel.setForeground(Color.WHITE);

        JLabel subtitleLabel = new JLabel(subtitle);
        subtitleLabel.setFont(FONT_SMALL);
        subtitleLabel.setForeground(new Color(243, 229, 245));

        header.add(titleLabel, BorderLayout.NORTH);
        header.add(subtitleLabel, BorderLayout.SOUTH);
        return header;
    }

    /**
     * Base window setup for all SheSafe screens.
     */
    public static void configureFrame(JFrame frame, String title, int width, int height) {
        frame.setTitle("SheSafe - " + title);
        frame.setSize(width, height);
        frame.setMinimumSize(new Dimension(width, height));
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setBackground(BG_LIGHT);
    }
}
