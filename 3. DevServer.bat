@echo off
cd /d C:\Users\andre\Desktop\727-source || goto :error

echo Running Avalon
echo.

REM Run Gradle
call gradlew run --args="false 43594"

echo.
echo Gradle exited with code %errorlevel%.
echo.

pause
exit /b

:error
echo Failed to change directory!
pause
