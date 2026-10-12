package com.shesafe.dao;

import com.shesafe.database.DatabaseConnection;
import com.shesafe.model.SOSAlert;
import com.shesafe.model.SOSNotification;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * SOSAlertDAO
 * Handles database operations for 'sos_alerts' and 'sos_notifications'.
 * Enforces user isolation so each user only sees their own emergency alert history.
 */
public class SOSAlertDAO {

    /**
     * Inserts an SOS distress alert event into MySQL.
     *
     * @param alert The SOSAlert to persist
     * @return Generated alert_id
     * @throws SQLException if a database error occurs
     */
    public int createAlert(SOSAlert alert) throws SQLException {
        String sql = "INSERT INTO sos_alerts (user_id, location, location_type, map_link, status, contacts_notified) "
                   + "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, alert.getUserId());
            pstmt.setString(2, alert.getLocation());
            pstmt.setString(3, alert.getLocationType() != null ? alert.getLocationType() : "NONE");
            pstmt.setString(4, alert.getMapLink());
            pstmt.setString(5, alert.getStatus() != null ? alert.getStatus() : "TRIGGERED");
            pstmt.setInt(6, alert.getContactsNotified());

            pstmt.executeUpdate();

            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    int generatedId = rs.getInt(1);
                    alert.setAlertId(generatedId);
                    return generatedId;
                }
            }
        }
        return -1;
    }

    /**
     * Updates status and total contact count for an SOS alert after dispatch.
     */
    public void updateAlertStatus(int alertId, String status, int contactsNotified) throws SQLException {
        String sql = "UPDATE sos_alerts SET status = ?, contacts_notified = ? WHERE alert_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, status);
            pstmt.setInt(2, contactsNotified);
            pstmt.setInt(3, alertId);

            pstmt.executeUpdate();
        }
    }

    /**
     * Logs an individual notification attempt for a recipient.
     */
    public void logNotification(SOSNotification notif) throws SQLException {
        String sql = "INSERT INTO sos_notifications (alert_id, contact_id, recipient_name, recipient_target, channel, delivery_status, status_details) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, notif.getAlertId());
            if (notif.getContactId() != null) {
                pstmt.setInt(2, notif.getContactId());
            } else {
                pstmt.setNull(2, Types.INTEGER);
            }
            pstmt.setString(3, notif.getRecipientName());
            pstmt.setString(4, notif.getRecipientTarget());
            pstmt.setString(5, notif.getChannel());
            pstmt.setString(6, notif.getDeliveryStatus());
            pstmt.setString(7, notif.getStatusDetails());

            pstmt.executeUpdate();

            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    notif.setNotificationId(rs.getInt(1));
                }
            }
        }
    }

    /**
     * Retrieves all SOS alerts logged for the specific user (User Isolation).
     */
    public List<SOSAlert> getAlertsByUserId(int userId) throws SQLException {
        List<SOSAlert> list = new ArrayList<>();
        String sql = "SELECT alert_id, user_id, alert_time, location, location_type, map_link, status, contacts_notified, created_at "
                   + "FROM sos_alerts WHERE user_id = ? ORDER BY alert_time DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    SOSAlert alert = new SOSAlert(
                            rs.getInt("alert_id"),
                            rs.getInt("user_id"),
                            rs.getTimestamp("alert_time"),
                            rs.getString("location"),
                            rs.getString("location_type"),
                            rs.getString("map_link"),
                            rs.getString("status"),
                            rs.getInt("contacts_notified"),
                            rs.getTimestamp("created_at")
                    );
                    list.add(alert);
                }
            }
        }
        return list;
    }

    /**
     * Retrieves the individual delivery breakdown for a specific alert.
     */
    public List<SOSNotification> getNotificationsForAlert(int alertId) throws SQLException {
        List<SOSNotification> list = new ArrayList<>();
        String sql = "SELECT notification_id, alert_id, contact_id, recipient_name, recipient_target, channel, delivery_status, status_details, sent_at "
                   + "FROM sos_notifications WHERE alert_id = ? ORDER BY notification_id ASC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, alertId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Integer contactId = rs.getObject("contact_id") != null ? rs.getInt("contact_id") : null;
                    SOSNotification notif = new SOSNotification(
                            rs.getInt("notification_id"),
                            rs.getInt("alert_id"),
                            contactId,
                            rs.getString("recipient_name"),
                            rs.getString("recipient_target"),
                            rs.getString("channel"),
                            rs.getString("delivery_status"),
                            rs.getString("status_details"),
                            rs.getTimestamp("sent_at")
                    );
                    list.add(notif);
                }
            }
        }
        return list;
    }
}
