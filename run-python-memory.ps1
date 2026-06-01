# ================================================================
# HAEUN — Python Memory Service 실행 스크립트
#
# 역할:
#   FastAPI + ChromaDB 기반 의미 기억 서비스를 로컬에서 실행한다.
#   Java Spring Boot와 별개로 독립 실행 가능하다.
#
# 실행 방법:
#   powershell -ExecutionPolicy Bypass -File .\run-python-memory.ps1
#
# 종료:
#   Ctrl + C
#
# 사전 조건:
#   setup-python-memory.ps1 을 먼저 실행해야 한다.
# ================================================================

$ErrorActionPreference = "Stop"

$serviceDir     = Join-Path $PSScriptRoot "python-memory-service"
$venvDir        = Join-Path $serviceDir ".venv"
$activateScript = Join-Path $venvDir "Scripts\Activate.ps1"

Write-Host ""
Write-Host "======================================================"
Write-Host " [HAEUN] Python Memory Service 실행"
Write-Host "======================================================"
Write-Host ""

# 가상환경 존재 확인
if (!(Test-Path $activateScript)) {
    Write-Host "[HAEUN] 가상환경(.venv)이 없습니다."
    Write-Host ""
    Write-Host "        먼저 환경 설정 스크립트를 실행해주세요:"
    Write-Host ""
    Write-Host "        powershell -ExecutionPolicy Bypass -File .\setup-python-memory.ps1"
    Write-Host ""
    exit 1
}

# 가상환경 활성화
Write-Host "[HAEUN] 가상환경 활성화 중..."
. $activateScript

# python-memory-service 디렉토리로 이동
Set-Location $serviceDir

Write-Host "[HAEUN] Python Memory Service 시작 중..."
Write-Host ""
Write-Host "  주소:   http://127.0.0.1:8001"
Write-Host "  Swagger: http://127.0.0.1:8001/docs"
Write-Host "  Health:  http://127.0.0.1:8001/health"
Write-Host ""
Write-Host "  종료: Ctrl + C"
Write-Host ""
Write-Host "  주의: 최초 실행 시 임베딩 모델(약 400MB) 다운로드로"
Write-Host "        시작까지 수 분이 걸릴 수 있습니다."
Write-Host ""

# FastAPI 서버 실행 (--reload: 코드 변경 시 자동 재시작)
uvicorn main:app --host 127.0.0.1 --port 8001 --reload
