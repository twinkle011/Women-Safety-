@echo off
title SheSafe - Women's Safety System
color 0D
echo ====================================================================
echo        SheSafe - Women's Safety & Emergency Support System
echo                 1-Click Windows Application Launcher
echo ====================================================================
echo.

:: 1. Check for Java Installation
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo [ERROR] Java JDK or JRE is not detected on your system PATH!
    echo Please install Java 8 or higher from https://www.oracle.com/java/
    pause
    exit /b
)

:: 2. Set Classpath (Include src/bin and any JARs in lib folder)
set CP=bin;src;lib/*;*

:: 3. Create bin output directory if not present
if not exist "bin" mkdir bin

:: 4. Compile all Java files if bin is empty or source updated
echo [1/2] Compiling SheSafe Java source files...
javac -cp "%CP%" -d bin src/com/shesafe/*.java src/com/shesafe/model/*.java src/com/shesafe/dao/*.java src/com/shesafe/service/*.java src/com/shesafe/database/*.java src/com/shesafe/ui/*.java src/com/shesafe/test/*.java >nul 2>&1

:: Copy db.properties into bin for classpath resolution
if exist "src\db.properties" copy "src\db.properties" "bin\db.properties" >nul 2>&1
if exist "db.properties" copy "db.properties" "bin\db.properties" >nul 2>&1

echo [2/2] Starting SheSafe Application...
echo.
echo ====================================================================
echo Application running! Keep this window open or minimize it.
echo Close the SheSafe window to exit.
echo ====================================================================

java -cp "%CP%;bin" com.shesafe.SheSafeApp

pause
