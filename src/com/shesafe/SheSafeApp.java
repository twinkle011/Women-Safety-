package com.shesafe;

import com.shesafe.database.DatabaseConnection;

import javax.swing.*;

/**
 * SheSafeApp
 * Main entry point for SheSafe – Women's Safety & Emergency Support System.
 * Phase 2: GUI Design & MySQL Database Integration.
 */
public class SheSafeApp {

    public static void main(String[] args) {
        // 1. Apply System Look and Feel for native, crisp UI rendering
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
            System.err.println("Note: Using default Swing Look and Feel. (" + ex.getMessage() + ")");
        }

        // 2. Perform quick background database connection health check
        new Thread(() -> {
            boolean isConnected = DatabaseConnection.testConnection();
            if (isConnected) {
                System.out.println("=================================================");
                System.out.println("[SheSafe] Successfully connected to MySQL database: shesafe_db");
                System.out.println("=================================================");
            } else {
                System.err.println("=================================================");
                System.err.println("[SheSafe Warning] Could not connect to MySQL database.");
                System.err.println("1. Verify MySQL Server (or XAMPP/WAMP) is running.");
                System.err.println("2. Ensure 'shesafe_db' exists (run database_schema.sql).");
                System.err.println("3. Check credentials in 'db.properties'.");
                System.err.println("=================================================");
            }
        }).start();

        // 3. Launch Application on Swing's Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(() -> {
            WelcomeScreen welcomeScreen = new WelcomeScreen();
            welcomeScreen.setVisible(true);
        });
    }
}
