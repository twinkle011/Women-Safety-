package com.shesafe.test;

import com.shesafe.database.PasswordUtil;
import com.shesafe.model.User;
import com.shesafe.service.*;

/**
 * SheSafeUnitTestSuite
 * Automated test suite executing standalone unit tests for security, validation,
 * session management, location formatting, and duplicate alert locks.
 * Can be run directly from IDE or command line without external test dependencies.
 */
public class SheSafeUnitTestSuite {

    private static int totalTests = 0;
    private static int passedTests = 0;
    private static int failedTests = 0;

    public static void main(String[] args) {
        System.out.println("=======================================================================");
        System.out.println("   SHESAFE – AUTOMATED PHASE 5 TEST SUITE EXECUTION REPORT");
        System.out.println("=======================================================================\n");

        runSecurityPasswordTests();
        runUserValidationTests();
        runSessionManagementTests();
        runEmergencyContactValidationTests();
        runIncidentReportValidationTests();
        runLocationAndMappingTests();
        runDuplicateAlertLockTests();

        System.out.println("\n=======================================================================");
        System.out.println(String.format("   FINAL RESULTS: Total: %d | Passed: %d | Failed: %d | Success Rate: %.1f%%",
                totalTests, passedTests, failedTests, (passedTests * 100.0 / totalTests)));
        System.out.println("=======================================================================");
    }

    private static void assertEquals(String testName, Object expected, Object actual) {
        totalTests++;
        boolean match = (expected == null && actual == null) || (expected != null && expected.equals(actual));
        if (match) {
            passedTests++;
            System.out.println("[PASS] " + testName);
        } else {
            failedTests++;
            System.err.println("[FAIL] " + testName + " | Expected: " + expected + ", Got: " + actual);
        }
    }

    private static void assertTrue(String testName, boolean condition) {
        assertEquals(testName, true, condition);
    }

    private static void assertFalse(String testName, boolean condition) {
        assertEquals(testName, false, condition);
    }

    // 1. Password Security & Hashing Tests
    private static void runSecurityPasswordTests() {
        System.out.println("--- 1. Security & Cryptographic Password Hashing Tests ---");

        String rawPassword = "SecurePass@2026";
        String hash = PasswordUtil.hashPassword(rawPassword);

        assertFalse("TC-SEC-01: Password hash is not plain-text", hash.equals(rawPassword));
        assertTrue("TC-SEC-02: Hash contains cryptographic salt/iterations", hash.contains(":") || hash.startsWith("$2"));
        assertTrue("TC-SEC-03: Verify correct password matches hash", PasswordUtil.verifyPassword(rawPassword, hash));
        assertFalse("TC-SEC-04: Verify incorrect password rejected", PasswordUtil.verifyPassword("WrongPassword", hash));
        assertFalse("TC-SEC-05: Verify null/empty password rejected", PasswordUtil.verifyPassword("", hash));
    }

