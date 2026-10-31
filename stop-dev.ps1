# Battleship Platform - Stop Development Servers Script
Write-Host "==============================================================================" -ForegroundColor Cyan
Write-Host "Battleship Platform - Stopping Full-Stack Development Servers" -ForegroundColor Yellow
Write-Host "==============================================================================" -ForegroundColor Cyan

$ports = @(8080, 3000)

foreach ($port in $ports) {
    $connections = Get-NetTCPConnection -LocalPort $port -State Listen -ErrorAction SilentlyContinue
    if ($connections) {
        foreach ($conn in $connections) {
            $pidToKill = $conn.OwningProcess
            try {
                $proc = Get-Process -Id $pidToKill -ErrorAction SilentlyContinue
                Write-Host "Stopping process $($proc.ProcessName) (PID: $pidToKill) on port $port..." -ForegroundColor Green
                Stop-Process -Id $pidToKill -Force -ErrorAction SilentlyContinue
            } catch {
                # Process already exited
            }
        }
    } else {
        Write-Host "No active listener found on port $port." -ForegroundColor Gray
    }
}

Write-Host "`nPorts 8080 and 3000 are freed. Development servers stopped." -ForegroundColor Green
