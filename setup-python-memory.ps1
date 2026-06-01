# ================================================================
# HAEUN — Python Memory Service 환경 설정 스크립트
#
# 역할:
#   python-memory-service/.venv 가상환경 생성 및 의존성 설치
#
# 실행 방법:
#   powershell -ExecutionPolicy Bypass -File .\setup-python-memory.ps1
#
# 주의:
#   최초 실행 시 sentence-transformers 모델(약 400MB)이 다운로드됩니다.
#   모델은 서버 실행 시 로드되며 이 스크립트에서는 다운로드하지 않습니다.
# ================================================================

$ErrorActionPreference = "Stop"

Write-Host ""
Write-Host "======================================================"
Write-Host " [HAEUN] Python Memory Service 환경 설정"
Write-Host "======================================================"
Write-Host ""

# 프로젝트 루트 기준 경로 설정
$serviceDir       = Join-Path $PSScriptRoot "python-memory-service"
$venvDir          = Join-Path $serviceDir ".venv"
$requirementsFile = Join-Path $serviceDir "requirements.txt"
$activateScript   = Join-Path $venvDir "Scripts\Activate.ps1"

# python-memory-service 디렉토리 존재 확인
if (!(Test-Path $serviceDir)) {
    Write-Host "[HAEUN] 오류: python-memory-service 디렉토리를 찾을 수 없습니다."
    Write-Host "        경로: $serviceDir"
    Write-Host "        프로젝트 루트에서 실행하고 있는지 확인해주세요."
    exit 1
}

# Python 명령어 존재 확인
Write-Host "[HAEUN] Python 버전 확인 중..."
try {
    $pythonVersion = python --version 2>&1
    Write-Host "[HAEUN] 감지된 Python: $pythonVersion"
} catch {
    Write-Host ""
    Write-Host "[HAEUN] 오류: Python을 찾을 수 없습니다."
    Write-Host "        Python 3.9 이상을 설치하고 PATH에 추가해주세요."
    Write-Host "        다운로드: https://www.python.org/downloads/"
    exit 1
}

# .venv 가상환경 생성 (없을 경우에만)
if (!(Test-Path $venvDir)) {
    Write-Host "[HAEUN] 가상환경 생성 중: $venvDir"
    python -m venv $venvDir
    Write-Host "[HAEUN] 가상환경 생성 완료"
} else {
    Write-Host "[HAEUN] 가상환경이 이미 존재합니다: $venvDir"
}

# Activate.ps1 존재 확인
if (!(Test-Path $activateScript)) {
    Write-Host "[HAEUN] 오류: 가상환경 활성화 스크립트를 찾을 수 없습니다."
    Write-Host "        경로: $activateScript"
    Write-Host "        .venv를 삭제 후 다시 실행해주세요."
    exit 1
}

# 가상환경 활성화
Write-Host "[HAEUN] 가상환경 활성화 중..."
. $activateScript

# pip 최신 버전으로 업그레이드
Write-Host "[HAEUN] pip 업그레이드 중..."
python -m pip install --upgrade pip

# requirements.txt 의존성 설치
Write-Host ""
Write-Host "[HAEUN] 의존성 설치 중 (requirements.txt)..."
Write-Host "[HAEUN] 포함 항목: fastapi, uvicorn, chromadb, sentence-transformers, pydantic"
Write-Host ""
pip install -r $requirementsFile

Write-Host ""
Write-Host "======================================================"
Write-Host " [HAEUN] Python Memory Service 설치 완료!"
Write-Host ""
Write-Host " 다음 명령어로 서비스를 실행하세요:"
Write-Host ""
Write-Host "   Python만 실행:"
Write-Host "   powershell -ExecutionPolicy Bypass -File .\run-python-memory.ps1"
Write-Host ""
Write-Host "   Java + Python 통합 실행:"
Write-Host "   powershell -ExecutionPolicy Bypass -File .\run-haeun-full.ps1"
Write-Host "======================================================"
Write-Host ""
