# ================================================================
# HAEUN - Full Stack 통합 실행 스크립트
#
# 역할:
#   Python Vector Memory Service + Java Spring Boot를 함께 실행한다.
#   Python 서비스를 새 창에서 열고, Java는 현재 창에서 실행한다.
#   Java 종료 시 Python 프로세스도 함께 정리한다.
#
# 실행 방법:
#   powershell -ExecutionPolicy Bypass -File .\run-haeun-full.ps1
#
# 사전 조건:
#   setup-python-memory.ps1 을 먼저 1회 실행해야 한다.
#
# Spring Boot 설정:
#   application.yml의 haeun.memory.vector.enabled=false 여도
#   이 스크립트로 실행하면 true로 오버라이드된다.
# ================================================================

$ErrorActionPreference = "Continue"

$java17Path      = "C:\Program Files\Eclipse Adoptium\jdk-17.0.19.10-hotspot"
$runMemoryScript = Join-Path $PSScriptRoot "run-python-memory.ps1"
$memoryVenvCheck = Join-Path $PSScriptRoot "python-memory-service\.venv\Scripts\Activate.ps1"

$global:haeunMemoryProcess = $null

Write-Host ""
Write-Host "======================================================"
Write-Host " [HAEUN] Full Stack 통합 실행"
Write-Host " Python Vector Memory Service + Java Spring Boot"
Write-Host "======================================================"
Write-Host ""

# 가상환경 존재 확인
if (!(Test-Path $memoryVenvCheck)) {
    Write-Host "[HAEUN] Python 가상환경(.venv)이 없습니다."
    Write-Host ""
    Write-Host "        먼저 환경 설정 스크립트를 실행해주세요:"
    Write-Host ""
    Write-Host "        powershell -ExecutionPolicy Bypass -File .\setup-python-memory.ps1"
    Write-Host ""
    exit 1
}

# Java 17 환경변수 설정
if (Test-Path $java17Path) {
    $env:JAVA_HOME = $java17Path
    $env:Path      = "$env:JAVA_HOME\bin;$env:Path"
    Write-Host "[HAEUN] Java 17 설정됨: $java17Path"
} else {
    Write-Warning "[HAEUN] Java 17 경로를 찾을 수 없습니다. 시스템 기본 Java로 계속합니다."
}

# Python Memory Service 시작 (새 창)
Write-Host "[HAEUN] Starting Python Vector Memory Service..."
$global:haeunMemoryProcess = Start-Process powershell `
    -ArgumentList "-ExecutionPolicy Bypass -File `"$runMemoryScript`"" `
    -PassThru -WindowStyle Normal

# 종료 시 Python 프로세스 정리 등록
Register-EngineEvent -SourceIdentifier PowerShell.Exiting -Action {
    if ($null -ne $global:haeunMemoryProcess -and -not $global:haeunMemoryProcess.HasExited) {
        Stop-Process -Id $global:haeunMemoryProcess.Id -Force -ErrorAction SilentlyContinue
    }
} | Out-Null

# Health Check (최대 10회, 2초 간격)
Write-Host "[HAEUN] Waiting for Python Memory Service health check..."
$maxRetries = 10; $retryCount = 0; $ready = $false

while ($retryCount -lt $maxRetries -and -not $ready) {
    Start-Sleep -Seconds 2
    try {
        $r = Invoke-WebRequest -Uri "http://127.0.0.1:8001/health" -UseBasicParsing -TimeoutSec 2 -ErrorAction Stop
        if ($r.StatusCode -eq 200) { $ready = $true }
    } catch {
        $retryCount++
        Write-Host "[HAEUN] Waiting... ($retryCount/$maxRetries)"
    }
}

# Spring Boot 실행
if (-not $ready) {
    Write-Warning "[HAEUN] Memory Service 미응답 - vector memory 없이 실행합니다."
    .\gradlew.bat bootRun
} else {
    Write-Host "[HAEUN] Python Memory Service is ready."
    Write-Host "[HAEUN] Starting Spring Boot with vector memory enabled..."
    Write-Host "[HAEUN] Open http://localhost:8080"
    Write-Host ""
    .\gradlew.bat bootRun '--args=--haeun.memory.vector.enabled=true'
}

# 종료 후 정리
Write-Host ""
Write-Host "[HAEUN] Java Spring Boot가 종료되었습니다."
if ($null -ne $global:haeunMemoryProcess -and -not $global:haeunMemoryProcess.HasExited) {
    Write-Host "[HAEUN] Python Memory Service를 종료하는 중..."
    Stop-Process -Id $global:haeunMemoryProcess.Id -Force -ErrorAction SilentlyContinue
}
Write-Host "[HAEUN] 모든 서비스가 종료되었습니다."
