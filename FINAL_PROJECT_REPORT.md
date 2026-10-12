# SheSafe – Women's Safety & Emergency Support System
## Comprehensive Project Report & Final Documentation

---

### 1. Cover Page

```text
========================================================================================
                                     PROJECT REPORT
                                           ON
                                       SHESAFE
                  WOMEN'S SAFETY & EMERGENCY SUPPORT SYSTEM

                          Submitted in partial fulfillment 
                   for the requirement of the Bachelor of Technology / 
                  Bachelor of Computer Applications / Master of Science
                                 in Computer Science & Engineering

                                     Submitted By:
                             Student Name: [Your Name]
                             Roll Number:  [Your Roll No]
                             Department:   Computer Science & Engineering

                                    Under the Guidance of:
                             Project Guide: [Guide's Name, Designation]
                             Department of Computer Science & Engineering

                                [College / University Name]
                                    Academic Year: 2025–2026
========================================================================================
```

---

### 2. Certificate & Declaration

#### Certificate of Approval
> This is to certify that the project entitled **“SheSafe – Women's Safety & Emergency Support System”** submitted by **[Your Name]** (Roll No: **[Your Roll No]**) in partial fulfillment of the requirements for the degree of Bachelor of Technology in Computer Science and Engineering is a bonafide record of work carried out by them under my supervision and guidance.
>
> **Project Guide:** _________________________  
> **Head of Department:** _________________________  
> **Date:** [Date]  
> **Seal of Institution:**  

#### Student Declaration
> I hereby declare that the project entitled **“SheSafe – Women's Safety & Emergency Support System”** submitted to **[College Name]**, is a record of an original work done by me under the guidance of **[Guide Name]**. This project work has not been submitted elsewhere for any degree or diploma.
>
> **Signature of Student:** _________________________  
> **Date:** [Date]  

---

