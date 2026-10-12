@echo off
title Push SheSafe to GitHub
color 0B
echo ====================================================================
echo             SheSafe - Push Project to GitHub Repository
echo ====================================================================
echo.

:: 1. Verify Git Installation
git --version >nul 2>&1
if %errorlevel% neq 0 (
    echo [ERROR] Git is not installed or not found on your system PATH!
    echo Please install Git from https://git-scm.com/
    pause
    exit /b
)

:: 2. Initialize Git Repository if not already initialized
if not exist ".git" (
    echo [Step 1/5] Initializing new Git repository...
    git init
) else (
    echo [Step 1/5] Existing Git repository detected.
)

:: 3. Stage All Project Files
echo [Step 2/5] Staging project files (respecting .gitignore)...
git add .

:: 4. Commit Changes
echo [Step 3/5] Committing changes...
git commit -m "SheSafe - Complete Women's Safety & Emergency Support System (Phases 1-5)" >nul 2>&1
if %errorlevel% neq 0 (
    echo [INFO] No new changes to commit or working tree clean.
)

:: 5. Set default branch to main
git branch -M main

:: 6. Check for Remote Origin
echo.
git remote get-url origin >nul 2>&1
if %errorlevel% neq 0 (
    echo [Step 4/5] No GitHub remote repository configured yet.
    echo.
    echo Please create a new repository on GitHub:
    echo   1. Go to https://github.com/new
    echo   2. Repository name: SheSafe (or any name you prefer)
    echo   3. Leave "Initialize with README" UNCHECKED
    echo   4. Click "Create repository"
    echo.
    set /p REPO_URL="Enter your GitHub Repository URL (e.g. https://github.com/username/SheSafe.git): "
    
    if "%REPO_URL%"=="" (
        echo [ERROR] No repository URL provided. Aborting push.
        pause
        exit /b
    )
    git remote add origin %REPO_URL%
) else (
    echo [Step 4/5] Remote origin already configured:
    git remote -v
)

:: 7. Push to GitHub
echo.
echo [Step 5/5] Pushing to GitHub (main branch)...
git push -u origin main

if %errorlevel% equ 0 (
    echo.
    echo ====================================================================
    echo [SUCCESS] SheSafe has been successfully pushed to GitHub!
    echo ====================================================================
) else (
    echo.
    echo ====================================================================
    echo [NOTE] If GitHub prompted for authentication:
    echo   - Use your GitHub Personal Access Token (PAT) as the password.
    echo   - Or sign in using the GitHub CLI / Git Credential Manager window.
    echo ====================================================================
)

pause
