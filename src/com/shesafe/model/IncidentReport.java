package com.shesafe.model;

import java.sql.Date;
import java.sql.Timestamp;

/**
 * Model representing a logged incident report.
 * Maps to the 'incident_reports' table in MySQL database.
 */
public class IncidentReport {

    private int reportId;
    private int userId;
    private String incidentType;
    private String location;
    private Date incidentDate;
    private String description;
    private Timestamp createdAt;

    public IncidentReport() {
    }

    public IncidentReport(int userId, String incidentType, String location, Date incidentDate, String description) {
        this.userId = userId;
        this.incidentType = incidentType;
        this.location = location;
        this.incidentDate = incidentDate;
        this.description = description;
    }

    public IncidentReport(int reportId, int userId, String incidentType, String location, Date incidentDate, String description, Timestamp createdAt) {
        this.reportId = reportId;
        this.userId = userId;
        this.incidentType = incidentType;
        this.location = location;
        this.incidentDate = incidentDate;
        this.description = description;
        this.createdAt = createdAt;
    }

    public int getReportId() {
        return reportId;
    }

    public void setReportId(int reportId) {
        this.reportId = reportId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getIncidentType() {
        return incidentType;
    }

    public void setIncidentType(String incidentType) {
        this.incidentType = incidentType;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Date getIncidentDate() {
        return incidentDate;
    }

    public void setIncidentDate(Date incidentDate) {
        this.incidentDate = incidentDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "IncidentReport{" +
                "reportId=" + reportId +
                ", userId=" + userId +
                ", incidentType='" + incidentType + '\'' +
                ", location='" + location + '\'' +
                ", incidentDate=" + incidentDate +
                '}';
    }
}
