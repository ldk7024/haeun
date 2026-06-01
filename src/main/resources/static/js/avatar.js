/**
 * avatar.js — 하은 3D 아바타 컨트롤러 (개선판)
 *
 * Three.js + @pixiv/three-vrm 기반
 * VRM 파일이 없거나 로드 실패 시 자동으로 이미지/SVG fallback 표시
 *
 * 주요 개선 사항:
 *   - T-pose 제거: 팔을 자연스럽게 아래로 (1.25 rad ≈ 71도)
 *   - 카메라 재조정: FOV 좁히고 가까이 당겨 얼굴 중심 portrait 구도
 *   - 감정별 고개/몸 자세 변화 추가
 *   - 감정 전환 속도 증가
 *   - 말하기 중 고개 미세 움직임 추가
 */
import * as THREE from 'three';
import { GLTFLoader } from 'three/addons/loaders/GLTFLoader.js';
import { VRMLoaderPlugin, VRMUtils } from '@pixiv/three-vrm';

/* =================================================================
   설정 상수
   ─────────────────────────────────────────────────────────────────
   여기 값만 조정해도 외형이 크게 바뀝니다.
   VRM 모델마다 비율이 다를 수 있으니 조정 가이드 참고.
   ================================================================= */
const CONFIG = {
    /* 모델 경로 */
    modelUrl: '/models/haeun.vrm',

    /*
     * 카메라 설정
     *   cameraFov: 좁을수록 얼굴 왜곡 줄어들고 "당겨보이는" 효과
     *              22° → 얼굴/상반신 portrait 구도
     *   cameraY:   카메라 높이 (VRoid 표준 모델 눈높이 ≈ 1.5m)
     *   cameraZ:   거리. 1.6m → 얼굴·가슴 프레임
     *   lookAtY:   카메라가 향하는 Y좌표 (보통 cameraY - 0.05)
     */
    cameraFov:  22,
    cameraY:    1.52,
    cameraZ:    1.65,
    lookAtY:    1.48,

    /*
     * 모델 위치 오프셋
     *   modelOffsetY: 음수 = 모델 아래로 이동 (발이 잘리게)
     *                 → 얼굴이 캔버스 중심에 오도록 튜닝
     */
    modelOffsetY: -0.10,

    /*
     * 팔 기본 포즈 (T-pose 제거)
     *   VRM 정규화 본에서 Z축 회전이 팔을 올리내림
     *   1.25 rad ≈ 71.6도 → 자연스럽게 옆으로 늘어진 팔
     *   모델에 따라 1.1~1.4 사이에서 조정
     */
    armDropL:  1.25,
    armDropR: -1.25,

    /* 아래팔 (forearm) 자연스러운 굽힘 각도 */
    forearmBendL:  0.12,  // 왼쪽: 약간 안쪽으로
    forearmBendR: -0.12,  // 오른쪽
};

/* =================================================================
   감정별 고개/몸 자세 오프셋 테이블
   ─────────────────────────────────────────────────────────────────
   x: 고개 앞뒤 기울기 (+ = 숙임, - = 들어올림)
   z: 고개 좌우 기울기 (+ = 오른쪽으로, - = 왼쪽으로)
   ySpeed: idle 고개 흔들림 배율 (1.0 = 기본)
   ================================================================= */
const EMOTION_POSE = {
    neutral:  { x: 0.00,  z: 0.00,  ySpeed: 1.0 },
    happy:    { x: -0.04, z: 0.01,  ySpeed: 1.3 },   // 고개 살짝 올림, 활기차게
    sad:      { x: 0.10,  z: 0.04,  ySpeed: 0.5 },   // 고개 숙임, 느리게
    thinking: { x: -0.03, z: -0.06, ySpeed: 0.7 },   // 고개 갸웃 (왼쪽으로)
    comfort:  { x: -0.02, z: 0.02,  ySpeed: 0.9 },
    excited:  { x: -0.06, z: 0.0,   ySpeed: 1.5 },   // 고개 많이 올림, 빠르게
    error:    { x: 0.05,  z: 0.0,   ySpeed: 0.6 },
};

/* =================================================================
   감정별 표정 강도 테이블
   ─────────────────────────────────────────────────────────────────
   VRM 1.0 expression 이름 사용.
   VRM 0.x는 _setExpression 내부에서 이름 변환.
   ================================================================= */
