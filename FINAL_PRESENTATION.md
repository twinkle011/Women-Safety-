# SheSafe – Women's Safety & Emergency Support System
## Final Project Presentation Deck (Viva / Demonstration)

---

### Slide 1: Title & Team Details
```text
========================================================================================
                                     SHESAFE
                  WOMEN'S SAFETY & EMERGENCY SUPPORT SYSTEM
                      A Java Swing & MySQL Desktop Solution

                               Presented by:
                        [Student Name / Team Members]
                         Department of CSE / IT
                        [College / University Name]
========================================================================================
```

---

### Slide 2: Problem Statement & Motivation
- **Context:** Women traveling, studying, or working require rapid and dependable distress communication tools.
- **Current Challenges:**
  - Emergency tools on desktop terminals are often fragmented or complicated to trigger quickly.
  - Insecure data practices: Storing passwords and emergency numbers in plain text.
  - False technical claims: Many desktop applications falsely claim real satellite GPS tracking when PCs lack GPS receivers.
  - Lack of data privacy: Multi-user desktop machines frequently expose private emergency contacts between users.
- **The SheSafe Answer:** A unified, secure desktop application providing instant SOS triggering, trusted guardian networks, verified personal defense guidelines, and formal incident documentation.

---

### Slide 3: Project Objectives
1. **Intuitive UI:** Design a modern, responsive Java Swing interface with a calming purple/pink safety palette.
2. **Relational Database:** Persist user accounts, emergency contacts, incidents, and SOS alerts in MySQL via JDBC.
3. **Cryptographic Security:** Hash passwords using BCrypt / Salted PBKDF2 to prevent plain-text credential leaks.
4. **Emergency SOS Trigger:** Implement single-click distress initiation with user confirmation and duplicate-click protection.
5. **Location Sharing:** Provide transparent location sharing generating verified Google Maps links without background surveillance.
6. **Strict User Isolation:** Guarantee that users view and modify only their own private records.

---

### Slide 4: Technologies & Tools Used
| Category | Technology / Library | Role in Project |
| :--- | :--- | :--- |
| **Language** | Java (JDK 8 / 11 / 17 / 21) | Core application programming language |
| **GUI Framework** | Java Swing & AWT | Front-end graphical user interface |
| **Database** | MySQL Server (InnoDB) | Relational database storage |
| **Connectivity** | JDBC (`mysql-connector-j`) | Parameterized database queries (`PreparedStatement`) |
| **Security** | BCrypt / PBKDF2WithHmacSHA256 | Cryptographic password hashing with 128-bit salt |
| **Notifications** | JavaMail API (SMTP) | Automated emergency email notifications |
| **Mapping** | Google Maps URL Schema | Clickable coordinate links for guardians |
| **Concurrency** | `SwingWorker` | Asynchronous non-blocking UI background threads |

---

### Slide 5: Multi-Tier System Architecture
```text
[ Presentation Layer ]  ➔  [ Service Layer ]       ➔  [ DAO Layer ]     ➔  [ Database Layer ]
Java Swing Screens          Business Rules & Logic      PreparedStatements     MySQL shesafe_db
- WelcomeScreen             - AuthService               - UserDAO              - users
- RegistrationScreen        - SessionManager            - EmergencyContactDAO  - emergency_contacts
- LoginScreen               - ContactService            - IncidentReportDAO    - incident_reports
- DashboardScreen           - IncidentService           - SOSAlertDAO          - sos_alerts
- EmergencyContactsScreen   - LocationService                                  - sos_notifications
- SafetyTipsScreen          - NotificationService
- IncidentReportingScreen   - SOSAlertService
- SOSHistoryScreen
```

---

### Slide 6: Database Design & Relationships
- **`users`:** Primary user table storing encrypted password hashes and contact info.
- **`emergency_contacts`:** Guardian directory linked via `user_id` (`ON DELETE CASCADE`).
- **`incident_reports`:** Formal safety reports linked via `user_id` (`ON DELETE CASCADE`).
- **`sos_alerts`:** Distress event log storing timestamp, location, and status (`ON DELETE CASCADE`).
- **`sos_notifications`:** Recipient delivery log recording delivery status for each guardian.

---

