# ================================================================
# HAEUN — Full Stack 통합 실행 스크립트
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

# Java 17 경로 (run-java17.ps1과 동일)
$java17Path = "C:\Program Files\Eclipse Adoptium\jdk-17.0.19.10-hotspot"

# 경로 설정
$serviceDir       = Join-Path $PSScriptRoot "python-memory-service"
$venvDir          = Join-Path $serviceDir ".venv"
$activateScript   = Join-Path $venvDir "Scripts\Activate.ps1"
$runPythonScript  = Join-Path $PSScriptRoot "run-python-memory.ps1"

# Python 프로세스 핸들 (종료 시 정리에 사용)
$global:haeunPythonProcess = $null

Write-Host ""
Write-Host "======================================================"
Write-Host " [HAEUN] Full Stack 통합 실행"
Write-Host " Python Vector Memory Service + Java Spring Boot"
Write-Host "======================================================"
Write-Host ""

# ── 1. 가상환경 존재 확인 ──────────────────────────────────────
if (!(Test-Path $activateScript)) {
    Write-Host "[HAEUN] Python 가상환경(.venv)이 없습니다."
    Write-Host ""
    Write-Host "        먼저 환경 설정 스크립트를 실행해주세요:"
    Write-Host ""
    Write-Host "        powershell -ExecutionPolicy Bypass -File .\setup-python-memory.ps1"
    Write-Host ""
    exit 1
}

# ── 2. Java 17 환경변수 설정 ───────────────────────────────────
if (Test-Path $java17Path) {
    $env:JAVA_HOME = $java17Path
    $env:Path      = "$env:JAVA_HOME\bin;$env:Path"
    Write-Host "[HAEUN] Java 17 설정됨: $java17Path"
} else {
    Write-Warning "[HAEUN] Java 17 경로를 찾을 수 없습니다: $java17Path"
    Write-Warning "[HAEUN] 시스템 기본 Java로 계속 진행합니다."
}

# ── 3. Python Memory Service 백그라운드 실행 (새 창) ────────────
Write-Host "[HAEUN] Starting Python Vector Memory Service..."
$global:haeunPythonProcess = Start-Process powershell `
    -ArgumentList "-ExecutionPolicy Bypass -File `"$runPythonScript`"" `
    -PassThru `
    -WindowStyle Normal

# PowerShell 세션 종료 시 Python 프로세스 자동 정리 등록
Register-EngineEvent -SourceIdentifier PowerShell.Exiting -Action {
    if ($null -ne $global:haeunPythonProcess -and -not $global:haeunPythonProcess.HasExited) {
        Stop-Process -Id $global:haeunPythonProcess.Id -Force -ErrorAction SilentlyContinue
    }
} | Out-Null

# ── 4. Health Check — 최대 10회 재시도 (2초 간격, 최대 20초) ───
Write-Host "[HAEUN] Waiting for Python Memory Service health check..."

$maxRetries = 10
$retryCount = 0
$ready      = $false

while ($retryCount -lt $maxRetries -and -not $ready) {
    Start-Sleep -Seconds 2
    try {
        $response = Invoke-WebRequest `
            -Uri "http://127.0.0.1:8001/health" `
            -UseBasicParsing `
            -TimeoutSec 2 `
            -ErrorAction Stop
        if ($response.StatusCode -eq 200) {
            $ready = $true
        }
    } catch {
        $retryCount++
        Write-Host "[HAEUN] Waiting for Python Memory Service... ($retryCount/$maxRetries)"
    }
}

# ── 5. Spring Boot 실행 ────────────────────────────────────────
if (-not $ready) {
    Write-Host ""
    Write-Warning "[HAEUN] Python Memory Service가 응답하지 않습니다."
    Write-Warning "[HAEUN] Vector Memory 없이 Java만 실행합니다. (기존 Java 기억 모드)"
    Write-Host ""
    Write-Host "[HAEUN] Starting Spring Boot (vector memory disabled)..."
    Write-Host "[HAEUN] Open http://localhost:8080"
    Write-Host ""

    .\gradlew.bat bootRun

} else {
    Write-Host "[HAEUN] Python Memory Service is ready."
    Write-Host ""
    Write-Host "[HAEUN] Starting Spring Boot with vector memory enabled..."
    Write-Host "[HAEUN] Open http://localhost:8080"
    Write-Host ""

    # application.yml의 enabled=false를 커맨드라인 인자로 오버라이드
    .\gradlew.bat bootRun '--args=--haeun.memory.vector.enabled=true'
}

# ── 6. Java 종료 후 Python 프로세스 정리 ───────────────────────
Write-Host ""
Write-Host "[HAEUN] Java Spring Boot가 종료되었습니다."

if ($null -ne $global:haeunPythonProcess -and -not $global:haeunPythonProcess.HasExited) {
    Write-Host "[HAEUN] Python Memory Service를 종료하는 중..."
    Stop-Process -Id $global:haeunPythonProcess.Id -Force -ErrorAction SilentlyContinue
    Write-Host "[HAEUN] Python Memory Service 종료 완료."
} else {
    Write-Host "[HAEUN] Python Memory Service는 이미 종료되었습니다."
}

Write-Host ""
Write-Host "[HAEUN] 모든 서비스가 종료되었습니다."
