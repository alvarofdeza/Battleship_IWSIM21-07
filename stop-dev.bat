@echo off
title Battleship Platform - Stop Development Servers
echo ==============================================================================
echo Battleship Platform - Stopping Full-Stack Development Servers
echo ==============================================================================

echo Stopping processes listening on port 8080 (Spring Boot Backend)...
for /f "tokens=5" %%a in ('netstat -aon ^| findstr ":8080 " ^| findstr "LISTENING"') do (
    echo Terminating PID: %%a
    taskkill /F /PID %%a >nul 2>&1
)

echo Stopping processes listening on port 3000 (Next.js Frontend)...
for /f "tokens=5" %%a in ('netstat -aon ^| findstr ":3000 " ^| findstr "LISTENING"') do (
    echo Terminating PID: %%a
    taskkill /F /PID %%a >nul 2>&1
)

echo ==============================================================================
echo Development servers stopped and ports 8080 and 3000 freed.
echo ==============================================================================
timeout /t 3
