package com.shesafe.service;

import com.shesafe.dao.EmergencyContactDAO;
import com.shesafe.dao.SOSAlertDAO;
import com.shesafe.model.EmergencyContact;
import com.shesafe.model.SOSAlert;
import com.shesafe.model.SOSNotification;
import com.shesafe.model.User;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * SOSAlertService
 * Orchestrates the end-to-end SOS distress workflow:
 * 1. Validates active session and enforces duplicate alert prevention.
 * 2. Persists the alert in 'sos_alerts' table.
 * 3. Fetches user's emergency contacts from MySQL.
 * 4. Dispatches notifications via NotificationService.
 * 5. Records per-recipient delivery statuses in 'sos_notifications'.
 * 6. Calculates and updates overall alert status in database.
 */
public class SOSAlertService {

    private final SOSAlertDAO alertDAO;
    private final EmergencyContactDAO contactDAO;
    private final NotificationService notificationService;

    // Cooldown lock to prevent accidental duplicate triggers (5 seconds)
    private static volatile long lastTriggerTimestamp = 0;
    private static final long COOLDOWN_MILLIS = 5000;

    public static class SOSDispatchResult {
        public final int alertId;
        public final String overallStatus;
        public final int totalContacts;
        public final int successfulDispatches;
        public final List<NotificationService.NotificationResult> deliveryLogs;
        public final String locationUsed;
        public final String mapLink;

        public SOSDispatchResult(int alertId, String overallStatus, int totalContacts,
                                 int successfulDispatches, List<NotificationService.NotificationResult> logs,
                                 String locationUsed, String mapLink) {
            this.alertId = alertId;
            this.overallStatus = overallStatus;
            this.totalContacts = totalContacts;
            this.successfulDispatches = successfulDispatches;
            this.deliveryLogs = logs;
            this.locationUsed = locationUsed;
            this.mapLink = mapLink;
        }
    }

    public SOSAlertService() {
        this.alertDAO = new SOSAlertDAO();
        this.contactDAO = new EmergencyContactDAO();
        this.notificationService = new NotificationService();
    }

    /**
     * Initiates an emergency SOS dispatch event for the currently logged in user.
     *
     * @param locationData Location information authorized by the user
     * @return Complete dispatch breakdown
     * @throws ValidationException on cooldown violation or unauthenticated session
     * @throws SQLException on database persistence failure
     */
    public synchronized SOSDispatchResult triggerSOS(LocationService.LocationData locationData)
            throws ValidationException, SQLException {

        if (!SessionManager.isLoggedIn()) {
            throw new ValidationException("User session expired. Please log in before initiating SOS alerts.");
        }

        // Duplicate alert prevention (Debounce / Cooldown)
        long now = System.currentTimeMillis();
        if (now - lastTriggerTimestamp < COOLDOWN_MILLIS) {
            long remaining = (COOLDOWN_MILLIS - (now - lastTriggerTimestamp)) / 1000;
            throw new ValidationException("Alert in progress. Please wait " + (remaining + 1) + " seconds to prevent duplicate dispatches.");
        }
        lastTriggerTimestamp = now;

        User currentUser = SessionManager.getCurrentUser();
        int userId = currentUser.getUserId();

        String displayLocation = locationData != null ? locationData.displayLocation : null;
        String locationType = locationData != null ? locationData.locationType : "NONE";
        String mapLink = locationData != null ? locationData.mapLink : null;

        // 1. Create initial SOS Alert record in MySQL
        SOSAlert alert = new SOSAlert(userId, displayLocation, locationType, mapLink, "TRIGGERED");
        int alertId = alertDAO.createAlert(alert);

        // 2. Fetch emergency contacts strictly for this user
        List<EmergencyContact> contacts = contactDAO.getContactsByUserId(userId);
        List<NotificationService.NotificationResult> logs = new ArrayList<>();

        int successfulCount = 0;

        if (contacts.isEmpty()) {
            alertDAO.updateAlertStatus(alertId, "NO_CONTACTS", 0);
            return new SOSDispatchResult(alertId, "NO_CONTACTS", 0, 0, logs, displayLocation, mapLink);
        }

        // 3. Dispatch notification to each contact
        for (EmergencyContact contact : contacts) {
            NotificationService.NotificationResult result = notificationService.notifyContact(
                    currentUser, contact, displayLocation, mapLink
            );
            logs.add(result);

            if (result.isSuccess) {
                successfulCount++;
            }

            // 4. Save per-contact notification log in 'sos_notifications'
            SOSNotification notif = new SOSNotification(
                    alertId,
                    contact.getContactId(),
                    contact.getContactName(),
                    result.target,
                    result.channel,
                    result.deliveryStatus,
                    result.details
            );
            alertDAO.logNotification(notif);
        }

        // 5. Determine overall alert status
        String finalStatus;
        if (successfulCount == contacts.size()) {
            finalStatus = "DELIVERED";
        } else if (successfulCount > 0) {
            finalStatus = "PARTIAL";
        } else {
            finalStatus = "FAILED";
        }

        // 6. Update database record with final result
        alertDAO.updateAlertStatus(alertId, finalStatus, successfulCount);

        return new SOSDispatchResult(alertId, finalStatus, contacts.size(), successfulCount, logs, displayLocation, mapLink);
    }

    /**
     * Retrieves previous SOS events strictly for the active user.
     */
    public List<SOSAlert> getAlertHistoryForCurrentUser() throws ValidationException, SQLException {
        if (!SessionManager.isLoggedIn()) {
            throw new ValidationException("User session expired. Please log in again.");
        }
        int userId = SessionManager.getCurrentUserId();
        return alertDAO.getAlertsByUserId(userId);
    }

    /**
     * Retrieves the individual delivery breakdown for a specific alert.
     */
    public List<SOSNotification> getNotificationBreakdown(int alertId) throws SQLException {
        return alertDAO.getNotificationsForAlert(alertId);
    }
}
