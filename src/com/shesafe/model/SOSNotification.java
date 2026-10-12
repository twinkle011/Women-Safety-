package com.shesafe.model;

import java.sql.Timestamp;

/**
 * Model representing an individual notification delivery attempt to a contact.
 * Maps to the 'sos_notifications' table in MySQL.
 */
public class SOSNotification {

    private int notificationId;
    private int alertId;
    private Integer contactId; // Nullable
    private String recipientName;
    private String recipientTarget; // Email address or phone number
    private String channel;         // 'EMAIL', 'SMS_EXTERNAL', 'SYSTEM_LOG'
    private String deliveryStatus;  // 'DELIVERED', 'FAILED', 'TEST_MODE'
    private String statusDetails;
    private Timestamp sentAt;

    public SOSNotification() {
    }

    public SOSNotification(int alertId, Integer contactId, String recipientName, String recipientTarget,
                           String channel, String deliveryStatus, String statusDetails) {
        this.alertId = alertId;
        this.contactId = contactId;
        this.recipientName = recipientName;
        this.recipientTarget = recipientTarget;
        this.channel = channel;
        this.deliveryStatus = deliveryStatus;
        this.statusDetails = statusDetails;
    }

    public SOSNotification(int notificationId, int alertId, Integer contactId, String recipientName,
                           String recipientTarget, String channel, String deliveryStatus,
                           String statusDetails, Timestamp sentAt) {
        this.notificationId = notificationId;
        this.alertId = alertId;
        this.contactId = contactId;
        this.recipientName = recipientName;
        this.recipientTarget = recipientTarget;
        this.channel = channel;
        this.deliveryStatus = deliveryStatus;
        this.statusDetails = statusDetails;
        this.sentAt = sentAt;
    }

    public int getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(int notificationId) {
        this.notificationId = notificationId;
    }

    public int getAlertId() {
        return alertId;
    }

    public void setAlertId(int alertId) {
        this.alertId = alertId;
    }

    public Integer getContactId() {
        return contactId;
    }

    public void setContactId(Integer contactId) {
        this.contactId = contactId;
    }

    public String getRecipientName() {
        return recipientName;
    }

    public void setRecipientName(String recipientName) {
        this.recipientName = recipientName;
    }

    public String getRecipientTarget() {
        return recipientTarget;
    }

    public void setRecipientTarget(String recipientTarget) {
        this.recipientTarget = recipientTarget;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getDeliveryStatus() {
        return deliveryStatus;
    }

    public void setDeliveryStatus(String deliveryStatus) {
        this.deliveryStatus = deliveryStatus;
    }

    public String getStatusDetails() {
        return statusDetails;
    }

    public void setStatusDetails(String statusDetails) {
        this.statusDetails = statusDetails;
    }

    public Timestamp getSentAt() {
        return sentAt;
    }

    public void setSentAt(Timestamp sentAt) {
        this.sentAt = sentAt;
    }

    @Override
    public String toString() {
        return "SOSNotification{" +
                "notificationId=" + notificationId +
                ", recipientName='" + recipientName + '\'' +
                ", deliveryStatus='" + deliveryStatus + '\'' +
                ", channel='" + channel + '\'' +
                '}';
    }
}
