@echo off
cd /d C:\Users\andre\Desktop\727-source

echo Starting Avalon...
echo.

java -Xms1G -Xmx4G -XX:+UseG1GC -cp "build/libs/727-source-1.0.jar;lib/*;data/libs/*" com.rs.Launcher false 43594

pause
