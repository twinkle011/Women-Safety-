package com.shesafe.model;

import java.sql.Timestamp;

/**
 * Model representing an SOS distress event.
 * Maps to the 'sos_alerts' table in MySQL.
 */
public class SOSAlert {

    private int alertId;
    private int userId;
    private Timestamp alertTime;
    private String location;
    private String locationType; // 'IP_ESTIMATED', 'MANUAL_ENTRY', 'NONE'
    private String mapLink;
    private String status;       // 'TRIGGERED', 'DELIVERED', 'PARTIAL', 'FAILED', 'NO_CONTACTS'
    private int contactsNotified;
    private Timestamp createdAt;

    public SOSAlert() {
    }

    public SOSAlert(int userId, String location, String locationType, String mapLink, String status) {
        this.userId = userId;
        this.location = location;
        this.locationType = locationType;
        this.mapLink = mapLink;
        this.status = status;
        this.contactsNotified = 0;
    }

    public SOSAlert(int alertId, int userId, Timestamp alertTime, String location, String locationType,
                    String mapLink, String status, int contactsNotified, Timestamp createdAt) {
        this.alertId = alertId;
        this.userId = userId;
        this.alertTime = alertTime;
        this.location = location;
        this.locationType = locationType;
        this.mapLink = mapLink;
        this.status = status;
        this.contactsNotified = contactsNotified;
        this.createdAt = createdAt;
    }

    public int getAlertId() {
        return alertId;
    }

    public void setAlertId(int alertId) {
        this.alertId = alertId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public Timestamp getAlertTime() {
        return alertTime;
    }

    public void setAlertTime(Timestamp alertTime) {
        this.alertTime = alertTime;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getLocationType() {
        return locationType;
    }

    public void setLocationType(String locationType) {
        this.locationType = locationType;
    }

    public String getMapLink() {
        return mapLink;
    }

    public void setMapLink(String mapLink) {
        this.mapLink = mapLink;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getContactsNotified() {
        return contactsNotified;
    }

    public void setContactsNotified(int contactsNotified) {
        this.contactsNotified = contactsNotified;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "SOSAlert{" +
                "alertId=" + alertId +
                ", userId=" + userId +
                ", status='" + status + '\'' +
                ", location='" + location + '\'' +
                ", contactsNotified=" + contactsNotified +
                '}';
    }
}
