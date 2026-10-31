@echo off
title Battleship Platform - Development Environment Launcher
echo ==============================================================================
echo Battleship Platform - Starting Full-Stack Development Cluster
echo Backend: Spring Boot 3 (Port 8080)
echo Frontend: Next.js 16 (Port 3000)
echo ==============================================================================

echo Launching Spring Boot Backend in a new window...
start "Battleship - Backend Engine" cmd /k ".\mvnw.cmd -pl backend spring-boot:run"

echo Launching Next.js Frontend in a new window...
start "Battleship - Frontend Terminal" cmd /k "cd frontend && npm run dev"

echo ==============================================================================
echo Both services have been launched in separate terminal windows.
echo - Web Terminal: http://localhost:3000
echo - Swagger UI:   http://localhost:8080/swagger-ui.html
echo - H2 Database:  http://localhost:8080/h2-console
echo ==============================================================================
timeout /t 5
