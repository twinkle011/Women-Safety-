package com.shesafe.service;

import com.shesafe.dao.IncidentReportDAO;
import com.shesafe.model.IncidentReport;

import java.sql.Date;
import java.sql.SQLException;
import java.util.List;

/**
 * IncidentService
 * Handles business validation and data persistence for safety incidents.
 * Enforces user isolation so reports are always bound to the authenticated user.
 */
public class IncidentService {

    private final IncidentReportDAO reportDAO;

    public IncidentService() {
        this.reportDAO = new IncidentReportDAO();
    }

    public IncidentService(IncidentReportDAO reportDAO) {
        this.reportDAO = reportDAO;
    }

    /**
     * Validates and submits a new incident report for the authenticated user.
     */
    public IncidentReport submitReport(String incidentType, String location, String dateStr, String description)
            throws ValidationException, SQLException {

        if (!SessionManager.isLoggedIn()) {
            throw new ValidationException("User session expired. Please log in again.");
        }

        if (incidentType == null || incidentType.trim().isEmpty()) {
            throw new ValidationException("Please select an Incident Category.");
        }

        if (location == null || location.trim().isEmpty()) {
            throw new ValidationException("Incident Location is mandatory.");
        }
        if (location.trim().length() < 3) {
            throw new ValidationException("Location must be at least 3 characters (e.g. landmark or street).");
        }

        if (dateStr == null || dateStr.trim().isEmpty()) {
            throw new ValidationException("Incident Date is mandatory.");
        }

        Date sqlDate;
        try {
            sqlDate = Date.valueOf(dateStr.trim());
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Invalid Date format. Please use YYYY-MM-DD (e.g. 2026-10-11).");
        }

        // Verify date is not in future
        long currentTime = System.currentTimeMillis();
        if (sqlDate.getTime() > currentTime + 86400000L) { // Allow today + timezone buffer
            throw new ValidationException("Incident date cannot be in the future.");
        }

        if (description == null || description.trim().isEmpty()) {
            throw new ValidationException("Detailed Description is mandatory.");
        }
        if (description.trim().length() < 10) {
            throw new ValidationException("Please provide a descriptive account (at least 10 characters).");
        }

        int userId = SessionManager.getCurrentUserId();
        IncidentReport report = new IncidentReport(userId, incidentType.trim(), location.trim(), sqlDate, description.trim());

        boolean added = reportDAO.addReport(report);
        if (!added) {
            throw new SQLException("Failed to save incident report in database.");
        }

        return report;
    }

    /**
     * Retrieves all incident reports logged by the current user.
     */
    public List<IncidentReport> getReportsForCurrentUser() throws ValidationException, SQLException {
        if (!SessionManager.isLoggedIn()) {
            throw new ValidationException("User session expired. Please log in again.");
        }
        int userId = SessionManager.getCurrentUserId();
        return reportDAO.getReportsByUserId(userId);
    }
}
