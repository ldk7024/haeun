# ================================================================
# HAEUN 프로젝트 실행 스크립트
# 현재 PowerShell 세션에서만 Java 17을 임시로 사용합니다.
# 시스템 환경변수는 변경하지 않습니다.
# ================================================================

# 현재 세션의 JAVA_HOME을 JDK 17 경로로 설정
$env:JAVA_HOME = "C:\Program Files\Eclipse Adoptium\jdk-17.0.19.10-hotspot"

# 현재 세션의 PATH 맨 앞에 JDK 17 bin을 추가 (기존 Java보다 우선 적용)
$env:Path = "$env:JAVA_HOME\bin;$env:Path"

# 적용된 Java 버전 확인
Write-Host "현재 이 PowerShell 세션에서만 Java 17을 사용합니다."
Write-Host "JAVA_HOME = $env:JAVA_HOME"

java -version

# HAEUN 프로젝트 실행
.\gradlew.bat bootRun
