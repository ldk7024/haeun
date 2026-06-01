# 하은 VRM 모델 에셋 폴더

이 폴더에 하은의 VRM 파일을 배치하면 3D 아바타가 자동으로 활성화됩니다.

## 파일명

  haeun.vrm

이 파일을 이 폴더에 복사하면
Spring Boot 정적 리소스로 /models/haeun.vrm 경로에서 서빙됩니다.

## 지원 모델 형식

  - VRM 0.x (.vrm) — 권장
  - VRM 1.0 (.vrm)
  - GLB (.glb) — 경로 변경 필요: avatar.js 의 CONFIG.modelUrl 수정

## VRM 모델 구하는 방법

1. VRoid Studio 로 직접 제작
   https://vroid.com/studio

2. VRoid Hub 에서 무료 CC 라이선스 모델 다운로드
   https://hub.vroid.com

3. Booth.pm 에서 구매
   https://booth.pm

## 사용 권장 설정 (VRoid Studio 내보내기)

- 폴리곤 수: Medium (권장)
- 표정 블렌드셰이프: Blink / Joy / Sorrow / Surprised / A 포함 여부 확인
- 텍스처 해상도: 1024×1024

## VRM 없을 때 동작

모델 파일이 없으면 자동으로 /images/haeun-portrait.png 또는 SVG 캐릭터가 표시됩니다.
