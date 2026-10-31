# Battleship Platform - PowerShell Development Environment Launcher
Write-Host "==============================================================================" -ForegroundColor Cyan
Write-Host "Battleship Platform - Starting Full-Stack Development Cluster" -ForegroundColor Green
Write-Host "Backend: Spring Boot 3 (Port 8080)" -ForegroundColor Yellow
Write-Host "Frontend: Next.js 16 (Port 3000)" -ForegroundColor Yellow
Write-Host "==============================================================================" -ForegroundColor Cyan

# Launch Spring Boot Backend
Start-Process powershell -ArgumentList "-NoExit", "-Command", "Write-Host 'Starting Battleship Backend (Spring Boot 3)...' -ForegroundColor Green; .\mvnw.cmd -pl backend spring-boot:run"

# Launch Next.js Frontend
Start-Process powershell -ArgumentList "-NoExit", "-Command", "Write-Host 'Starting Battleship Frontend (Next.js 16)...' -ForegroundColor Cyan; cd frontend; npm run dev"

Write-Host "`nBoth development services launched in separate windows." -ForegroundColor Green
Write-Host "- Web Terminal: http://localhost:3000" -ForegroundColor White
Write-Host "- Swagger UI:   http://localhost:8080/swagger-ui.html" -ForegroundColor White
Write-Host "- H2 Database:  http://localhost:8080/h2-console" -ForegroundColor White
