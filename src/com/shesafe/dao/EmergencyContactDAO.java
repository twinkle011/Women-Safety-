package com.shesafe.dao;

import com.shesafe.database.DatabaseConnection;
import com.shesafe.model.EmergencyContact;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * EmergencyContactDAO
 * Data Access Object for 'emergency_contacts' table.
 * Enforces user isolation so users can only access and modify their own contacts.
 */
public class EmergencyContactDAO {

    /**
     * Retrieves all emergency contacts registered for a specific user.
     *
     * @param userId The ID of the authenticated user
     * @return List of EmergencyContact objects
     * @throws SQLException if a database query error occurs
     */
    public List<EmergencyContact> getContactsByUserId(int userId) throws SQLException {
        List<EmergencyContact> contacts = new ArrayList<>();
        String sql = "SELECT contact_id, user_id, contact_name, contact_phone, relationship "
                   + "FROM emergency_contacts WHERE user_id = ? ORDER BY contact_id ASC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    EmergencyContact contact = new EmergencyContact(
                            rs.getInt("contact_id"),
                            rs.getInt("user_id"),
                            rs.getString("contact_name"),
                            rs.getString("contact_phone"),
                            rs.getString("relationship")
                    );
                    contacts.add(contact);
                }
            }
        }
        return contacts;
    }

    /**
     * Inserts a new emergency contact for the user.
     *
     * @param contact EmergencyContact to insert
     * @return true if added successfully, false otherwise
     * @throws SQLException if insert fails
     */
    public boolean addContact(EmergencyContact contact) throws SQLException {
        String sql = "INSERT INTO emergency_contacts (user_id, contact_name, contact_phone, relationship) "
                   + "VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, contact.getUserId());
            pstmt.setString(2, contact.getContactName().trim());
            pstmt.setString(3, contact.getContactPhone().trim());
            pstmt.setString(4, contact.getRelationship() != null ? contact.getRelationship() : "Guardian");

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        contact.setContactId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
            return false;
        }
    }

    /**
     * Deletes a contact belonging strictly to the specified user.
     *
     * @param contactId ID of contact to remove
     * @param userId ID of user requesting deletion (enforces security boundary)
     * @return true if successfully deleted
     * @throws SQLException if delete fails
     */
    public boolean deleteContact(int contactId, int userId) throws SQLException {
        String sql = "DELETE FROM emergency_contacts WHERE contact_id = ? AND user_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, contactId);
            pstmt.setInt(2, userId);

            return pstmt.executeUpdate() > 0;
        }
    }
}
