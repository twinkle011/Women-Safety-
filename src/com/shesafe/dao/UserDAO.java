package com.shesafe.dao;

import com.shesafe.database.DatabaseConnection;
import com.shesafe.database.PasswordUtil;
import com.shesafe.model.User;

import java.sql.*;

/**
 * UserDAO
 * Data Access Object for 'users' table operations.
 * Implements parameterized queries, password hashing, and user authentication.
 */
public class UserDAO {

    /**
     * Registers a new user into the database with a hashed password.
     *
     * @param user User object containing fullName, email, and phone
     * @param plainPassword Raw password to be securely hashed
     * @return true if successfully inserted, false otherwise
     * @throws SQLException if a database error occurs
     */
    public boolean registerUser(User user, String plainPassword) throws SQLException {
        if (emailExists(user.getEmail())) {
            throw new SQLException("The email address '" + user.getEmail() + "' is already registered.");
        }

        String sql = "INSERT INTO users (full_name, email, phone, password_hash) VALUES (?, ?, ?, ?)";
        String passwordHash = PasswordUtil.hashPassword(plainPassword);

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, user.getFullName());
            pstmt.setString(2, user.getEmail().trim().toLowerCase());
            pstmt.setString(3, user.getPhone().trim());
            pstmt.setString(4, passwordHash);

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        user.setUserId(generatedKeys.getInt(1));
                    }
                }
                user.setPasswordHash(passwordHash);
                return true;
            }
            return false;
        }
    }

    /**
     * Authenticates a user using email and plain-text password.
     *
     * @param email User email
     * @param plainPassword User raw password
     * @return Authenticated User object if credentials match, null otherwise
     * @throws SQLException if database query fails
     */
    public User authenticate(String email, String plainPassword) throws SQLException {
        String sql = "SELECT user_id, full_name, email, phone, password_hash, created_at FROM users WHERE email = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, email.trim().toLowerCase());

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String storedHash = rs.getString("password_hash");

                    // Verify hashed password
                    if (PasswordUtil.verifyPassword(plainPassword, storedHash)) {
                        return new User(
                                rs.getInt("user_id"),
                                rs.getString("full_name"),
                                rs.getString("email"),
                                rs.getString("phone"),
                                storedHash,
                                rs.getTimestamp("created_at")
                        );
                    }
                }
            }
        }
        return null;
    }

    /**
     * Retrieves user details by email.
     *
     * @param email User email
     * @return User object or null if not found
     */
    public User getUserByEmail(String email) throws SQLException {
        String sql = "SELECT user_id, full_name, email, phone, password_hash, created_at FROM users WHERE email = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, email.trim().toLowerCase());

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new User(
                            rs.getInt("user_id"),
                            rs.getString("full_name"),
                            rs.getString("email"),
                            rs.getString("phone"),
                            rs.getString("password_hash"),
                            rs.getTimestamp("created_at")
                    );
                }
            }
        }
        return null;
    }

    /**
     * Checks if an email is already taken by another account.
     */
    public boolean emailExists(String email) throws SQLException {
        String sql = "SELECT COUNT(*) FROM users WHERE email = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, email.trim().toLowerCase());

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }
}
