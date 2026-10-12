package com.shesafe.dao;

import com.shesafe.database.DatabaseConnection;
import com.shesafe.model.IncidentReport;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * IncidentReportDAO
 * Data Access Object for 'incident_reports' table.
 * Handles recording safety incidents and retrieving incident history per user.
 */
public class IncidentReportDAO {

    /**
     * Inserts a new incident report for the user.
     *
     * @param report IncidentReport to insert
     * @return true if successfully inserted, false otherwise
     * @throws SQLException if insert fails
     */
    public boolean addReport(IncidentReport report) throws SQLException {
        String sql = "INSERT INTO incident_reports (user_id, incident_type, location, incident_date, description) "
                   + "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, report.getUserId());
            pstmt.setString(2, report.getIncidentType());
            pstmt.setString(3, report.getLocation().trim());
            pstmt.setDate(4, report.getIncidentDate());
            pstmt.setString(5, report.getDescription().trim());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        report.setReportId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
            return false;
        }
    }

    /**
     * Retrieves all incident reports logged by a specific user.
     *
     * @param userId The ID of the authenticated user
     * @return List of IncidentReport objects
     * @throws SQLException if query fails
     */
    public List<IncidentReport> getReportsByUserId(int userId) throws SQLException {
        List<IncidentReport> reports = new ArrayList<>();
        String sql = "SELECT report_id, user_id, incident_type, location, incident_date, description, created_at "
                   + "FROM incident_reports WHERE user_id = ? ORDER BY created_at DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    IncidentReport report = new IncidentReport(
                            rs.getInt("report_id"),
                            rs.getInt("user_id"),
                            rs.getString("incident_type"),
                            rs.getString("location"),
                            rs.getDate("incident_date"),
                            rs.getString("description"),
                            rs.getTimestamp("created_at")
                    );
                    reports.add(report);
                }
            }
        }
        return reports;
    }
}
