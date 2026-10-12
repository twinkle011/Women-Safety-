package com.shesafe.service;

import com.shesafe.dao.UserDAO;
import com.shesafe.model.User;

import java.sql.SQLException;
import java.util.regex.Pattern;

/**
 * AuthService
 * Encapsulates core business rules and validation for user registration,
 * authentication, and session initialization.
 */
public class AuthService {

    private final UserDAO userDAO;

    // Strict validation regex patterns
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\d{10}$");

    public AuthService() {
        this.userDAO = new UserDAO();
    }

    public AuthService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    /**
     * Validates and registers a new user in the system.
     *
     * @param fullName Full legal name
     * @param email Valid email address
     * @param phone 10-digit mobile number
     * @param password Password (min 6 chars)
     * @param confirmPassword Must match password
     * @return Newly registered User with generated ID
     * @throws ValidationException for invalid field entries
     * @throws SQLException if a database error occurs
     */
    public User registerUser(String fullName, String email, String phone, String password, String confirmPassword)
            throws ValidationException, SQLException {

        // 1. Mandatory field checks
        if (fullName == null || fullName.trim().isEmpty()) {
            throw new ValidationException("Full Name is required.");
        }
        if (fullName.trim().length() < 2) {
            throw new ValidationException("Full Name must be at least 2 characters.");
        }

        if (email == null || email.trim().isEmpty()) {
            throw new ValidationException("Email Address is required.");
        }

        if (phone == null || phone.trim().isEmpty()) {
            throw new ValidationException("Phone Number is required.");
        }

        if (password == null || password.isEmpty()) {
            throw new ValidationException("Password is required.");
        }

        if (confirmPassword == null || confirmPassword.isEmpty()) {
            throw new ValidationException("Please confirm your password.");
        }

        String cleanEmail = email.trim().toLowerCase();
        String cleanPhone = phone.trim();

        // 2. Email format validation
        if (!EMAIL_PATTERN.matcher(cleanEmail).matches()) {
            throw new ValidationException("Please enter a valid email address (e.g. user@example.com).");
        }

        // 3. Phone format validation (10 digits)
        if (!PHONE_PATTERN.matcher(cleanPhone).matches()) {
            throw new ValidationException("Phone number must contain exactly 10 digits.");
        }

        // 4. Password length validation
        if (password.length() < 6) {
            throw new ValidationException("Password must be at least 6 characters long for security.");
        }

        // 5. Password match check
        if (!password.equals(confirmPassword)) {
            throw new ValidationException("Passwords do not match. Please re-enter.");
        }

        // 6. Check if email already registered
        if (userDAO.emailExists(cleanEmail)) {
            throw new ValidationException("The email address '" + cleanEmail + "' is already registered. Please login instead.");
        }

        // 7. Persist user into database
        User newUser = new User(fullName.trim(), cleanEmail, cleanPhone);
        boolean created = userDAO.registerUser(newUser, password);

        if (!created) {
            throw new SQLException("Failed to create user account in database.");
        }

        return newUser;
    }

    /**
     * Authenticates a user and starts an active session in SessionManager.
     *
     * @param email User email
     * @param password User raw password
     * @return Authenticated User object
     * @throws ValidationException if input format is invalid
     * @throws AuthenticationException if credentials do not match
     * @throws SQLException if a database error occurs
     */
    public User loginUser(String email, String password)
            throws ValidationException, AuthenticationException, SQLException {

        if (email == null || email.trim().isEmpty()) {
            throw new ValidationException("Please enter your email address.");
        }

        if (password == null || password.isEmpty()) {
            throw new ValidationException("Please enter your password.");
        }

        String cleanEmail = email.trim().toLowerCase();

        if (!cleanEmail.contains("@") || !cleanEmail.contains(".")) {
            throw new ValidationException("Please enter a valid email format.");
        }

        User authenticated = userDAO.authenticate(cleanEmail, password);

        if (authenticated == null) {
            throw new AuthenticationException("Invalid email or password. Please verify your credentials.");
        }

        // Initialize active user session
        SessionManager.login(authenticated);
        return authenticated;
    }

    /**
     * Clears active session.
     */
    public void logoutUser() {
        SessionManager.logout();
    }
}
