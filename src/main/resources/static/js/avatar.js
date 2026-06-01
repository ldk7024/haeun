/**
 * avatar.js — 하은 3D 아바타 컨트롤러
 *
 * Three.js + @pixiv/three-vrm 기반
 * VRM 파일이 없거나 로드 실패 시 자동으로 이미지/SVG fallback 표시
 *
 * 사용법:
 *   모델 파일을 /models/haeun.vrm 에 배치하면 자동 적용
 *   모델이 없으면 기존 portrait 이미지 or SVG가 표시됨
 */
import * as THREE from 'three';
import { GLTFLoader } from 'three/addons/loaders/GLTFLoader.js';
import { VRMLoaderPlugin, VRMUtils } from '@pixiv/three-vrm';

/* =================================================================
   설정 상수 — 모델 경로나 카메라 위치만 바꾸면 바로 적용됩니다
   ================================================================= */
const CONFIG = {
    modelUrl:       '/models/haeun.vrm',   // VRM 모델 경로 (GLB도 가능)
    cameraFov:      28,                    // 카메라 화각 (좁을수록 왜곡 없음)
    cameraY:        1.38,                  // 카메라 높이 (가슴~얼굴 프레임)
    cameraZ:        2.5,                   // 카메라 거리
    modelOffsetY:  -0.28,                  // 모델 Y 오프셋 (캔버스에 맞게 조정)
};

/* =================================================================
   HaeunAvatar — 하은의 3D 아바타 컨트롤러 클래스
   ================================================================= */
class HaeunAvatar {

    /**
     * @param {HTMLCanvasElement} canvasEl 렌더링할 canvas 요소
     */
    constructor(canvasEl) {
        this.canvas   = canvasEl;
        this.vrm      = null;
        this.clock    = new THREE.Clock();
        this.isReady  = false;

        /* --- idle 시간 누적 --- */
        this.idleTime = 0;

        /* --- 눈 깜빡임 상태 --- */
        this.blinkTimer    = 0;
        this.blinkInterval = 3.5 + Math.random() * 3;  // 3.5~6.5초 랜덤
        this.isBlinking    = false;
        this.blinkProgress = 0;

        /* --- 말하기 상태 --- */
        this.isSpeaking = false;
        this.speakPhase = 0;

        /* --- 시선 추적 (부드러운 보간용) --- */
        this.mouseNX    = 0;   // 마우스 정규화 X (-1 ~ 1)
        this.mouseNY    = 0;   // 마우스 정규화 Y (-1 ~ 1)
        this.headTrackX = 0;   // 보간된 시선 X
        this.headTrackY = 0;   // 보간된 시선 Y

        /* --- 감정 시스템 (목표값 / 현재값) --- */
        this.emotionTargets  = {};
        this.emotionCurrents = {};

        this._initScene();
        this._animate();
    }

    /* ---------------------------------------------------------------
       씬 초기화 (renderer / camera / scene / lighting)
       --------------------------------------------------------------- */
    _initScene() {
        const w = this.canvas.clientWidth  || 224;
        const h = this.canvas.clientHeight || 340;

        /* 렌더러 — 배경 투명 (CSS 배경과 합성) */
        this.renderer = new THREE.WebGLRenderer({
            canvas: this.canvas,
            antialias: true,
            alpha: true,
        });
        this.renderer.setPixelRatio(Math.min(window.devicePixelRatio, 2));
        this.renderer.setSize(w, h);
        this.renderer.outputColorSpace = THREE.SRGBColorSpace;

        /* 씬 */
        this.scene = new THREE.Scene();

        /* 카메라 */
        this.camera = new THREE.PerspectiveCamera(CONFIG.cameraFov, w / h, 0.1, 20);
        this.camera.position.set(0, CONFIG.cameraY, CONFIG.cameraZ);
        this.camera.lookAt(0, CONFIG.cameraY - 0.05, 0);

        /* 조명 — HAEUN 세계관: 차가운 블루 + 은은한 림라이트
           키라이트 (메인 조명): 좌상단에서 파란빛
           림라이트: 뒤에서 캐릭터 윤곽을 은빛으로 감쌈
           필라이트: 아래에서 약하게 올라와 얼굴 그림자 완화 */
        const ambient = new THREE.AmbientLight(0x2a3c5a, 2.0);
        this.scene.add(ambient);

        const keyLight = new THREE.DirectionalLight(0x99c8ff, 2.8);
        keyLight.position.set(1.5, 3.0, 2.0);
        this.scene.add(keyLight);

        const rimLight = new THREE.DirectionalLight(0x7eb8f7, 1.4);
        rimLight.position.set(-2.5, 1.5, -1.5);
        this.scene.add(rimLight);

        const fillLight = new THREE.DirectionalLight(0xfff0e0, 0.35);
        fillLight.position.set(0.0, -1.0, 1.5);
        this.scene.add(fillLight);

        /* 리사이즈 대응 */
        window.addEventListener('resize', () => this._onResize());
    }