const EMOTION_EXPRESSION = {
    neutral:  {},
    happy:    { happy: 1.0 },
    sad:      { sad: 1.0 },
    thinking: { surprised: 0.35, sad: 0.20 },
    comfort:  { relaxed: 0.90, happy: 0.25 },
    excited:  { happy: 1.0, surprised: 0.50 },
    error:    { sad: 0.60 },
};

/* =================================================================
   HaeunAvatar — 하은의 3D 아바타 컨트롤러
   ================================================================= */
class HaeunAvatar {

    /** @param {HTMLCanvasElement} canvasEl */
    constructor(canvasEl) {
        this.canvas  = canvasEl;
        this.vrm     = null;
        this.clock   = new THREE.Clock();
        this.isReady = false;

        /* idle 시간 누적 */
        this.idleTime = 0;

        /* 감정 상태 */
        this.currentEmotion  = 'neutral';
        this.emotionTargets  = {};
        this.emotionCurrents = {};

        /* 눈 깜빡임 */
        this.blinkTimer    = 0;
        this.blinkInterval = 3 + Math.random() * 3.5;  // 3~6.5초 랜덤
        this.isBlinking    = false;
        this.blinkProgress = 0;

        /* 말하기 */
        this.isSpeaking  = false;
        this.speakPhase  = 0;
        this.speakHeadPh = 0;   // 말할 때 고개 미세 움직임 위상

        /* 시선 추적 */
        this.mouseNX    = 0;
        this.mouseNY    = 0;
        this.headTrackX = 0;
        this.headTrackY = 0;

        this._initScene();
        this._animate();
    }

    /* ---------------------------------------------------------------
       씬 초기화
       --------------------------------------------------------------- */
    _initScene() {
        /* canvas.display:none 상태에서 clientWidth/Height = 0이므로 기본값 사용 */
        const w = this.canvas.clientWidth  || 220;
        const h = this.canvas.clientHeight || 340;

        /* 렌더러 — alpha:true 로 배경 투명 (CSS 배경이 비쳐 보임) */
        this.renderer = new THREE.WebGLRenderer({
            canvas: this.canvas,
            antialias: true,
            alpha: true,
        });
        this.renderer.setPixelRatio(Math.min(window.devicePixelRatio, 2));
        this.renderer.setSize(w, h);
        this.renderer.outputColorSpace = THREE.SRGBColorSpace;

        this.scene = new THREE.Scene();

        /* 카메라 — portrait 구도 */
        this.camera = new THREE.PerspectiveCamera(CONFIG.cameraFov, w / h, 0.1, 20);
        this.camera.position.set(0, CONFIG.cameraY, CONFIG.cameraZ);
        this.camera.lookAt(0, CONFIG.lookAtY, 0);

        /* ── 조명 ──────────────────────────────────────────────────
           ambient  : 전체 기본 조도 (어두운 파란빛)
           keyLight : 좌상단 메인 조명 (밝고 차가운 파란빛)
           rimLight : 뒤에서 캐릭터 윤곽을 감싸는 은은한 빛
           fillLight: 아래에서 얼굴 그림자 완화
           ─────────────────────────────────────────────────────── */
        const ambient = new THREE.AmbientLight(0x2a3c5a, 2.2);
        this.scene.add(ambient);

        const keyLight = new THREE.DirectionalLight(0x99c8ff, 3.0);
        keyLight.position.set(1.5, 3.0, 2.0);
        this.scene.add(keyLight);

        const rimLight = new THREE.DirectionalLight(0x7eb8f7, 1.5);
        rimLight.position.set(-2.5, 1.5, -1.5);
        this.scene.add(rimLight);

        const fillLight = new THREE.DirectionalLight(0xfff0e0, 0.40);
        fillLight.position.set(0.0, -1.0, 1.5);
        this.scene.add(fillLight);

        window.addEventListener('resize', () => this._onResize());
    }

    /* ---------------------------------------------------------------
       VRM 모델 로드
       --------------------------------------------------------------- */
    async loadModel(url = CONFIG.modelUrl) {
        const loader = new GLTFLoader();
        loader.register(parser => new VRMLoaderPlugin(parser));

        try {
            const gltf = await loader.loadAsync(url);
            const vrm  = gltf.userData.vrm;

            if (!vrm) throw new Error('VRM 메타데이터 없음');

            /* VRM 0.x 좌표계 보정 */
            VRMUtils.rotateVRM0(vrm);

            /* 불필요한 관절 제거 (성능) */
            try { VRMUtils.removeUnnecessaryJoints(vrm.scene); } catch (_) {}

            this.vrm = vrm;
            this.vrm.scene.position.y = CONFIG.modelOffsetY;
            this.scene.add(this.vrm.scene);

            this.isReady = true;
            this._onModelLoaded();
            return true;

        } catch (err) {
            console.warn('[HAEUN Avatar] 모델 로드 실패 → fallback:', err.message);
            this._onModelError();
            return false;
        }
    }

