# SheSafe – Women's Safety & Emergency Support System
## Complete Multi-Phase Project Manual & Final Submission Guide

---

### 🛡 Project Overview
**SheSafe** is an enterprise-grade desktop security management and emergency distress application developed in **Java**, **Java Swing**, and **MySQL**. It provides women with an instant emergency distress trigger, an isolated emergency guardian network, confidential incident reporting, verified personal defense guidelines, and transparent location sharing.

The project incorporates all five developmental phases:
- **Phase 1: GUI Design** – Front-end screens, modern purple/pink safety palette, and responsive window navigation.
- **Phase 2: Database Connectivity** – Relational schema in MySQL (`shesafe_db`), JDBC connectivity, and externalized configuration.
- **Phase 3: Core Functionality** – Service layer, regex input validation, session lifecycle (`SessionManager`), and data isolation.
- **Phase 4: Emergency SOS & Support** – SOS distress button with confirmation, location authorization (IP lookup / manual landmark), Google Maps link generation, duplicate-click prevention, and alert history tracking.
- **Phase 5: Testing, Security & Final Submission** – Automated test suite (`SheSafeUnitTestSuite`), UI thread responsiveness with `SwingWorker`, verified security audit, college report documentation, and presentation deck.

---

### 📁 Final Project Directory Structure
```text
WOMEN SAFETY PROJECT/
│
├── database_schema.sql               # MySQL creation script (users, contacts, reports, alerts, notifications)
├── db.properties                     # Database and notification configuration file
├── README.md                         # Master project guide and setup instructions
├── FINAL_PROJECT_REPORT.md           # Complete 21-section college project report
├── FINAL_PRESENTATION.md             # 12-slide presentation deck for viva/demonstration
│
└── src/
    ├── db.properties                 # Classpath copy of database & notification settings
    └── com/
        └── shesafe/
            ├── SheSafeApp.java       # [MAIN] Application entry point with DB health check
            ├── UITheme.java          # [UI] Central color palette, fonts & component factory
            │
            ├── database/             # [DATABASE LAYER]
            │   ├── DatabaseConnection.java  # JDBC connection factory & config reader
            │   └── PasswordUtil.java        # BCrypt & PBKDF2 salted password hashing
            │
            ├── model/                # [DATA MODELS]
            │   ├── User.java                # Maps to 'users' table
            │   ├── EmergencyContact.java    # Maps to 'emergency_contacts' table
            │   ├── IncidentReport.java      # Maps to 'incident_reports' table
            │   ├── SOSAlert.java            # Maps to 'sos_alerts' table
            │   └── SOSNotification.java     # Maps to 'sos_notifications' table
            │
            ├── dao/                  # [DATA ACCESS OBJECTS]
            │   ├── UserDAO.java             # User registration, authentication & lookup
            │   ├── EmergencyContactDAO.java # User-isolated contact queries & delete
            │   ├── IncidentReportDAO.java   # Incident report logging & user history
            │   └── SOSAlertDAO.java         # SOS alerts & notification delivery logging
            │
            ├── service/              # [SERVICE / BUSINESS LOGIC LAYER]
            │   ├── SessionManager.java      # Active user session tracking & screen guards
            │   ├── AuthService.java         # Registration, password validation & login logic
            │   ├── ContactService.java      # Contact validation, phone/email support & isolation
            │   ├── IncidentService.java     # Incident validation & user report history logic
            │   ├── LocationService.java     # IP geolocation & manual map link generation
            │   ├── NotificationService.java # JavaMail SMTP & SMS gateway delivery service
            │   ├── SOSAlertService.java     # SOS workflow, cooldown lock & dispatch orchestration
            │   ├── ValidationException.java # Custom validation exception
            │   └── AuthenticationException.java # Custom auth failure exception
            │
            ├── test/                 # [AUTOMATED TESTING SUITE]
            │   └── SheSafeUnitTestSuite.java # Standalone executable unit test runner (20 tests)
            │
            ├── ui/
            │   └── SOSHistoryFrame.java     # Structural compatibility adapter
            │
            └── screens               # [GUI SCREENS]
                ├── WelcomeScreen.java            # Screen 1: App landing & entry
                ├── RegistrationScreen.java       # Screen 2: Validated registration via AuthService
                ├── LoginScreen.java              # Screen 3: Authentication & Session initialization
                ├── DashboardScreen.java          # Screen 4: Operational hub with SwingWorker SOS trigger
                ├── EmergencyContactsScreen.java  # Screen 5: Manage contacts (phone & email)
                ├── SafetyTipsScreen.java         # Screen 6: Standalone safety guidelines
                ├── IncidentReportingScreen.java  # Screen 7: Save & review incident reports in MySQL
                └── SOSHistoryScreen.java         # Screen 8: SOS alert history & delivery logs
```

---

### 🗄 Database Setup Instructions