    /* ---------------------------------------------------------------
       VRM / GLB 모델 로드
       url이 없으면 CONFIG.modelUrl 사용
       --------------------------------------------------------------- */
    async loadModel(url = CONFIG.modelUrl) {
        const loader = new GLTFLoader();
        loader.register(parser => new VRMLoaderPlugin(parser));

        try {
            const gltf = await loader.loadAsync(url);
            const vrm  = gltf.userData.vrm;

            if (!vrm) throw new Error('VRM 메타데이터 없음 — VRM 파일이 맞는지 확인하세요.');

            /* VRM 0.x 좌표계 보정 (Y축이 뒤집히는 문제 수정) */
            VRMUtils.rotateVRM0(vrm);

            /* 불필요한 관절 정리 (성능 최적화) */
            try { VRMUtils.removeUnnecessaryJoints(vrm.scene); } catch (_) {}

            this.vrm = vrm;
            this.vrm.scene.position.y = CONFIG.modelOffsetY;
            this.scene.add(this.vrm.scene);

            this.isReady = true;
            this._onModelLoaded();
            return true;

        } catch (err) {
            console.warn('[HAEUN Avatar] 3D 모델 로드 실패. fallback 표시 →', err.message);
            this._onModelError();
            return false;
        }
    }

    /* 모델 로드 성공: 캔버스 표시, fallback 숨김 */
    _onModelLoaded() {
        this.canvas.classList.add('avatar-ready');
        const fb = document.getElementById('haeunAvatarFallback');
        if (fb) fb.style.display = 'none';
    }

    /* 모델 로드 실패: 캔버스 숨김, fallback 표시 */
    _onModelError() {
        this.canvas.style.display = 'none';
        const fb = document.getElementById('haeunAvatarFallback');
        if (fb) { fb.style.display = 'block'; fb.style.visibility = 'visible'; }
    }

    /* ---------------------------------------------------------------
       공개 API — app.js 에서 호출
       --------------------------------------------------------------- */

    /**
     * 감정 표정 설정
     * @param {'neutral'|'happy'|'sad'|'thinking'|'comfort'|'excited'} emotion
     * @param {number} intensity 0.0 ~ 1.0
     */
    setEmotion(emotion, intensity = 0.9) {
        /* VRM 1.0 expression 이름 매핑표 */
        const presets = {
            neutral:  {},
            happy:    { happy: intensity * 0.85 },
            sad:      { sad:   intensity * 0.70 },
            thinking: { surprised: intensity * 0.22, sad: intensity * 0.12 },
            comfort:  { relaxed:   intensity * 0.60 },
            excited:  { happy: intensity * 1.0, surprised: intensity * 0.30 },
        };
        this.emotionTargets = presets[emotion] ?? {};
    }

    /**
     * 말하기 On/Off
     * @param {boolean} isSpeaking
     */
    setSpeaking(isSpeaking) {
        this.isSpeaking = isSpeaking;
        if (!isSpeaking) this.speakPhase = 0;
    }

    /**
     * 마우스 정규화 좌표 전달 (mousemove에서 호출)
     * @param {number} nx  -1 (왼쪽) ~ 1 (오른쪽)
     * @param {number} ny  -1 (위) ~ 1 (아래)
     */
    lookAtCursor(nx, ny) {
        this.mouseNX = nx;
        this.mouseNY = ny;
    }

    /* ---------------------------------------------------------------
       메인 업데이트 루프
       --------------------------------------------------------------- */
    update(delta) {
        if (!this.vrm) return;
        this.idleTime += delta;

        /* 업데이트 순서 중요: tracking → idle(tracking 값 사용) → 나머지 */
        this._updateHeadTracking(delta);
        this._updateIdle();
        this._updateBlink(delta);
        this._updateSpeaking(delta);
        this._updateEmotions(delta);

        this.vrm.update(delta);
    }

