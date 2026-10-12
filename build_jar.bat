@echo off
title Build Executable JAR - SheSafe
color 0A
echo ====================================================================
echo        SheSafe - Automated Executable JAR Builder
echo ====================================================================
echo.

:: 1. Check for JDK
javac -version >nul 2>&1
if %errorlevel% neq 0 (
    echo [ERROR] JDK (javac) is not found in your system PATH!
    echo Please make sure Java Development Kit (JDK) is installed and PATH is set.
    pause
    exit /b
)

:: 2. Clean and create output directory
if not exist "bin" mkdir bin
if not exist "bin\META-INF" mkdir bin\META-INF

:: 3. Set Classpath
set CP=bin;lib/*;*

echo [Step 1/4] Compiling Java classes...
javac -cp "%CP%" -d bin src/com/shesafe/*.java src/com/shesafe/model/*.java src/com/shesafe/dao/*.java src/com/shesafe/service/*.java src/com/shesafe/database/*.java src/com/shesafe/ui/*.java src/com/shesafe/test/*.java

if %errorlevel% neq 0 (
    echo [ERROR] Compilation failed. Please check source files for errors.
    pause
    exit /b
)

:: 4. Copy configuration files to bin
echo [Step 2/4] Bundling configuration files...
if exist "src\db.properties" copy "src\db.properties" "bin\db.properties" >nul
if exist "db.properties" copy "db.properties" "bin\db.properties" >nul

:: 5. Create Manifest File
echo [Step 3/4] Generating Application Manifest...
(
echo Manifest-Version: 1.0
echo Main-Class: com.shesafe.SheSafeApp
echo Class-Path: . lib/mysql-connector-j-8.3.0.jar lib/mysql-connector-j-8.0.33.jar mysql-connector-j-8.3.0.jar mysql-connector-j-8.0.33.jar
) > bin\META-INF\MANIFEST.MF

:: 6. Package into JAR
echo [Step 4/4] Packaging into SheSafe.jar...
jar cfm SheSafe.jar bin\META-INF\MANIFEST.MF -C bin .

if exist "SheSafe.jar" (
    echo.
    echo ====================================================================
    echo [SUCCESS] SheSafe.jar has been created successfully!
    echo.
    echo To run the application:
    echo   1. Double-click "SheSafe.jar"
    echo   OR
    echo   2. Run command: java -jar SheSafe.jar
    echo ====================================================================
) else (
    echo [ERROR] Failed to package SheSafe.jar.
)

pause