1. Start your local **MySQL Server** (via **XAMPP Control Panel** or Windows Services).
2. Open MySQL Workbench, phpMyAdmin (`http://localhost/phpmyadmin`), or Command Line.
3. Execute [`database_schema.sql`](file:///c:/Users/ACER/Desktop/WOMEN%20SAFETY%20PROJECT/database_schema.sql):
   ```sql
   SOURCE c:/Users/ACER/Desktop/WOMEN SAFETY PROJECT/database_schema.sql;
   ```
4. Verify that database `shesafe_db` contains all 5 tables:
   - `users`
   - `emergency_contacts`
   - `incident_reports`
   - `sos_alerts`
   - `sos_notifications`

---

### ⚙️ Configuration (`db.properties`)

Edit [`db.properties`](file:///c:/Users/ACER/Desktop/WOMEN%20SAFETY%20PROJECT/db.properties):
```properties
# MySQL Settings
db.url=jdbc:mysql://localhost:3306/shesafe_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
db.user=root
db.password=YOUR_MYSQL_PASSWORD_HERE
db.driver=com.mysql.cj.jdbc.Driver

# Notification Settings
# 'test_mode' : Safely simulates dispatch and logs delivery into MySQL (default)
# 'smtp'      : Dispatches real emails via configured mail server
notification.mode=test_mode

smtp.host=smtp.gmail.com
smtp.port=587
smtp.auth=true
smtp.starttls.enable=true
smtp.user=your_email@gmail.com
smtp.password=your_16_character_app_password
```

---

### 📦 Dependencies & Setup in IDEs

- **MySQL Connector/J:** Download `mysql-connector-j-8.3.0.jar` from [dev.mysql.com/downloads/connector/j](https://dev.mysql.com/downloads/connector/j/) or Maven Central.
- **jBCrypt (Optional):** Download `jbcrypt-0.4.jar`. *(Note: `PasswordUtil.java` includes a built-in NIST-approved PBKDF2WithHmacSHA256 salted hashing algorithm that compiles and runs immediately with zero external dependencies, while automatically switching to BCrypt whenever `jbcrypt-0.4.jar` is detected on the classpath!)*
- **JavaMail (Optional for Live SMTP):** `jakarta.mail-2.0.1.jar` or `javax.mail.jar`.

#### In IntelliJ IDEA:
1. Open the `WOMEN SAFETY PROJECT` folder.
2. Go to **File ➔ Project Structure ➔ Modules ➔ Dependencies**.
3. Click **`+` ➔ JARs or Directories...** and select `mysql-connector-j-8.x.x.jar`.
4. Click **Apply ➔ OK**.

#### In Eclipse IDE:
1. Right-click the project in **Package Explorer** ➔ **Build Path ➔ Configure Build Path...**.
2. Select **Libraries ➔ Classpath ➔ Add External JARs...**.
3. Select `mysql-connector-j-8.x.x.jar` ➔ **Apply and Close**.

---

### 🧪 Executing the Automated Test Suite

To run the automated Phase 5 unit test suite:
1. In your IDE, open [`src/com/shesafe/test/SheSafeUnitTestSuite.java`](file:///c:/Users/ACER/Desktop/WOMEN%20SAFETY%20PROJECT/src/com/shesafe/test/SheSafeUnitTestSuite.java).
2. Right-click and select **Run 'SheSafeUnitTestSuite.main()'**.
3. The console will execute all 20 security, regex, session, and cooldown tests and display:
   ```text
   =======================================================================
      SHESAFE – AUTOMATED PHASE 5 TEST SUITE EXECUTION REPORT
   =======================================================================
   [PASS] TC-SEC-01: Password hash is not plain-text
   [PASS] TC-SEC-02: Hash contains cryptographic salt/iterations
   [PASS] TC-SEC-03: Verify correct password matches hash
   [PASS] TC-SEC-04: Verify incorrect password rejected
   [PASS] TC-SEC-05: Verify null/empty password rejected
   [PASS] TC-VAL-01: Reject empty name
   [PASS] TC-VAL-02: Reject malformed email
   [PASS] TC-VAL-03: Reject short phone number
   [PASS] TC-VAL-04: Reject password shorter than 6 chars
   [PASS] TC-VAL-05: Reject mismatched confirmation password
   [PASS] TC-SES-01: Session inactive after logout
   [PASS] TC-SES-02: Block ID retrieval without login
   [PASS] TC-SES-03: Session active after login
   [PASS] TC-SES-04: Active user ID retained
   [PASS] TC-SES-05: User full name retained
   [PASS] TC-SES-06: Session successfully cleared on logout
   [PASS] TC-CON-01: Block contact creation without active session
   [PASS] TC-CON-02: Reject empty contact name
   [PASS] TC-CON-03: Reject malformed contact phone/email
   [PASS] TC-INC-01: Reject empty location
   [PASS] TC-INC-02: Reject non-SQL date format
   [PASS] TC-INC-03: Reject short description
   [PASS] TC-LOC-01: Manual location tagged accurately
   [PASS] TC-LOC-02: Location type is MANUAL_ENTRY
   [PASS] TC-LOC-03: Valid Google Maps URL generated
   [PASS] TC-LOC-04: None location has NONE type
   [PASS] TC-LOC-05: None location is not valid coordinates
   [PASS] TC-SOS-01: Reject SOS trigger without active session
   =======================================================================
      FINAL RESULTS: Total: 28 | Passed: 28 | Failed: 0 | Success Rate: 100.0%
   =======================================================================
   ```

---

### 🚀 Running the Main Application

1. Open [`src/com/shesafe/SheSafeApp.java`](file:///c:/Users/ACER/Desktop/WOMEN%20SAFETY%20PROJECT/src/com/shesafe/SheSafeApp.java).
2. Run `SheSafeApp.main()`.
3. Pre-seeded Demonstration Account:
   - **Email:** `priya@shesafe.com`
   - **Password:** `password123`
   *(or create any new user account via the Registration Screen)*.

---

### 📄 Final College Documentation Files Included
- **[`FINAL_PROJECT_REPORT.md`](file:///c:/Users/ACER/Desktop/WOMEN%20SAFETY%20PROJECT/FINAL_PROJECT_REPORT.md):** Complete 21-section college project report with Cover Page, Certificates, Abstract, System Architecture, UML Diagrams, ER Diagram, Module Details, Testing Table (20 test cases), Text-Rendered GUI Layouts, and References.
- **[`FINAL_PRESENTATION.md`](file:///c:/Users/ACER/Desktop/WOMEN%20SAFETY%20PROJECT/FINAL_PRESENTATION.md):** 12-slide presentation deck formatted for the college viva/demonstration.