    /* 모델 로드 성공: 캔버스 표시 */
    _onModelLoaded() {
        this.canvas.classList.add('avatar-ready');
        const fb = document.getElementById('haeunAvatarFallback');
        if (fb) fb.style.display = 'none';
        /* 캔버스가 보인 직후 실제 크기로 재조정 */
        requestAnimationFrame(() => this._onResize());
    }

    /* 모델 로드 실패: fallback 표시 */
    _onModelError() {
        this.canvas.style.display = 'none';
        const fb = document.getElementById('haeunAvatarFallback');
        if (fb) { fb.style.display = 'block'; fb.style.visibility = 'visible'; }
    }

    /* ---------------------------------------------------------------
       공개 API
       --------------------------------------------------------------- */

    /**
     * 감정 설정
     * @param {'neutral'|'happy'|'sad'|'thinking'|'comfort'|'excited'|'error'} emotion
     * @param {number} intensity 0~1 (기본 1.0)
     */
    setEmotion(emotion, intensity = 1.0) {
        this.currentEmotion = emotion;
        const preset = EMOTION_EXPRESSION[emotion] ?? {};
        /* intensity 배율 적용 */
        this.emotionTargets = {};
        for (const [k, v] of Object.entries(preset)) {
            this.emotionTargets[k] = v * intensity;
        }
    }

    /** @param {boolean} isSpeaking */
    setSpeaking(isSpeaking) {
        this.isSpeaking  = isSpeaking;
        if (!isSpeaking) {
            this.speakPhase  = 0;
            this.speakHeadPh = 0;
        }
    }

    /**
     * 마우스 정규화 좌표 전달
     * @param {number} nx -1(왼)~1(오른)
     * @param {number} ny -1(위)~1(아래)
     */
    lookAtCursor(nx, ny) {
        this.mouseNX = nx;
        this.mouseNY = ny;
    }

    /* ---------------------------------------------------------------
       메인 업데이트
       --------------------------------------------------------------- */
    update(delta) {
        if (!this.vrm) return;
        this.idleTime += delta;

        this._updateHeadTracking(delta);
        this._updatePose();
        this._updateBlink(delta);
        this._updateSpeaking(delta);
        this._updateEmotions(delta);

        this.vrm.update(delta);
    }

    /* ---------------------------------------------------------------
       시선 추적 — 마우스를 향해 부드럽게 보간
       --------------------------------------------------------------- */
    _updateHeadTracking(delta) {
        /* 지수 감쇠: 빠른 초기 반응, 느린 마무리 */
        const k = 1 - Math.pow(0.03, delta);
        this.headTrackX += (this.mouseNX * 0.15 - this.headTrackX) * k;
        this.headTrackY += (-this.mouseNY * 0.08 - this.headTrackY) * k;
    }