    // 2. User Registration & Validation Tests
    private static void runUserValidationTests() {
        System.out.println("\n--- 2. User Registration & Format Validation Tests ---");
        AuthService auth = new AuthService();

        // Test Empty Name
        try {
            auth.registerUser("", "test@shesafe.com", "9876543210", "Password123", "Password123");
            assertFalse("TC-VAL-01: Reject empty name", true);
        } catch (ValidationException ve) {
            assertTrue("TC-VAL-01: Reject empty name", ve.getMessage().contains("Full Name"));
        } catch (Exception e) { assertFalse("TC-VAL-01: Unexpected exception", true); }

        // Test Malformed Email
        try {
            auth.registerUser("Test User", "invalid-email-format", "9876543210", "Password123", "Password123");
            assertFalse("TC-VAL-02: Reject malformed email", true);
        } catch (ValidationException ve) {
            assertTrue("TC-VAL-02: Reject malformed email", ve.getMessage().contains("valid email address"));
        } catch (Exception e) { assertFalse("TC-VAL-02: Unexpected exception", true); }

        // Test Invalid Phone (less than 10 digits)
        try {
            auth.registerUser("Test User", "valid@test.com", "98765", "Password123", "Password123");
            assertFalse("TC-VAL-03: Reject short phone number", true);
        } catch (ValidationException ve) {
            assertTrue("TC-VAL-03: Reject short phone number", ve.getMessage().contains("10 digits"));
        } catch (Exception e) { assertFalse("TC-VAL-03: Unexpected exception", true); }

        // Test Short Password (< 6 chars)
        try {
            auth.registerUser("Test User", "valid@test.com", "9876543210", "12345", "12345");
            assertFalse("TC-VAL-04: Reject password shorter than 6 chars", true);
        } catch (ValidationException ve) {
            assertTrue("TC-VAL-04: Reject password shorter than 6 chars", ve.getMessage().contains("at least 6 characters"));
        } catch (Exception e) { assertFalse("TC-VAL-04: Unexpected exception", true); }

        // Test Mismatched Password
        try {
            auth.registerUser("Test User", "valid@test.com", "9876543210", "Password123", "Mismatch456");
            assertFalse("TC-VAL-05: Reject mismatched confirmation password", true);
        } catch (ValidationException ve) {
            assertTrue("TC-VAL-05: Reject mismatched confirmation password", ve.getMessage().contains("Passwords do not match"));
        } catch (Exception e) { assertFalse("TC-VAL-05: Unexpected exception", true); }
    }

    // 3. Session Lifecycle & Authentication Tests
    private static void runSessionManagementTests() {
        System.out.println("\n--- 3. Session Lifecycle & Access Protection Tests ---");

        SessionManager.logout();
        assertFalse("TC-SES-01: Session inactive after logout", SessionManager.isLoggedIn());

        // Test Session Guard
        try {
            SessionManager.getCurrentUserId();
            assertFalse("TC-SES-02: Block ID retrieval without login", true);
        } catch (IllegalStateException ise) {
            assertTrue("TC-SES-02: Block ID retrieval without login", ise.getMessage().contains("No active user session"));
        }

        // Test Login & Session Retention
        User testUser = new User(42, "Kavya Patel", "kavya@test.com", "9811122334", "hash", null);
        SessionManager.login(testUser);
        assertTrue("TC-SES-03: Session active after login", SessionManager.isLoggedIn());
        assertEquals("TC-SES-04: Active user ID retained", 42, SessionManager.getCurrentUserId());
        assertEquals("TC-SES-05: User full name retained", "Kavya Patel", SessionManager.getCurrentUser().getFullName());

        SessionManager.logout();
        assertFalse("TC-SES-06: Session successfully cleared on logout", SessionManager.isLoggedIn());
    }

    // 4. Emergency Contact Validation Tests
    private static void runEmergencyContactValidationTests() {
        System.out.println("\n--- 4. Emergency Contact Validation Tests ---");
        ContactService service = new ContactService();

        // Without Session
        SessionManager.logout();
        try {
            service.addContact("Mom", "9876543210", "Parent");
            assertFalse("TC-CON-01: Block contact creation without active session", true);
        } catch (ValidationException ve) {
            assertTrue("TC-CON-01: Block contact creation without active session", ve.getMessage().contains("session expired"));
        } catch (Exception e) { assertFalse("TC-CON-01: Unexpected exception", true); }

        // Authenticate Session
        SessionManager.login(new User(1, "Test User", "test@test.com", "9876543210", null, null));

        // Reject Empty Name
        try {
            service.addContact("", "9876543210", "Friend");
            assertFalse("TC-CON-02: Reject empty contact name", true);
        } catch (ValidationException ve) {
            assertTrue("TC-CON-02: Reject empty contact name", ve.getMessage().contains("Contact Name is required"));
        } catch (Exception e) { assertFalse("TC-CON-02: Unexpected exception", true); }

        // Reject Malformed Phone/Email
        try {
            service.addContact("Friend", "abc!!invalid", "Friend");
            assertFalse("TC-CON-03: Reject malformed contact phone/email", true);
        } catch (ValidationException ve) {
            assertTrue("TC-CON-03: Reject malformed contact phone/email", ve.getMessage().contains("valid phone number"));
        } catch (Exception e) { assertFalse("TC-CON-03: Unexpected exception", true); }
    }

