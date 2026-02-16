@echo off
title Avalon - Updating Server
cd /d C:\Users\andre\Desktop\727-source || goto :error

echo ==============================
echo   Building Production Jar...
echo ==============================
echo.

.\gradlew clean shadowJar

if %errorlevel% neq 0 (
    echo.
    echo Build FAILED.
    pause
    exit /b
)

echo.
echo Build completed successfully!
echo.
pause
exit /b

:error
echo Failed to change directory!
pause