    /* ---------------------------------------------------------------
       전신 포즈 업데이트
       ─────────────────────────────────────────────────────────────
       T-pose 제거 + idle motion + 감정별 자세 + 시선 합산
       매 프레임 절댓값으로 세팅 (누적 없음)
       --------------------------------------------------------------- */
    _updatePose() {
        if (!this.vrm.humanoid) return;
        const t  = this.idleTime;
        const ep = EMOTION_POSE[this.currentEmotion] ?? EMOTION_POSE.neutral;

        /* ── 척추 (spine) ─────────────────────────────────────────
           숨쉬기(x축)와 좌우 흔들림(z축) */
        const spine = this.vrm.humanoid.getNormalizedBoneNode('spine');
        if (spine) {
            spine.rotation.x = Math.sin(t * 0.50) * 0.010 + 0.005;
            spine.rotation.z = Math.sin(t * 0.30) * 0.012;
        }

        /* ── 상체 (chest) ───────────────────────────────────────── */
        const chest = this.vrm.humanoid.getNormalizedBoneNode('chest');
        if (chest) {
            chest.rotation.z = Math.sin(t * 0.30 + 0.9) * 0.006;
            chest.rotation.x = Math.sin(t * 0.45) * 0.005;
        }

        /* ── 목 (neck) ──────────────────────────────────────────── */
        const neck = this.vrm.humanoid.getNormalizedBoneNode('neck');
        if (neck) {
            neck.rotation.x = Math.sin(t * 0.42) * 0.007;
        }

        /* ── 머리 (head) ─────────────────────────────────────────
           idle 움직임 + 감정 자세 + 시선 tracking 합산
           ep.ySpeed: 감정별 고개 움직임 배율 */
        const head = this.vrm.humanoid.getNormalizedBoneNode('head');
        if (head) {
            const idleY  = Math.sin(t * 0.27 * ep.ySpeed) * 0.012;
            const idleX  = Math.sin(t * 0.58 * ep.ySpeed) * 0.008;
            const speakX = this.isSpeaking
                ? Math.sin(this.speakHeadPh) * 0.008   // 말할 때 고개 미세 움직임
                : 0;

            head.rotation.y = idleY + this.headTrackX;
            head.rotation.x = idleX + this.headTrackY + ep.x + speakX;
            head.rotation.z = Math.sin(t * 0.36) * 0.005 + ep.z;
        }

        /* ── 왼팔 (leftUpperArm) ─────────────────────────────────
           CONFIG.armDropL(1.25 rad ≈ 71°)로 T-pose 제거
           idle 흔들림은 ±0.015 rad만 추가 */
        const lArm = this.vrm.humanoid.getNormalizedBoneNode('leftUpperArm');
        if (lArm) {
            lArm.rotation.z = CONFIG.armDropL + Math.sin(t * 0.22) * 0.015;
            lArm.rotation.x = Math.sin(t * 0.17) * 0.010;
        }

        /* ── 오른팔 (rightUpperArm) ─────────────────────────────── */
        const rArm = this.vrm.humanoid.getNormalizedBoneNode('rightUpperArm');
        if (rArm) {
            rArm.rotation.z = CONFIG.armDropR + Math.sin(t * 0.22 + Math.PI) * 0.015;
            rArm.rotation.x = Math.sin(t * 0.17 + Math.PI) * 0.010;
        }

        /* ── 아래팔 (forearm): 자연스러운 굽힘 ─────────────────── */
        const lForearm = this.vrm.humanoid.getNormalizedBoneNode('leftLowerArm');
        if (lForearm) {
            lForearm.rotation.y = CONFIG.forearmBendL;
        }
        const rForearm = this.vrm.humanoid.getNormalizedBoneNode('rightLowerArm');
        if (rForearm) {
            rForearm.rotation.y = CONFIG.forearmBendR;
        }
    }

    /* ---------------------------------------------------------------
       눈 깜빡임
       --------------------------------------------------------------- */
    _updateBlink(delta) {
        this.blinkTimer += delta;

        if (!this.isBlinking && this.blinkTimer >= this.blinkInterval) {
            this.isBlinking    = true;
            this.blinkTimer    = 0;
            this.blinkProgress = 0;
            this.blinkInterval = 3 + Math.random() * 3.5;
        }

        if (!this.isBlinking) return;

        /* 눈 감기(0→1) → 뜨기(1→0), 0.13초 */
        const DUR = 0.13;
        this.blinkProgress += delta / DUR;

        let v;
        if (this.blinkProgress < 0.45) {
            v = this.blinkProgress / 0.45;
        } else if (this.blinkProgress < 1.0) {
            v = 1.0 - (this.blinkProgress - 0.45) / 0.55;
        } else {
            v = 0;
            this.isBlinking = false;
        }

        this._setExpression('blink', Math.min(1, v));
    }

    /* ---------------------------------------------------------------
       입 움직임 — 말할 때 aa 모프 + 고개 미세 흔들림
       --------------------------------------------------------------- */
    _updateSpeaking(delta) {
        if (!this.isSpeaking) {
            this._setExpression('aa', 0);
            return;
        }

        this.speakPhase  += delta * 6.5;
        this.speakHeadPh += delta * 4.0;

        /* sin * cos 조합으로 불규칙한 입 열림/닫힘 */
        const raw  = Math.sin(this.speakPhase) * Math.cos(this.speakPhase * 1.7 + 0.5);
        const open = Math.pow(Math.abs(raw), 0.65) * 0.60;
        this._setExpression('aa', open);
    }