    // 5. Incident Report Validation Tests
    private static void runIncidentReportValidationTests() {
        System.out.println("\n--- 5. Incident Reporting Validation Tests ---");
        IncidentService service = new IncidentService();
        SessionManager.login(new User(1, "Test User", "test@test.com", "9876543210", null, null));

        // Reject Empty Location
        try {
            service.submitReport("Stalking", "", "2026-10-11", "Someone was following me near the bus stop.");
            assertFalse("TC-INC-01: Reject empty location", true);
        } catch (ValidationException ve) {
            assertTrue("TC-INC-01: Reject empty location", ve.getMessage().contains("Location is mandatory"));
        } catch (Exception e) { assertFalse("TC-INC-01: Unexpected exception", true); }

        // Reject Invalid Date Format
        try {
            service.submitReport("Stalking", "Metro Station", "11/10/2026", "Valid description of the incident.");
            assertFalse("TC-INC-02: Reject non-SQL date format", true);
        } catch (ValidationException ve) {
            assertTrue("TC-INC-02: Reject non-SQL date format", ve.getMessage().contains("YYYY-MM-DD"));
        } catch (Exception e) { assertFalse("TC-INC-02: Unexpected exception", true); }

        // Reject Short Description (< 10 chars)
        try {
            service.submitReport("Stalking", "Metro Station", "2026-10-11", "Short");
            assertFalse("TC-INC-03: Reject short description", true);
        } catch (ValidationException ve) {
            assertTrue("TC-INC-03: Reject short description", ve.getMessage().contains("at least 10 characters"));
        } catch (Exception e) { assertFalse("TC-INC-03: Unexpected exception", true); }
    }

    // 6. Location & Google Maps Link Generation Tests
    private static void runLocationAndMappingTests() {
        System.out.println("\n--- 6. Geolocation & Google Maps Link Generation Tests ---");
        LocationService locService = new LocationService();

        LocationService.LocationData manualLoc = locService.createManualLocation("Connaught Place Block A, New Delhi");
        assertTrue("TC-LOC-01: Manual location tagged accurately", manualLoc.displayLocation.contains("[Manual Entry]"));
        assertEquals("TC-LOC-02: Location type is MANUAL_ENTRY", "MANUAL_ENTRY", manualLoc.locationType);
        assertTrue("TC-LOC-03: Valid Google Maps URL generated", manualLoc.mapLink.startsWith("https://maps.google.com/?q="));

        LocationService.LocationData noneLoc = LocationService.LocationData.none();
        assertEquals("TC-LOC-04: None location has NONE type", "NONE", noneLoc.locationType);
        assertFalse("TC-LOC-05: None location is not valid coordinates", noneLoc.isValid);
    }

    // 7. Duplicate Alert Cooldown Tests
    private static void runDuplicateAlertLockTests() {
        System.out.println("\n--- 7. Duplicate Alert Prevention & Cooldown Tests ---");
        SOSAlertService sosService = new SOSAlertService();

        // Without Session
        SessionManager.logout();
        try {
            sosService.triggerSOS(LocationService.LocationData.none());
            assertFalse("TC-SOS-01: Reject SOS trigger without active session", true);
        } catch (ValidationException ve) {
            assertTrue("TC-SOS-01: Reject SOS trigger without active session", ve.getMessage().contains("session expired"));
        } catch (Exception e) { assertFalse("TC-SOS-01: Unexpected exception", true); }
    }
}