    /* ---------------------------------------------------------------
       시선 추적 — 마우스 향해 부드럽게 보간
       --------------------------------------------------------------- */
    _updateHeadTracking(delta) {
        /* 지수 감쇠 보간: 매 프레임 일정 비율로 목표에 접근 */
        const k = 1 - Math.pow(0.04, delta);
        this.headTrackX += (this.mouseNX * 0.16 - this.headTrackX) * k;
        this.headTrackY += (-this.mouseNY * 0.09 - this.headTrackY) * k;
    }

    /* ---------------------------------------------------------------
       Idle 모션 — 숨쉬기 + 미세 흔들림 + 시선 tracking 합산
       매 프레임 회전값을 직접 세팅 (누적 안 됨)
       --------------------------------------------------------------- */
    _updateIdle() {
        if (!this.vrm.humanoid) return;
        const t = this.idleTime;

        /* 척추 (spine): 숨쉬기 + 좌우 흔들림 */
        const spine = this.vrm.humanoid.getNormalizedBoneNode('spine');
        if (spine) {
            spine.rotation.x = Math.sin(t * 0.50) * 0.010 + 0.004;  // 앞뒤 (숨쉬기)
            spine.rotation.z = Math.sin(t * 0.32) * 0.013;           // 좌우
        }

        /* 상체 (chest): 척추 반대로 미세 움직임 */
        const chest = this.vrm.humanoid.getNormalizedBoneNode('chest');
        if (chest) {
            chest.rotation.z = Math.sin(t * 0.32 + 1.0) * 0.006;
            chest.rotation.x = Math.sin(t * 0.46) * 0.005;
        }

        /* 목 (neck): 끄덕임 */
        const neck = this.vrm.humanoid.getNormalizedBoneNode('neck');
        if (neck) {
            neck.rotation.x = Math.sin(t * 0.43) * 0.008;
        }

        /* 머리 (head): idle + 시선 tracking 합산 */
        const head = this.vrm.humanoid.getNormalizedBoneNode('head');
        if (head) {
            head.rotation.y = Math.sin(t * 0.27) * 0.012 + this.headTrackX;
            head.rotation.x = Math.sin(t * 0.60) * 0.008 + this.headTrackY;
            head.rotation.z = Math.sin(t * 0.38) * 0.005;
        }

        /* 왼팔 (leftUpperArm): 미세 흔들림 */
        const lArm = this.vrm.humanoid.getNormalizedBoneNode('leftUpperArm');
        if (lArm) {
            lArm.rotation.z = Math.sin(t * 0.25) * 0.008 + 0.06;
        }
        const rArm = this.vrm.humanoid.getNormalizedBoneNode('rightUpperArm');
        if (rArm) {
            rArm.rotation.z = Math.sin(t * 0.25 + Math.PI) * 0.008 - 0.06;
        }
    }

    /* ---------------------------------------------------------------
       눈 깜빡임 — 일정 랜덤 간격으로 자연스럽게
       --------------------------------------------------------------- */
    _updateBlink(delta) {
        this.blinkTimer += delta;

        /* 깜빡임 시작 */
        if (!this.isBlinking && this.blinkTimer >= this.blinkInterval) {
            this.isBlinking    = true;
            this.blinkTimer    = 0;
            this.blinkProgress = 0;
            /* 다음 깜빡임 간격 랜덤 설정 */
            this.blinkInterval = 3 + Math.random() * 4;
        }

        if (!this.isBlinking) return;

        /* 깜빡임 애니메이션: 눈 감기(0→1) → 뜨기(1→0), 총 0.13초 */
        const BLINK_DURATION = 0.13;
        this.blinkProgress  += delta / BLINK_DURATION;

        let blinkVal;
        if (this.blinkProgress < 0.45) {
            blinkVal = this.blinkProgress / 0.45;          // 감기
        } else if (this.blinkProgress < 1.0) {
            blinkVal = 1.0 - (this.blinkProgress - 0.45) / 0.55; // 뜨기
        } else {
            blinkVal = 0;
            this.isBlinking = false;
        }

        this._setExpression('blink', Math.min(1, blinkVal));
    }

    /* ---------------------------------------------------------------
       입 움직임 — 말할 때 aa 모프 반복 (lip sync 시뮬레이션)
       --------------------------------------------------------------- */
    _updateSpeaking(delta) {
        if (!this.isSpeaking) {
            this._setExpression('aa', 0);
            return;
        }
        /* 불규칙한 입 움직임으로 자연스러운 발화 느낌 */
        this.speakPhase += delta * 6.5;
        const raw  = Math.sin(this.speakPhase) * Math.cos(this.speakPhase * 1.7 + 0.5);
        const open = Math.pow(Math.abs(raw), 0.65) * 0.55;
        this._setExpression('aa', open);
    }

