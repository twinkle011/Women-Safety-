package com.shesafe.service;

import com.shesafe.model.User;

import javax.swing.*;

/**
 * SessionManager
 * Manages the authenticated user's session state throughout the application lifecycle.
 * Prevents unauthorized access to protected dashboard screens.
 */
public class SessionManager {

    private static User currentUser = null;

    /**
     * Sets the active authenticated user session.
     */
    public static synchronized void login(User user) {
        currentUser = user;
    }

    /**
     * Terminates the current user session.
     */
    public static synchronized void logout() {
        currentUser = null;
    }

    /**
     * Checks if a user is currently authenticated.
     */
    public static synchronized boolean isLoggedIn() {
        return currentUser != null;
    }

    /**
     * Retrieves the active user session.
     */
    public static synchronized User getCurrentUser() {
        return currentUser;
    }

    /**
     * Retrieves the ID of the logged-in user.
     * Throws IllegalStateException if called without an active session.
     */
    public static synchronized int getCurrentUserId() {
        if (!isLoggedIn()) {
            throw new IllegalStateException("No active user session. Access is restricted.");
        }
        return currentUser.getUserId();
    }

    /**
     * Guard method that verifies an active login session before opening protected screens.
     * If no user is logged in, warns the user, disposes the caller, and redirects to LoginScreen.
     *
     * @param callingFrame The frame attempting to open
     * @return true if authorized, false if redirected
     */
    public static boolean requireLogin(JFrame callingFrame) {
        if (!isLoggedIn()) {
            JOptionPane.showMessageDialog(callingFrame,
                    "Access Denied:\nYou must be logged in to access this security feature.",
                    "Authentication Required",
                    JOptionPane.WARNING_MESSAGE);

            if (callingFrame != null) {
                callingFrame.dispose();
            }

            // Redirect to LoginScreen via reflection to avoid circular compile dependencies
            try {
                Class<?> loginClass = Class.forName("com.shesafe.LoginScreen");
                JFrame loginFrame = (JFrame) loginClass.getDeclaredConstructor().newInstance();
                loginFrame.setVisible(true);
            } catch (Exception e) {
                System.err.println("[SessionManager] Failed to redirect to LoginScreen: " + e.getMessage());
            }
            return false;
        }
        return true;
    }
}
