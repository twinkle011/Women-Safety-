# SheSafe – Complete Project Deployment & Distribution Guide
## How to Deploy, Package, and Distribute the SheSafe Application

---

### 📌 Overview
**SheSafe** is a **Java Swing Desktop Application** with a **MySQL Relational Database**.

Deploying a desktop application consists of three primary elements:
1. **Packaging the Application:** Compiling and packaging Java classes into a standalone runnable file (**`.jar`** or native **`.exe`**).
2. **Deploying the Database:** Providing access to MySQL either locally (on the same machine) or in the cloud (remote access over the internet).
3. **Creating a Distribution Bundle:** Packaging the executable, database setup script, configuration, and dependencies into a clean ZIP folder for submission or client delivery.

---

### 🚀 Option 1: 1-Click Automated Build (Fastest Method)

Two automated Windows batch scripts have been pre-configured in your project folder:

#### 1. Quick Launch Without IDE: [`run.bat`](file:///c:/Users/ACER/Desktop/WOMEN%20SAFETY%20PROJECT/run.bat)
- Double-click [`run.bat`](file:///c:/Users/ACER/Desktop/WOMEN%20SAFETY%20PROJECT/run.bat).
- It automatically verifies Java, compiles the source files into `bin/`, loads `db.properties`, and launches the SheSafe application immediately.

#### 2. Build Standalone JAR: [`build_jar.bat`](file:///c:/Users/ACER/Desktop/WOMEN%20SAFETY%20PROJECT/build_jar.bat)
- Double-click [`build_jar.bat`](file:///c:/Users/ACER/Desktop/WOMEN%20SAFETY%20PROJECT/build_jar.bat).
- It compiles all classes and generates **`SheSafe.jar`** in your project root with the main entry point set to `com.shesafe.SheSafeApp`.
- You can now launch SheSafe by double-clicking `SheSafe.jar` or running:
  ```bash
  java -jar SheSafe.jar
  ```

---

### 🛠 Option 2: Generating a Runnable JAR in IntelliJ IDEA

To build a self-contained "Fat JAR" containing all dependencies directly inside IntelliJ:

1. Open the project in **IntelliJ IDEA**.
2. Go to **File ➔ Project Structure...** (or press `Ctrl + Alt + Shift + S`).
3. Click on **Artifacts** in the left panel.
4. Click the green **`+`** icon ➔ **JAR ➔ From modules with dependencies...**.
5. In the configuration window:
   - **Main Class:** Click the folder icon and select `com.shesafe.SheSafeApp`.
   - **JAR files from libraries:** Select *"extract to the target JAR"*.
   - **Directory for META-INF/MANIFEST.MF:** Select `src` folder.
   - Click **OK**.
6. Check the box **Include in project build**.
7. Click **Apply ➔ OK**.
8. Build the JAR:
   - In the top menu, click **Build ➔ Build Artifacts... ➔ SheSafe:jar ➔ Build**.
9. Your standalone executable JAR will be generated in `out/artifacts/SheSafe_jar/SheSafe.jar`.

---

### 💻 Option 3: Generating a Runnable JAR in Eclipse IDE

1. Open **Eclipse**.
2. In the **Package Explorer**, right-click your `SheSafe` project.
3. Select **Export...**.
4. In the Export wizard, expand **Java ➔ Runnable JAR file** and click **Next**.
5. Configure the export settings:
   - **Launch configuration:** Select `SheSafeApp - SheSafe`.
   - **Export destination:** Browse and choose `C:\Users\ACER\Desktop\WOMEN SAFETY PROJECT\SheSafe.jar`.
   - **Library handling:** Select *"Package required libraries into generated JAR"*.
6. Click **Finish**.
7. `SheSafe.jar` is now created and ready to run with double-click!

---

### 🪟 Option 4: Converting JAR to Native Windows `.exe`

To distribute a professional Windows application that runs with a `.exe` icon and does not show terminal windows:

#### Method A: Using Launch4j (Free & Open Source)
1. Download **Launch4j** from [launch4j.sourceforge.net](http://launch4j.sourceforge.net/).
2. Open Launch4j and set:
   - **Output file:** `C:\Users\ACER\Desktop\WOMEN SAFETY PROJECT\SheSafe.exe`
   - **Jar:** `C:\Users\ACER\Desktop\WOMEN SAFETY PROJECT\SheSafe.jar`
   - **Icon (Optional):** Choose any `.ico` safety shield icon.
3. Under the **JRE** tab:
   - Set **Min JRE version:** `1.8.0` (or `11.0.0`).
4. Click the gear icon (**Build wrapper**).
5. You now have a native **`SheSafe.exe`** that opens the application directly!

#### Method B: Using JDK `jpackage` (Creates Standalone Installer with Bundled Java)
If the target computer does not have Java installed, JDK 17+ includes `jpackage` to bundle the Java runtime directly with your `.exe`:
```powershell
jpackage --input . --name SheSafe --main-jar SheSafe.jar --main-class com.shesafe.SheSafeApp --type exe --win-shortcut --win-menu
```
This produces a complete Windows installer (`SheSafe-1.0.exe`) that installs the software like any commercial desktop application.

---

### 🗄 Option 5: Database Deployment (Local vs Cloud)

#### Strategy A: Local Database Deployment (College Demonstration)
When presenting on your laptop or in the college lab:
1. Ensure **XAMPP** or **MySQL Server** is installed on the computer.
2. Start the MySQL service.
3. Execute [`database_schema.sql`](file:///c:/Users/ACER/Desktop/WOMEN%20SAFETY%20PROJECT/database_schema.sql) in MySQL Workbench or phpMyAdmin.
4. Keep [`db.properties`](file:///c:/Users/ACER/Desktop/WOMEN%20SAFETY%20PROJECT/db.properties) configured for `localhost:3306`:
   ```properties
   db.url=jdbc:mysql://localhost:3306/shesafe_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
   db.user=root
   db.password=
   ```

#### Strategy B: Cloud MySQL Database (Access from ANY Computer Anywhere)
If you want your application to connect over the internet without installing MySQL on the client machine:
1. Create a free cloud MySQL database on:
   - **Aiven:** [aiven.io/mysql](https://aiven.io/mysql) (Free tier)
   - **Clever Cloud:** [clever-cloud.com](https://www.clever-cloud.com/) (Free MySQL add-on)
   - **Railway:** [railway.app](https://railway.app/)
2. Open the cloud console and copy your public connection details (Host, Port, User, Password, Database Name).
3. Import [`database_schema.sql`](file:///c:/Users/ACER/Desktop/WOMEN%20SAFETY%20PROJECT/database_schema.sql) into the cloud database.
4. Update [`db.properties`](file:///c:/Users/ACER/Desktop/WOMEN%20SAFETY%20PROJECT/db.properties):
   ```properties
   db.url=jdbc:mysql://your-cloud-host.com:3306/shesafe_db?useSSL=true&serverTimezone=UTC
   db.user=your_cloud_user
   db.password=your_cloud_password
   ```
5. Now, anyone with `SheSafe.jar` or `SheSafe.exe` can run the app on their computer, and all alerts, users, and contacts will sync to the same cloud database in real-time!

---

### 📦 Option 6: Final Submission & Distribution Package

To submit your project to your college professor or examiner, create a distribution ZIP folder structured as follows:

```text
SheSafe_Final_Submission/
│
├── SheSafe.jar                       # Standalone Executable Application (Double-click to run)
├── SheSafe.exe                       # Native Windows Executable (Optional)
├── run.bat                           # 1-Click Windows Launcher Script
├── db.properties                     # Configuration file for Database & Notifications
├── database_schema.sql               # Complete MySQL Setup Script
│
├── lib/                              # External Libraries
│   └── mysql-connector-j-8.3.0.jar   # MySQL JDBC Driver
│
├── Documentation/
│   ├── FINAL_PROJECT_REPORT.md       # Complete 21-Section Academic Project Report
│   ├── FINAL_PRESENTATION.md         # 12-Slide Presentation Deck for Viva
│   └── README.md                     # Setup & User Manual
│
└── src/                              # Clean Full Java Source Code
    └── com/
        └── shesafe/
            ├── model/
            ├── dao/
            ├── service/
            ├── database/
            ├── ui/
            ├── test/
            └── screens/
```

---

### ❓ Troubleshooting Common Deployment Issues

| Issue | Cause | Solution |
| :--- | :--- | :--- |
| **Double-clicking JAR does nothing** | Windows file association for `.jar` is missing or mapped to WinRAR/7-Zip. | 1. Run via terminal: `java -jar SheSafe.jar`<br>2. Or run the free utility **Jarfix** ([johannepfl.de/jarfix](https://johannepfl.de/jarfix/)) to restore Windows double-click association. |
| **`ClassNotFoundException: com.mysql.cj.jdbc.Driver`** | MySQL Connector driver JAR is not bundled in the JAR or missing from `lib/`. | Ensure `mysql-connector-j-8.3.0.jar` is in the `lib` folder next to the JAR, or export as a "Fat JAR" containing extracted libraries. |
| **`Communications link failure`** | MySQL server is stopped or port 3306 is blocked. | Open XAMPP Control Panel and verify the green **Running** status next to MySQL. |
| **Emails not delivering in SMTP mode** | Using standard Gmail password instead of an App Password. | Go to Google Account Security ➔ 2-Step Verification ➔ **App passwords**, generate a 16-character password, and paste into `smtp.password` in `db.properties`. |
