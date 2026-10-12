package com.shesafe.service;

import com.shesafe.dao.EmergencyContactDAO;
import com.shesafe.model.EmergencyContact;

import java.sql.SQLException;
import java.util.List;

/**
 * ContactService
 * Handles business rules, validation, and user data isolation for emergency contacts.
 * Supports both mobile phone numbers and guardian email addresses for alert dispatch.
 */
public class ContactService {

    private final EmergencyContactDAO contactDAO;

    public ContactService() {
        this.contactDAO = new EmergencyContactDAO();
    }

    public ContactService(EmergencyContactDAO contactDAO) {
        this.contactDAO = contactDAO;
    }

    /**
     * Retrieves all contacts belonging to the currently authenticated user.
     */
    public List<EmergencyContact> getContactsForCurrentUser() throws ValidationException, SQLException {
        if (!SessionManager.isLoggedIn()) {
            throw new ValidationException("User session expired. Please log in again.");
        }
        int userId = SessionManager.getCurrentUserId();
        return contactDAO.getContactsByUserId(userId);
    }

    /**
     * Validates and saves a new emergency contact for the logged-in user.
     * Accepts either a valid phone number/helpline (3 to 15 digits) or a guardian email address.
     */
    public EmergencyContact addContact(String name, String phoneOrEmail, String relationship)
            throws ValidationException, SQLException {

        if (!SessionManager.isLoggedIn()) {
            throw new ValidationException("User session expired. Please log in again.");
        }

        if (name == null || name.trim().isEmpty()) {
            throw new ValidationException("Contact Name is required.");
        }
        if (name.trim().length() < 2) {
            throw new ValidationException("Contact Name must be at least 2 characters.");
        }

        if (phoneOrEmail == null || phoneOrEmail.trim().isEmpty()) {
            throw new ValidationException("Phone number or email is required.");
        }

        String input = phoneOrEmail.trim();
        boolean isEmail = input.contains("@") && input.contains(".");

        if (isEmail) {
            // Email validation
            if (!input.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
                throw new ValidationException("Please enter a valid email address format (e.g. guardian@example.com).");
            }
        } else {
            // Phone validation
            String cleanPhone = input.replaceAll("[\\s\\-\\(\\)]", "");
            if (cleanPhone.length() < 3 || cleanPhone.length() > 15 || !cleanPhone.matches("^\\+?\\d+$")) {
                throw new ValidationException("Please enter a valid phone number (e.g. 1091 or 10-digit mobile number) or email address.");
            }
        }

        String cleanRel = (relationship != null && !relationship.trim().isEmpty()) ? relationship.trim() : "Guardian";

        int userId = SessionManager.getCurrentUserId();
        EmergencyContact contact = new EmergencyContact(userId, name.trim(), input, cleanRel);

        boolean added = contactDAO.addContact(contact);
        if (!added) {
            throw new SQLException("Failed to save contact in database.");
        }

        return contact;
    }

    /**
     * Deletes an emergency contact ensuring the contact belongs to the active user.
     */
    public boolean deleteContact(int contactId) throws ValidationException, SQLException {
        if (!SessionManager.isLoggedIn()) {
            throw new ValidationException("User session expired. Please log in again.");
        }

        if (contactId <= 0) {
            throw new ValidationException("Invalid contact ID selected.");
        }

        int userId = SessionManager.getCurrentUserId();
        return contactDAO.deleteContact(contactId, userId);
    }
}