### Slide 7: Main Features & Capabilities
1. **User Authentication:** Regex field validation, duplicate email prevention, BCrypt password hashing.
2. **Emergency Contacts Circle:** Add, view, refresh, and delete guardians; supports both phone numbers and emails.
3. **Standalone Safety Guidelines:** Practical travel, transit, and situational vigilance guides functioning in-memory without database dependence.
4. **Incident Reporting:** Formally log harassment or safety hazards with landmarks, date checks, and history review.
5. **Emergency Distress (SOS):** Prominent red SOS button with confirmation dialog, location authorization, and delivery breakdown.
6. **SOS Alert History:** Inspect past distress events and per-recipient delivery statuses in a `JTable`.

---

### Slide 8: Security & Reliability Engineering
- **Zero Plain-Text Passwords:** Passwords encrypted using BCrypt / PBKDF2 with random salt.
- **100% Parameterized Queries:** All SQL queries use `PreparedStatement` to eliminate SQL injection vulnerabilities.
- **Session Lifecycle Guard (`SessionManager`):** Protected screens reject unauthenticated access and redirect to Login.
- **Duplicate Alert Lock (Debounce):** 5-second cooldown lock prevents duplicate alerts caused by rapid double-clicking.
- **Responsive Threading:** Network and database operations during SOS dispatch execute in `SwingWorker` background threads, preventing UI freezing.
- **Confidential Credentials:** Database and mail server credentials externalized to `db.properties` rather than hardcoded in source files.

---

### Slide 9: Desktop Geolocation & Notification Realities
- **Desktop Geolocation:** Standard PCs lack satellite GPS chips. SheSafe transparently provides automated IP Geolocation estimation or manual landmark entry with clear labeling (`[IP_ESTIMATED]` vs `[MANUAL_ENTRY]`).
- **Cellular SMS Notifications:** Ordinary desktop Java cannot send cellular SMS without a commercial SMS Gateway API (Twilio / Fast2SMS) requiring API keys and carrier fees. SheSafe integrates JavaMail SMTP for direct email delivery and documents SMS gateway requirements.
- **Honest Feedback:** The system never falsely claims an alert was delivered unless confirmed by the dispatch service.

---

### 10. Testing & Test Case Summary
- **Test Suite Executed:** 20 test cases covering functional correctness, edge-case rejection, and fault tolerance.
- **Automated Test Suite:** `SheSafeUnitTestSuite.java` provides executable automated unit tests for hashing, regex, session management, and duplicate locks.
- **Results:**
  - Registration & Validation: **100% PASS**
  - Authentication & Session Guards: **100% PASS**
  - Contact Management & User Isolation: **100% PASS**
  - Incident Logging & History: **100% PASS**
  - SOS Dispatch & Duplicate Prevention: **100% PASS**
  - Database Fault Tolerance: **100% PASS**

---

### Slide 11: Limitations & Future Enhancements
- **Current Limitations:**
  - Desktop PCs lack hardware GPS chips.
  - Direct cellular SMS requires a paid third-party SMS Gateway account.
- **Future Enhancements:**
  - **Mobile Companion Application:** Android/iOS app utilizing native satellite GPS and direct mobile telephony.
  - **IoT Wearable Button:** Physical panic button interfacing via Bluetooth Low Energy (BLE) or USB.
  - **Audio Recording:** Automated background microphone capture upon distress initiation.
  - **Community Safety Maps:** Aggregated anonymous heatmaps highlighting reported hazard zones.

---

### Slide 12: Conclusion & Demonstration
- **Summary:** SheSafe successfully integrates Java Swing, MySQL, and JDBC into an aesthetically appealing, secure, and production-ready safety system.
- **Impact:** Demonstrates how clean software engineering principles (multi-tier design, encryption, thread safety, and user isolation) solve real-world emergency support challenges.
- **Live Demonstration:**
  1. User Registration & BCrypt password inspection in MySQL.
  2. Login and Session Initialization.
  3. Managing Emergency Contacts.
  4. Logging an Incident Report.
  5. Triggering the Emergency SOS button, authorizing location, and inspecting recipient delivery logs.

```text
========================================================================================
                                THANK YOU!
                          QUESTIONS & DISCUSSION
========================================================================================
```