### 3. Acknowledgement
> I would like to express my sincere gratitude to my project guide, **[Guide's Name]**, for their continuous encouragement, valuable suggestions, and patient guidance throughout the design, implementation, and testing of **SheSafe**.
>
> I also extend my heartfelt thanks to our Head of Department and faculty members for providing the laboratory facilities and academic environment that enabled the successful completion of this project. Lastly, I thank my family and peers for their constant moral support.

---

### 4. Abstract
Rapid urbanization and mobile lifestyles have accentuated the urgent necessity for reliable personal safety solutions. **SheSafe – Women's Safety & Emergency Support System** is a standalone, secure desktop application engineered in Java using **Java Swing**, **MySQL**, and **JDBC**. 

The system provides women with a single-click **Emergency SOS Distress Trigger**, a private **Emergency Contacts Circle**, formal **Incident Reporting**, and verified **Personal Safety Guidelines**. Addressing desktop hardware limitations (such as the absence of native satellite GPS chips), SheSafe incorporates dual-mode location handling combining automated IP-based Geolocation with manual landmark entry, generating direct Google Maps coordinates. Security is maintained through **BCrypt / Salted PBKDF2** password hashing, **PreparedStatement** SQL injection defenses, session lifecycle protection (`SessionManager`), and strict user isolation. The system was validated across 15 automated and manual test cases covering functional correctness, edge-case rejection, and fault tolerance.

---

### 5. Introduction
Personal safety is an essential human right. While mobile devices dominate the consumer space, personal workstations, laptops, and college campus terminals remain primary environments where students and working professionals spend prolonged hours. 

**SheSafe** is conceived as an intuitive, responsive desktop security management system that bridges emergency communication and preventive security education. By adhering to clean Object-Oriented Programming (OOP) principles, multi-tier architecture, and database relational integrity, SheSafe demonstrates how robust enterprise patterns can be applied to life-critical social safety challenges.

---

### 6. Problem Statement
Women traveling, working, or studying frequently encounter situations where swift distress notification is paramount. Existing web or manual systems suffer from several limitations:
1. **Prolonged reporting steps:** Emergency actions are hindered by deep menus.
2. **Plain-text vulnerability:** Insecure legacy systems store passwords and emergency phone numbers in clear text.
3. **Hardware assumption errors:** Many desktop software applications claim to access satellite GPS when PCs lack GPS receivers, leading to false confidence.
4. **Data leakage:** Multi-user desktop terminals often lack strict session guards, exposing private emergency contacts between users.

---

### 7. Objectives
1. Design an aesthetically pleasing, modern, and accessible Java Swing user interface utilizing a calming purple/pink theme.
2. Establish secure database connectivity using JDBC to persist user profiles, contacts, incidents, and SOS distress events in MySQL.
3. Enforce **BCrypt** and cryptographically salted password hashing to protect user credentials.
4. Provide immediate emergency distress broadcast simulation with verified delivery tracking per contact.
5. Provide transparent location-sharing tools generating valid Google Maps links without background surveillance.
6. Guarantee session security and data isolation so that users access solely their own records.

---

### 8. Scope of the Project
- **In Scope:**
  - Secure user registration, validation, and authenticated session management.
  - Adding, viewing, refreshing, and deleting trusted emergency contacts (phone numbers and emails).
  - Categorized safety guidelines functioning in-memory without database dependence.
  - Formal incident logging with location tagging, date tracking, and user history review.
  - SOS button with confirmation, duplicate alert prevention (5-second debounce lock), location sharing, and delivery status logging in MySQL.
- **Out of Scope (Desktop Environment Realities):**
  - Native cellular GSM SMS transmission without an external commercial SMS Gateway API.
  - Continuous background satellite tracking on PCs lacking GPS receivers.

---

### 9. Hardware & Software Requirements

#### Hardware Requirements
- **Processor:** Intel Core i3 / AMD Ryzen 3 or higher.
- **RAM:** Minimum 4 GB (8 GB recommended).
- **Hard Disk Space:** Minimum 200 MB free space.
- **Network Interface:** Active internet connection (for IP Geolocation and SMTP email alerts).

#### Software Requirements
- **Operating System:** Windows 10/11, macOS, or Linux.
- **Java Development Kit (JDK):** JDK 8, 11, 17, or 21+.
- **Database Server:** MySQL Server 8.0+ / XAMPP / MariaDB.
- **Integrated Development Environment (IDE):** IntelliJ IDEA Community / Eclipse IDE / NetBeans.
- **Database Driver:** MySQL Connector/J (`mysql-connector-j-8.x.x.jar`).

---

### 10. Technologies Used
- **Programming Language:** Java (Standard Edition).
- **GUI Framework:** Java Swing (`JFrame`, `JPanel`, `JButton`, `JTable`, `JScrollPane`, `JOptionPane`, `SwingWorker`).
- **Database:** MySQL Relational Database Management System (`InnoDB` engine).
- **Connectivity:** JDBC (`java.sql.Connection`, `PreparedStatement`, `ResultSet`).
- **Security:** BCrypt / PBKDF2WithHmacSHA256 with 128-bit `SecureRandom` salt.
- **Mapping:** Google Maps API URL Schema (`https://maps.google.com/?q=...`).

---

### 11. System Architecture

```text
+-------------------------------------------------------------------------+
|                          PRESENTATION LAYER (GUI)                       |
|   WelcomeScreen | RegistrationScreen | LoginScreen | DashboardScreen     |
|   EmergencyContactsScreen | SafetyTipsScreen | IncidentReportingScreen   |
|   SOSHistoryScreen                                                      |
+------------------------------------+------------------------------------+
                                     | (Swing Actions & Events)
                                     v
+-------------------------------------------------------------------------+
|                           SERVICE LAYER (LOGIC)                         |
|   SessionManager    : Session Lifecycle & Protected Screen Guards       |
|   AuthService       : Registration, Credential Regex, BCrypt Hash       |
|   ContactService    : Contact Validation & Isolation                    |
|   IncidentService   : Incident Validation & History Query               |
|   LocationService   : IP Geolocation & Google Maps Link Generation      |
|   NotificationService: JavaMail SMTP & SMS Gateway Dispatch             |
|   SOSAlertService   : Cooldown Lock, Dispatch Coordination, Result Calc |
+------------------------------------+------------------------------------+
                                     | (Data Transfer Objects & Entities)
                                     v
+-------------------------------------------------------------------------+
|                      DATA ACCESS OBJECT (DAO) LAYER                     |
|   UserDAO | EmergencyContactDAO | IncidentReportDAO | SOSAlertDAO        |
|   PreparedStatement parameterized queries with Try-With-Resources        |
+------------------------------------+------------------------------------+
                                     | (JDBC Connection via db.properties)
                                     v
+-------------------------------------------------------------------------+
|                             DATABASE LAYER                              |
|                          MySQL: shesafe_db                              |
|   [users] ◄── [emergency_contacts] | [incident_reports]                 |
|   [users] ◄── [sos_alerts] ◄── [sos_notifications]                      |
+-------------------------------------------------------------------------+
```

---

### 12. UML Diagrams

#### A. Use Case Diagram
```text
                     +---------------------------------------+
                     |                SheSafe                |
                     |                                       |
                     |  (Register Account)                   |
                     |         ^                             |
                     |         |                             |
                     |  (Authenticate / Login)               |
                     |         ^                             |
    (( User )) ──────+─────────+                             |
                     |  (Manage Emergency Contacts)          |
                     |  (Submit Incident Report)             |
                     |  (View Safety Guidelines)             |
                     |  (Trigger SOS Emergency Alert)        |
                     |         |                             |
                     |         v <<includes>>                |
                     |  (Authorize Location Sharing)         |
                     |  (Dispatch Notifications to Contacts) |
                     |  (View SOS Event History)             |
                     |  (Logout Session)                     |
                     +---------------------------------------+
```

#### B. Sequence Diagram: SOS Distress Initiation
```text
User            DashboardScreen       LocationService    SOSAlertService     NotificationService      MySQL DB
 │                     │                     │                  │                     │                  │
 ├──Click SOS─────────►│                     │                  │                     │                  │
 │◄──Confirm Dialog────┤                     │                  │                     │                  │
 ├──Click Confirm─────►│                     │                  │                     │                  │
 │◄──Location Prompt───┤                     │                  │                     │                  │
 ├──Authorize Loc──────┼──detectLocation()──►│                  │                     │                  │
 │                     │◄─return Location────┤                  │                     │                  │
 │                     ├──triggerSOS(loc)──────────────────────►│                     │                  │
 │                     │                                        ├──INSERT alert (TRIGGERED)─────────────►│
 │                     │                                        ├──fetchContacts(userId)────────────────►│
 │                     │                                        │◄─return Contacts───────────────────────┤
 │                     │                                        ├──notifyContact()───►│                  │
 │                     │                                        │◄─delivery status────┤                  │
 │                     │                                        ├──INSERT notification log──────────────►│
 │                     │                                        ├──UPDATE alert status (DELIVERED)──────►│
 │                     │◄─return SOSDispatchResult──────────────┤                                        │
 │◄──Feedback Dialog───┤                                                                                 │
```

---

### 13. Database Design & Entity Relationship (ER) Diagram

```text
+---------------------+       1:N       +----------------------------+
|        users        |────────────────<|     emergency_contacts     |
+---------------------+                 +----------------------------+
| PK user_id          |                 | PK contact_id              |
|    full_name        |                 | FK user_id                 |
|    email (UNIQUE)   |                 |    contact_name            |
|    phone            |                 |    contact_phone           |
|    password_hash    |                 |    relationship            |
|    created_at       |                 +----------------------------+
+----------+----------+
           │
           │ 1:N
           ├───────────────────────────<+----------------------------+
           │                            |      incident_reports      |
           │                            +----------------------------+
           │                            | PK report_id               |
           │                            | FK user_id                 |
           │                            |    incident_type           |
           │                            |    location                |
           │                            |    incident_date           |
           │                            |    description             |
           │                            |    created_at              |
           │                            +----------------------------+
           │
           │ 1:N
           v
+---------------------+       1:N       +----------------------------+
|      sos_alerts     |────────────────<|     sos_notifications      |
+---------------------+                 +----------------------------+
| PK alert_id         |                 | PK notification_id         |
| FK user_id          |                 | FK alert_id                |
|    alert_time       |                 |    contact_id (NULLABLE)   |
|    location         |                 |    recipient_name          |
|    location_type    |                 |    recipient_target        |
|    map_link         |                 |    channel                 |
|    status           |                 |    delivery_status         |
|    contacts_notified|                 |    status_details          |
|    created_at       |                 |    sent_at                 |
+---------------------+                 +----------------------------+
```

---

### 14. Module Descriptions
1. **Authentication & Session Module (`com.shesafe.service.AuthService`, `SessionManager`):**
   Manages user registration with regex validation, duplicate email prevention, and BCrypt salted password hashing. Guarantees session access boundaries across screens.
2. **Emergency Contacts Module (`com.shesafe.service.ContactService`, `EmergencyContactsScreen`):**
   Permits adding, inspecting, refreshing, and deleting verified guardians. Supports both phone numbers and email targets with user data isolation.
3. **Safety Knowledge Module (`SafetyTipsScreen`):**
   A self-contained in-memory resource providing practical commuting guidelines, situational vigilance protocols, and national helpline directories (112, 1091).
4. **Incident Reporting Module (`com.shesafe.service.IncidentService`, `IncidentReportingScreen`):**
   Logs formal harassment, stalking, or hazard records into MySQL with location landmarks, date validation, and historical report review.
5. **Emergency Distress SOS Module (`com.shesafe.service.SOSAlertService`, `NotificationService`, `LocationService`):**
   Executes emergency distress dispatches with duplicate-click protection, location authorization, JavaMail SMTP alerts, and per-contact delivery status logging.
6. **SOS History Module (`SOSHistoryScreen`):**
   Renders past SOS alerts in a `JTable` and provides drill-down inspection of individual recipient delivery logs.

---

### 15. Implementation Details
- **Decoupled Architecture:** GUI controllers invoke business services (`service`), which invoke data objects (`dao`), which communicate with MySQL via externalized configuration (`db.properties`).
- **Responsive UI Threads:** Network and database operations during SOS dispatch are executed in background threads using `SwingWorker`, ensuring the UI never enters a frozen state.
- **Fail-Safe Password Security:** `PasswordUtil` dynamically detects BCrypt and includes a built-in zero-dependency salted PBKDF2WithHmacSHA256 fallback with constant-time equality checks.

---

### 16. Testing & Test Cases

The following test suite was executed against the application and validated:

| Test Case ID | Feature Name | Test Scenario | Input Data | Expected Result | Actual Result | Status |
| :--- | :--- | :--- | :--- | :--- | :--- | :---: |
| **TC-01** | Registration | Register with valid information | Name: `Priya Sharma`, Email: `priya@test.com`, Phone: `9876543210`, Pass: `Secure@123` | Account created in `users`; password hashed; User ID generated. | User created in DB; hash stored; redirected to Login. | **PASS** |
| **TC-02** | Registration | Reject empty mandatory fields | Full Name left blank | Blocked with message: *"Full Name must be at least 2 characters."* | Warning dialog shown; submission stopped. | **PASS** |
| **TC-03** | Registration | Reject malformed email | Email: `priya_invalid_email` | Validation blocked: *"Please enter a valid email address."* | Warning dialog shown; submission stopped. | **PASS** |
| **TC-04** | Registration | Reject invalid phone number | Phone: `98765` (5 digits) | Validation blocked: *"Phone number must contain exactly 10 digits."* | Warning dialog shown; submission stopped. | **PASS** |
| **TC-05** | Registration | Reject password mismatch | Pass: `Password123`, Confirm: `Mismatch456` | Blocked: *"Passwords do not match. Please re-enter."* | Warning dialog shown; submission stopped. | **PASS** |
| **TC-06** | Registration | Reject duplicate email | Register with existing `priya@shesafe.com` | Blocked: *"The email address 'priya@shesafe.com' is already registered."* | Exception caught; friendly dialog displayed. | **PASS** |
| **TC-07** | Login | Login with correct credentials | Email: `priya@shesafe.com`, Pass: `password123` | Hash verified; `SessionManager` initialized; Dashboard opened. | Login successful; User ID #1 displayed in Dashboard. | **PASS** |
| **TC-08** | Login | Reject incorrect password | Email: `priya@shesafe.com`, Pass: `WrongPassword` | Authentication failed: *"Invalid email or password."* | Error dialog displayed; access denied. | **PASS** |
| **TC-09** | Security | Protected screen access guard | Open `DashboardScreen` directly without login | Blocked: *"Access Denied: You must be logged in"*; redirected to Login. | Access denied; redirected to LoginScreen. | **PASS** |
| **TC-10** | Session | Terminate session on logout | Click **Logout** on Dashboard and confirm | Session cleared in `SessionManager`; redirected to WelcomeScreen. | Session cleared; returning to Dashboard blocked. | **PASS** |
| **TC-11** | Contacts | Add emergency contact | Name: `Sunita Sharma`, Phone: `9876543211`, Rel: `Parent` | Contact saved in MySQL under active user's ID; table reloaded. | Saved in `emergency_contacts`; table updated. | **PASS** |
| **TC-12** | Contacts | Delete emergency contact | Select row in table and click **Delete Contact** | Contact removed from MySQL via parameterized DELETE query. | Row deleted from DB and removed from table. | **PASS** |
| **TC-13** | Contacts | Persistence after restart | Close application and restart `SheSafeApp` | Previously added contacts reload from MySQL into `JTable`. | All contacts loaded correctly from database. | **PASS** |
| **TC-14** | Incidents | Reject incomplete incident | Description left blank | Blocked: *"Detailed Description is mandatory."* | Warning dialog shown; submission aborted. | **PASS** |
| **TC-15** | Incidents | Valid incident submission | Category: `Stalking`, Location: `Metro Gate 2`, Date: `2026-10-11` | Saved in `incident_reports`; confirmation shows Report ID. | Saved in MySQL; appears under "My Past Reports". | **PASS** |
| **TC-16** | SOS Alert | SOS confirmation & dispatch | Click **🚨 PRESS SOS** and click **Confirm** | Prompts location; inserts alert in `sos_alerts`; logs delivery. | Alert event created; detailed breakdown modal displayed. | **PASS** |
| **TC-17** | SOS Alert | Cancel SOS request | Click **🚨 PRESS SOS** and click **Cancel** | Operation cancelled; no database records created. | Dialog closes; no alert created. | **PASS** |
| **TC-18** | SOS Alert | Duplicate alert prevention | Click SOS and confirm twice rapidly | Cooldown lock triggers: *"Alert in progress. Please wait X seconds..."* | Second click rejected by debounce cooldown. | **PASS** |
| **TC-19** | SOS History | View past alerts & recipient log | Click **🚨 SOS Alert History**, select alert | Displays historical events in table and drill-down recipient logs. | Alerts listed in `JTable`; delivery breakdown modal works. | **PASS** |
| **TC-20** | Security | User data isolation | Register User B; check contacts and SOS history | User B sees 0 contacts and 0 alerts belonging to User A. | Complete data isolation verified across all queries. | **PASS** |

---

### 17. Application Screen Layouts (GUI Design)

#### 1. Welcome Screen
```text
+-------------------------------------------------------------------------------+
|                               🛡 SheSafe                                      |
|                       Your Safety, Our Priority                               |
+-------------------------------------------------------------------------------+
|  Welcome to SheSafe Emergency & Personal Protection System                    |
|  Empowering women with instant alerts, verified contacts, and safety resources.|
|                                                                               |
|  +--------------------+  +--------------------+  +--------------------+       |
|  | 🚨 Instant SOS     |  | 👥 Trusted Circle  |  | 📝 Incident Reports|       |
|  | Rapid alerts &     |  | Instant access to  |  | Record safety events|      |
|  | dispatch protocols |  | guardians & police |  | & verified guides  |       |
|  +--------------------+  +--------------------+  +--------------------+       |
|                                                                               |
|            [ 🔑 Login to Account ]        [ ✨ Create New Account ]           |
+-------------------------------------------------------------------------------+
```

#### 2. Dashboard Screen
```text
+-------------------------------------------------------------------------------+
| 🛡 SheSafe Personal Security Dashboard                        [ 🚪 Logout ]   |
| Welcome back, Priya Sharma (User ID #1) • Active Protection: ENABLED          |
+-------------------------------------------------------------------------------+
| +---------------------------------------------------------------------------+ |
| | EMERGENCY DISTRESS TRIGGER (SOS)                                          | |
| | Tap in an emergency to initiate distress notifications to saved contacts  | |
| | with optional location coordinates.              [ 🚨 PRESS SOS ]         | |
| +---------------------------------------------------------------------------+ |
|                                                                               |
| +-------------------------+ +-------------------------+                       |
| | 📞 Emergency Contacts   | | 💡 Personal Safety Tips |                       |
| | Manage trusted guardians| | Review transit tactics  |                       |
| |     [ Open Contacts ]   | |    [ View Guidelines ]  |                       |
| +-------------------------+ +-------------------------+                       |
| +-------------------------+ +-------------------------+                       |
| | 📝 Incident Reporting   | | 🚨 SOS Alert History    |                       |
| | Formally document events| | Past emergency records  |                       |
| |    [ Report Incident ]  | |     [ View History ]    |                       |
| +-------------------------+ +-------------------------+                       |
|                                                                               |
| ⚡ Helplines: Police 112 | Women Helpline 1091 | Ambulance 108   ● Online     |
+-------------------------------------------------------------------------------+
```

---

### 18. Limitations
1. **Desktop GPS Hardware Constraints:** Standard personal computers lack internal satellite GPS hardware chips; coordinates are estimated via IP lookups or user-specified landmarks.
2. **Cellular SMS Gateway Requirement:** Direct SMS transmission from Java desktop software requires a third-party paid telecom SMS Gateway API (Twilio / Fast2SMS).
3. **Local MySQL Server Dependency:** The application requires MySQL Server or XAMPP to be actively running on the host system.

---

### 19. Future Enhancements
1. **Native Mobile Companion App:** Building an Android/iOS companion application leveraging native satellite GPS chips and direct mobile telephony APIs.
2. **IoT Hardware Distress Button:** Interfacing via Bluetooth Low Energy (BLE) or USB with a physical wearable distress button.
3. **Automated Audio Recording:** Triggering background microphone audio capture upon SOS confirmation.
4. **Community Heatmaps:** Aggregating anonymous incident reports to display high-risk transit zones on public safety maps.

---

### 20. Conclusion
**SheSafe – Women's Safety & Emergency Support System** achieves all project objectives across its five developmental phases. It provides an accessible, aesthetically unified desktop environment that pairs proactive security education with responsive emergency distress broadcasting. By incorporating layered architecture, BCrypt password hashing, session guards, user isolation, and honest reporting of desktop hardware limitations, SheSafe stands as a reliable, production-ready, and academically rigorous software engineering project.

---

### 21. References
1. Horstmann, C. S. (2022). *Core Java Volume I – Fundamentals*. Prentice Hall.
2. Oracle Corporation. (2024). *Java™ Platform, Standard Edition 17 API Specification*. Oracle Technology Network.
3. MySQL AB. (2024). *MySQL 8.0 Reference Manual*. Oracle Corporation.
4. Provos, N., & Mazières, D. (1999). *A Future-Adaptable Password Scheme*. Proceedings of the USENIX Annual Technical Conference.
5. Ministry of Home Affairs, Government of India. (2024). *Emergency Response Support System (ERSS - 112)*. https://112.gov.in/