    /* ---------------------------------------------------------------
       감정 블렌드 — 목표값으로 부드럽게 보간
       --------------------------------------------------------------- */
    _updateEmotions(delta) {
        /* 지원 expression 이름 목록 */
        const EXPRESSIONS = ['happy', 'sad', 'surprised', 'relaxed', 'angry'];
        const k = 1 - Math.pow(0.01, delta);  // 보간 속도

        for (const name of EXPRESSIONS) {
            const target  = this.emotionTargets[name]  ?? 0;
            const current = this.emotionCurrents[name] ?? 0;
            const next    = current + (target - current) * k;
            this.emotionCurrents[name] = next;
            this._setExpression(name, Math.max(0, next));
        }
    }

    /* ---------------------------------------------------------------
       Expression 값 설정 — VRM 0.x / 1.0 양쪽 대응
       --------------------------------------------------------------- */
    _setExpression(name, value) {
        if (!this.vrm) return;

        /* VRM 1.0: expressionManager */
        if (this.vrm.expressionManager) {
            try { this.vrm.expressionManager.setValue(name, value); } catch (_) {}
            return;
        }

        /* VRM 0.x: blendShapeProxy (이름 변환 필요) */
        if (this.vrm.blendShapeProxy) {
            const nameMap = {
                blink:     'Blink',
                happy:     'Joy',
                sad:       'Sorrow',
                surprised: 'Surprised',
                aa:        'A',
                relaxed:   'Fun',
                angry:     'Angry',
            };
            const n = nameMap[name];
            if (n) try { this.vrm.blendShapeProxy.setValue(n, value); } catch (_) {}
        }
    }

    /* ---------------------------------------------------------------
       requestAnimationFrame 루프
       --------------------------------------------------------------- */
    _animate() {
        requestAnimationFrame(() => this._animate());
        /* delta 상한 설정: 탭 비활성 시 큰 delta로 애니메이션 튀는 것 방지 */
        const delta = Math.min(this.clock.getDelta(), 0.05);
        this.update(delta);
        this.renderer.render(this.scene, this.camera);
    }

    /* 리사이즈 처리 */
    _onResize() {
        const w = this.canvas.clientWidth;
        const h = this.canvas.clientHeight;
        if (!w || !h) return;
        this.camera.aspect = w / h;
        this.camera.updateProjectionMatrix();
        this.renderer.setSize(w, h);
    }
}

/* =================================================================
   텍스트 감정 판별 — 사용자/하은 메시지에서 감정 추론
   ================================================================= */
function detectEmotion(text) {
    if (!text) return 'neutral';
    if (/힘들|슬프|우울|괴롭|지쳐|아파|힘드/.test(text))         return 'sad';
    if (/고마|감사|좋아|기쁘|행복|ㅋㅋ|ㅎㅎ|😊|❤/.test(text))  return 'happy';
    if (/왜|어떻게|모르겠|이해가|궁금|모르/.test(text))           return 'thinking';
    if (/대박|멋지|최고|진짜!|!!/.test(text))                      return 'excited';
    if (/괜찮|걱정|힘내|응원|안심/.test(text))                     return 'comfort';
    return 'neutral';
}

/* =================================================================
   전역으로 노출 — app.js (일반 스크립트) 에서 window.haeunAvatar로 접근
   ================================================================= */
window.detectEmotion = detectEmotion;

/* =================================================================
   자동 초기화 — DOMContentLoaded 이후 실행
   ================================================================= */
document.addEventListener('DOMContentLoaded', () => {
    const canvas = document.getElementById('haeunAvatarCanvas');
    if (!canvas) return;

    /* HaeunAvatar 인스턴스 생성 */
    const avatar = new HaeunAvatar(canvas);

    /* 마우스 이동 → 시선 추적 */
    document.addEventListener('mousemove', (e) => {
        const nx =  (e.clientX / window.innerWidth)  * 2 - 1;
        const ny = -(e.clientY / window.innerHeight) * 2 + 1;  // Y 반전
        avatar.lookAtCursor(nx, ny);
    });

    /* 모델 로드 시도 */
    avatar.loadModel();

    /* 전역으로 노출 (app.js에서 window.haeunAvatar로 접근) */
    window.haeunAvatar = avatar;
});