    /* ---------------------------------------------------------------
       감정 블렌드 — 목표값으로 빠르게 보간
       ─────────────────────────────────────────────────────────────
       k = 1 - Math.pow(0.001, delta) → 약 0.11/frame @ 60fps
       → 감정 변화가 1초 이내에 완료
       --------------------------------------------------------------- */
    _updateEmotions(delta) {
        const EXPRS = ['happy', 'sad', 'surprised', 'relaxed', 'angry'];
        /* 0.001 base → 0.01 base보다 ~50% 빠른 전환 */
        const k = 1 - Math.pow(0.001, delta);

        for (const name of EXPRS) {
            const target  = this.emotionTargets[name]  ?? 0;
            const current = this.emotionCurrents[name] ?? 0;
            const next    = current + (target - current) * k;
            this.emotionCurrents[name] = next;
            this._setExpression(name, Math.max(0, Math.min(1, next)));
        }
    }

    /* ---------------------------------------------------------------
       Expression 설정 — VRM 0.x / 1.0 자동 전환
       --------------------------------------------------------------- */
    _setExpression(name, value) {
        if (!this.vrm) return;

        /* VRM 1.0 */
        if (this.vrm.expressionManager) {
            try { this.vrm.expressionManager.setValue(name, value); } catch (_) {}
            return;
        }

        /* VRM 0.x — 이름 변환 필요 */
        if (this.vrm.blendShapeProxy) {
            const MAP = {
                blink:     'Blink',
                happy:     'Joy',
                sad:       'Sorrow',
                surprised: 'Surprised',
                aa:        'A',
                relaxed:   'Fun',
                angry:     'Angry',
            };
            const n = MAP[name];
            if (n) try { this.vrm.blendShapeProxy.setValue(n, value); } catch (_) {}
        }
    }

    /* ---------------------------------------------------------------
       RAF 루프
       --------------------------------------------------------------- */
    _animate() {
        requestAnimationFrame(() => this._animate());
        /* delta 상한: 탭 비활성 복귀 시 큰 값으로 튀는 것 방지 */
        const delta = Math.min(this.clock.getDelta(), 0.05);
        this.update(delta);
        this.renderer.render(this.scene, this.camera);
    }

    /* 리사이즈 */
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
   텍스트 감정 판별
   ================================================================= */
function detectEmotion(text) {
    if (!text) return 'neutral';
    if (/힘들|슬프|우울|괴롭|지쳐|아파|힘드/.test(text))        return 'sad';
    if (/고마|감사|좋아|기쁘|행복|ㅋㅋ|ㅎㅎ|😊|❤/.test(text)) return 'happy';
    if (/왜|어떻게|모르겠|이해가|궁금|모르/.test(text))          return 'thinking';
    if (/대박|멋지|최고|진짜!|!!/.test(text))                     return 'excited';
    if (/괜찮|걱정|힘내|응원|안심/.test(text))                    return 'comfort';
    return 'neutral';
}

window.detectEmotion = detectEmotion;

/* =================================================================
   VRM 3D — Experimental 모드
   ─────────────────────────────────────────────────────────────────
   기본값: false → portrait 이미지 시스템 사용
   VRM 3D 활성화: 아래 VRM_ENABLED = true 로 변경

   주의: VRoid Studio 모델은 애니/버튜버 감성이 강합니다.
         프로젝트 방향(반실사 안드로이드)과 맞지 않을 수 있으므로
         experimental 기능으로 분리합니다.
   ================================================================= */
const VRM_ENABLED = false;

document.addEventListener('DOMContentLoaded', () => {
    const canvas = document.getElementById('haeunAvatarCanvas');
    if (!canvas) return;

    /* HaeunAvatar 인스턴스 (setEmotion/setSpeaking API 사용을 위해 항상 생성) */
    const avatar = new HaeunAvatar(canvas);

    /* 전역 노출 — app.js에서 window.haeunAvatar.setEmotion() 호출 가능 */
    window.haeunAvatar = avatar;

    /* VRM 활성 시에만 모델 로드
       mousemove 시선 추적은 app.js initPortraitSystem()에서 통합 처리 */
    if (VRM_ENABLED) {
        console.info('[HAEUN] VRM 3D 모드 활성화 → 모델 로드 중...');
        avatar.loadModel();
    } else {
        console.info('[HAEUN] Portrait 이미지 모드 (기본). VRM 3D: VRM_ENABLED=true 로 변경 가능.');
    }
});
