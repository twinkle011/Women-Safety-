package com.shesafe.model;

/**
 * Model representing an emergency contact person.
 * Maps to the 'emergency_contacts' table in MySQL database.
 */
public class EmergencyContact {

    private int contactId;
    private int userId;
    private String contactName;
    private String contactPhone;
    private String relationship;

    public EmergencyContact() {
    }

    public EmergencyContact(int userId, String contactName, String contactPhone, String relationship) {
        this.userId = userId;
        this.contactName = contactName;
        this.contactPhone = contactPhone;
        this.relationship = relationship != null ? relationship : "Guardian";
    }

    public EmergencyContact(int contactId, int userId, String contactName, String contactPhone, String relationship) {
        this.contactId = contactId;
        this.userId = userId;
        this.contactName = contactName;
        this.contactPhone = contactPhone;
        this.relationship = relationship != null ? relationship : "Guardian";
    }

    public int getContactId() {
        return contactId;
    }

    public void setContactId(int contactId) {
        this.contactId = contactId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public String getRelationship() {
        return relationship;
    }

    public void setRelationship(String relationship) {
        this.relationship = relationship;
    }

    @Override
    public String toString() {
        return "EmergencyContact{" +
                "contactId=" + contactId +
                ", userId=" + userId +
                ", contactName='" + contactName + '\'' +
                ", contactPhone='" + contactPhone + '\'' +
                ", relationship='" + relationship + '\'' +
                '}';
    }
}
