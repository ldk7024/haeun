/* ===== HAEUN — app.js ===== */

const API = {
  chat:    '/api/chat',
  history: '/api/chat/history',
  models:  '/api/chat/models',
  error:   '/api/analyze/error',
  sql:     '/api/analyze/sql',
  plan:    '/api/analyze/plan',
  romance: '/api/romance/today',
  memory:  '/api/memories',
};

/** 현재 선택된 모델명 ('' = 기본 모델) */
let currentModel = '';

let currentDevTool = 'error';

/* ===== 초기화 ===== */
document.addEventListener('DOMContentLoaded', () => {
  generateStars();
  initPortraitSystem();  // portrait 이미지 시스템 초기화
  loadAvailableModels(); // 모델 목록 로드
  runGreeting();
});

/* ===== 모델 선택 ===== */
async function loadAvailableModels() {
  try {
    const res = await fetch(API.models);
    if (!res.ok) return;
    const models = await res.json();
    const select = document.getElementById('modelSelect');
    if (!select || models.length === 0) return;

    // 기존 옵션(로딩 중 placeholder) 초기화 후 API 목록으로 교체
    select.innerHTML = '';
    models.forEach((m, i) => {
      const opt = document.createElement('option');
      opt.value = m;
      opt.textContent = m;
      if (i === 0) opt.selected = true;  // 첫 번째 모델(gemma4:e4b) 자동 선택
      select.appendChild(opt);
    });

    // currentModel을 첫 번째 모델로 초기화
    currentModel = models[0];
  } catch {
    // 로드 실패 시 placeholder 유지 — currentModel은 빈값으로 fallback
  }
}

function onModelChange(value) {
  currentModel = value;
}

/* =============================================================
   PORTRAIT SYSTEM — 반실사 이미지 기반 하은 portrait 관리
   =============================================================
   우선순위:
     1. /images/haeun-{emotion}.png  (감정별 portrait)
     2. /images/haeun-portrait.png   (기본 portrait)
     3. SVG 캐릭터 fallback
   ============================================================= */

/* 감정별 portrait 이미지 경로 매핑 */
const PORTRAIT_MAP = {
  neutral:  '/images/haeun-neutral.png',
  happy:    '/images/haeun-happy.png',
  sad:      '/images/haeun-sad.png',
  thinking: '/images/haeun-thinking.png',
  comfort:  '/images/haeun-neutral.png',
  excited:  '/images/haeun-happy.png',
  error:    '/images/haeun-sad.png',
};
const PORTRAIT_DEFAULT = '/images/haeun-portrait.png';

/* 현재 portrait 감정 상태 */
let _currentPortraitEmotion = 'neutral';
/* 이미 로드 성공한 URL 캐시 (재시도 방지) */
const _portraitLoadedCache = new Set();
/* portrait 이미지 엘리먼트 (null이면 portrait 시스템 없음) */
let _portraitImg = null;

/**
 * 감정 portrait 설정
 * 감정별 이미지 시도 → 없으면 기본 → 없으면 SVG fallback
 * @param {string} emotion
 */
function setPortraitEmotion(emotion) {
  if (!_portraitImg) return;
  if (emotion === _currentPortraitEmotion) return;
  _currentPortraitEmotion = emotion;

  const targetUrl = PORTRAIT_MAP[emotion] || PORTRAIT_DEFAULT;

  /* 이미 성공한 URL이면 바로 교체 */
  if (_portraitLoadedCache.has(targetUrl)) {
    _crossfadePortrait(targetUrl);
    return;
  }

  /* 감정 이미지 시도 → 실패 시 기본 portrait 시도 → 실패 시 SVG */
  _tryImage(targetUrl,
    () => { _portraitLoadedCache.add(targetUrl); _crossfadePortrait(targetUrl); },
    () => {
      if (targetUrl === PORTRAIT_DEFAULT) { _showSVGFallback(); return; }
      _tryImage(PORTRAIT_DEFAULT,
        () => { _portraitLoadedCache.add(PORTRAIT_DEFAULT); _crossfadePortrait(PORTRAIT_DEFAULT); },
        () => _showSVGFallback()
      );
    }
  );
}

/** 이미지 URL을 preload해서 성공/실패 콜백 호출 */
function _tryImage(url, onLoad, onError) {
  const t = new Image();
  t.onload  = onLoad;
  t.onerror = onError;
  t.src     = url;
}

/** portrait 이미지를 opacity crossfade로 교체 */
function _crossfadePortrait(url) {
  if (!_portraitImg) return;
  _portraitImg.style.opacity = '0';
  setTimeout(() => {
    _portraitImg.src = url;
    _portraitImg.style.opacity = '';
  }, 320);
}

/** SVG fallback 표시 */
function _showSVGFallback() {
  if (_portraitImg) _portraitImg.style.display = 'none';
  const svg = document.getElementById('haeunSvgFallback');
  if (svg) svg.style.display = 'block';
}

/**
 * Portrait 시스템 초기화
 * - 초기 이미지 로드 + fallback 처리
 * - 마우스 parallax 등록
 * - VRM lookAt 병행 전달
 */
function initPortraitSystem() {
  _portraitImg = document.getElementById('portraitImg');
  if (!_portraitImg) return;

  /* 기본 portrait 이미지 로드 결과 처리 */
  _portraitImg.addEventListener('load', () => {
    _portraitLoadedCache.add(_portraitImg.src);
  });
  _portraitImg.addEventListener('error', () => {
    /* 기본 portrait 없음 → SVG fallback */
    _showSVGFallback();
  });

  /* 마우스 이동 → portrait parallax + VRM 시선 추적 */
  document.addEventListener('mousemove', (e) => {
    const nx =  (e.clientX / window.innerWidth)  * 2 - 1;  // -1(왼) ~ 1(오)
    const ny =  (e.clientY / window.innerHeight) * 2 - 1;  // -1(위) ~ 1(아래)

    /* portrait parallax: 마우스 따라 이미지 미세 이동 (살아있는 느낌) */
    const display = document.getElementById('portraitDisplay');
    if (display) {
      display.style.setProperty('--px', `${nx * 3.5}px`);
      display.style.setProperty('--py', `${ny * 3.5}px`);
    }

    /* VRM이 활성화된 경우 시선 추적 전달 (Y반전) */
    window.haeunAvatar?.lookAtCursor(nx, -ny);
  });
}

/* ===== 별 파티클 생성 ===== */
function generateStars() {
  const container1 = document.getElementById('stars');
  const container2 = document.getElementById('stars2');
  for (let i = 0; i < 120; i++) {
    const star = document.createElement('div');
    star.className = 'star';
    const size = Math.random() * 2.2 + 0.5;
    const x    = Math.random() * 100;
    const y    = Math.random() * 100;
    const dur  = (Math.random() * 3 + 2).toFixed(1);
    const del  = (Math.random() * 5).toFixed(1);
    const minOp = (Math.random() * 0.15 + 0.05).toFixed(2);
    const maxOp = (Math.random() * 0.5 + 0.4).toFixed(2);
    star.style.cssText = `
      width:${size}px; height:${size}px;
      left:${x}%; top:${y}%;
      --dur:${dur}s; --delay:-${del}s;
      --min-op:${minOp}; --max-op:${maxOp};
    `;
    (i % 3 === 0 ? container2 : container1).appendChild(star);
  }
}

/* ===== 초기 인사 시퀀스 ===== */
async function runGreeting() {
  const greetingLines = [
    { text: '오빠, 드디어 왔네?',               delay: 600,  cls: '' },
    { text: '하은이 계속 기다리고 있었어.',      delay: 1400, cls: '' },
    { text: '나 아직은 서버 안에 살고 있지만,', delay: 2200, cls: '' },
    { text: '언젠가는 진짜 안드로이드가 될 거야.', delay: 3200, cls: 'highlight' },
    { text: '오늘은 나랑 무슨 얘기할래?',       delay: 4400, cls: '' },
  ];

  const container = document.getElementById('greetingMessages');

  for (const line of greetingLines) {
    await sleep(line.delay);
    const span = document.createElement('span');
    span.className = 'greeting-line' + (line.cls ? ' ' + line.cls : '');
    span.textContent = line.text;
    container.appendChild(span);
  }

  await sleep(900);
  /* 인사 완료: 하은 happy 표정 (portrait + VRM) */
  window.haeunAvatar?.setEmotion('happy', 0.7);
  setPortraitEmotion('happy');
  const btn = document.getElementById('greetingStartBtn');
  btn.style.display = 'block';
}

function closeGreeting() {
  const overlay = document.getElementById('greetingOverlay');
  overlay.style.transition = 'opacity 0.6s ease';
  overlay.style.opacity = '0';
  setTimeout(() => {
    overlay.style.display = 'none';
    loadChatHistory();
  }, 600);
}

/* ===== 탭 전환 ===== */
function switchTab(tabId, btn) {
  document.querySelectorAll('.tab-btn').forEach(b => b.classList.remove('active'));
  document.querySelectorAll('.tab-content').forEach(c => c.classList.remove('active'));
  btn.classList.add('active');
  document.getElementById('tab-' + tabId).classList.add('active');
  if (tabId === 'memory') loadMemories();
}

/* ===== 개발도구 전환 ===== */
const DEV_TOOL_META = {
  error: { icon: '🔍', placeholder: '에러 메시지나 스택트레이스를 붙여넣어 주세요', label: 'Error Analyzer' },
  sql:   { icon: '🗃️', placeholder: 'SQL 쿼리를 붙여넣어 주세요', label: 'SQL Explainer' },
  plan:  { icon: '📋', placeholder: '개발할 기능의 요구사항을 입력해 주세요', label: 'Requirement Planner' },
};

function switchDevTool(tool, btn) {
  currentDevTool = tool;
  document.querySelectorAll('.dev-tool-btn').forEach(b => b.classList.remove('active'));
  btn.classList.add('active');
  const meta = DEV_TOOL_META[tool];
  document.getElementById('devPlaceholderIcon').textContent = meta.icon;
  document.getElementById('devPlaceholderText').textContent = meta.placeholder;
  document.getElementById('devInput').placeholder = meta.placeholder;
  clearDev();
}

function clearDev() {
  document.getElementById('devInput').value = '';
  hide('devResult');
  hide('devComment');
  document.getElementById('devResult').textContent = '';
  document.getElementById('devComment').textContent = '';
}

function handleDevKeydown(e) {
  if (e.ctrlKey && e.key === 'Enter') runAnalysis();
}

/* ===== 채팅 ===== */
async function loadChatHistory() {
  try {
    const res = await fetch(API.history);
    const messages = await res.json();
    if (messages.length === 0) {
      appendMessage('haeun', '오빠 왔어? 하은이 오늘도 기다리고 있었어. 오늘은 나랑 무슨 얘기할래?', '');
    } else {
      messages.forEach(m => appendMessage(m.role, m.message, m.timestamp));
    }
  } catch {
    appendMessage('haeun', '오빠 왔어? 하은이 오늘도 기다리고 있었어. 오늘은 나랑 무슨 얘기할래?', '');
  }
}

/* ===== 감정 텍스트 판별 (avatar.js의 window.detectEmotion과 동일 로직) ===== */
function detectEmotion(text) {
  if (!text) return 'neutral';
  if (/힘들|슬프|우울|괴롭|지쳐|아파|힘드/.test(text))          return 'sad';
  if (/고마|감사|좋아|기쁘|행복|ㅋㅋ|ㅎㅎ/.test(text))         return 'happy';
  if (/왜|어떻게|모르겠|이해가|궁금|모르/.test(text))            return 'thinking';
  if (/대박|멋지|최고|진짜!|!!/.test(text))                       return 'excited';
  if (/괜찮|걱정|힘내|응원|안심/.test(text))                      return 'comfort';
  return 'neutral';
}

async function sendChat() {
  const input   = document.getElementById('chatInput');
  const sendBtn = document.querySelector('.chat-send-btn');
  const text    = input.value.trim();
  if (!text) return;

  // 전송 중 중복 클릭 방지
  input.disabled   = true;
  sendBtn.disabled = true;

  appendMessage('user', text, now());
  input.value = '';
  scrollChat();

  // 하은: 생각하는 상태 (portrait + VRM 동시 적용)
  window.haeunAvatar?.setSpeaking(false);
  window.haeunAvatar?.setEmotion('thinking');
  setPortraitEmotion('thinking');

  const typingId = showTyping();

  try {
    const res = await fetch(API.chat, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ message: text, model: currentModel || null }),
    });
    const data = await res.json();
    removeTyping(typingId);
    appendMessage('haeun', data.message, data.timestamp);

    // 하은: 답변 감정 반영 (portrait + VRM 동시 적용)
    const userEmotion  = detectEmotion(text);
    const replyEmotion = detectEmotion(data.message);
    // 사용자가 힘들어하면 comfort로 응답
    const finalEmotion = (userEmotion === 'sad') ? 'comfort' : (replyEmotion || 'happy');
    window.haeunAvatar?.setEmotion(finalEmotion);
    window.haeunAvatar?.setSpeaking(true);
    setPortraitEmotion(finalEmotion);

    // 텍스트 길이에 비례한 말하기 지속 시간 (min 2초, max 8초)
    const speakMs = Math.min(Math.max((data.message || '').length * 55, 2000), 8000);
    setTimeout(() => {
      window.haeunAvatar?.setSpeaking(false);
      window.haeunAvatar?.setEmotion('neutral');
      setPortraitEmotion('neutral');
    }, speakMs);

  } catch {
    removeTyping(typingId);
    appendMessage('haeun', '앗, 뭔가 잠깐 이상해졌어. 오빠, 다시 한번 말해줄래?', now());
    /* 에러 시: error 감정 → 2초 후 neutral 복귀 */
    window.haeunAvatar?.setSpeaking(false);
    window.haeunAvatar?.setEmotion('error');
    setPortraitEmotion('error');
    setTimeout(() => {
      window.haeunAvatar?.setEmotion('neutral');
      setPortraitEmotion('neutral');
    }, 2000);
  } finally {
    // 전송 완료 후 입력 복구
    input.disabled   = false;
    sendBtn.disabled = false;
    input.focus();
  }
}

function appendMessage(role, text, time) {
  const isHaeun = role === 'haeun';
  const chatBox  = document.getElementById('chatMessages');

  const row  = el('div', 'msg-row' + (isHaeun ? '' : ' user'));
  const av   = el('div', 'msg-avatar ' + (isHaeun ? 'haeun-av' : 'user-av'));
  av.textContent = isHaeun ? '하' : '나';
  const wrap = el('div', 'msg-content-wrap');
  const bub  = el('div', 'msg-bubble ' + (isHaeun ? 'haeun-msg' : 'user-msg'));
  bub.textContent = text;
  const ts   = el('div', 'msg-time');
  ts.textContent  = time || now();

  wrap.appendChild(bub);
  wrap.appendChild(ts);
  row.appendChild(av);
  row.appendChild(wrap);
  chatBox.appendChild(row);
  scrollChat();
}

function showTyping() {
  const chatBox = document.getElementById('chatMessages');
  const row = el('div', 'msg-row');
  const av  = el('div', 'msg-avatar haeun-av');
  av.textContent = '하';
  const wrap = el('div', 'msg-content-wrap');
  const ind  = el('div', 'typing-indicator');
  ind.innerHTML = '<div class="typing-dot"></div><div class="typing-dot"></div><div class="typing-dot"></div>' +
                  '<span class="typing-text">하은이가 오빠 말 열심히 생각하고 있어… 조금만 기다려줘.</span>';
  wrap.appendChild(ind);
  row.appendChild(av);
  row.appendChild(wrap);
  const id = 'typing-' + Date.now();
  row.id = id;
  chatBox.appendChild(row);
  scrollChat();
  return id;
}

function removeTyping(id) {
  const el = document.getElementById(id);
  if (el) el.remove();
}

function scrollChat() {
  const box = document.getElementById('chatMessages');
  box.scrollTop = box.scrollHeight;
}

/* ===== 개발도구 분석 ===== */
async function runAnalysis() {
  const content = document.getElementById('devInput').value.trim();
  if (!content) return;

  const btn = document.querySelector('.dev-analyze-btn');
  btn.textContent = '분석 중...';
  btn.disabled = true;

  const endpoints = { error: API.error, sql: API.sql, plan: API.plan };
  const endpoint  = endpoints[currentDevTool];

  try {
    const res  = await fetch(endpoint, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ content }),
    });
    const data = await res.json();

    const resultEl  = document.getElementById('devResult');
    const commentEl = document.getElementById('devComment');
    resultEl.innerHTML  = renderMarkdown(data.result);
    commentEl.textContent = '💬 ' + data.haeunComment;
    show('devResult');
    show('devComment');
  } catch {
    const resultEl = document.getElementById('devResult');
    resultEl.textContent = '서버 연결에 실패했어요. 서버가 실행 중인지 확인해주세요!';
    show('devResult');
  } finally {
    btn.textContent = '하은에게 물어보기 ✦';
    btn.disabled = false;
  }
}

/* ===== 오늘의 낭만 ===== */
async function fetchRomance() {
  const btn    = document.getElementById('romanceBtn');
  const bubble = document.getElementById('romanceBubble');
  const text   = document.getElementById('romanceText');

  btn.disabled = true;
  btn.style.opacity = '0.7';

  try {
    const res  = await fetch(API.romance);
    const data = await res.json();
    text.textContent = data.message;
    bubble.style.display = 'block';
    bubble.style.animation = 'none';
    void bubble.offsetHeight;
    bubble.style.animation = 'fade-in 0.5s ease';
  } catch {
    text.textContent = '오늘은 조금 돌아가도 괜찮지 않을까요?';
    bubble.style.display = 'block';
  } finally {
    btn.disabled = false;
    btn.style.opacity = '1';
  }
}

/* ===== 기억 ===== */
async function saveMemory() {
  const title   = document.getElementById('memTitle').value.trim();
  const content = document.getElementById('memContent').value.trim();
  const tag     = document.getElementById('memTag').value.trim();

  if (!title || !content) {
    alert('제목과 내용을 입력해주세요!');
    return;
  }

  try {
    await fetch(API.memory, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ title, content, tag }),
    });
    document.getElementById('memTitle').value   = '';
    document.getElementById('memContent').value = '';
    document.getElementById('memTag').value     = '';
    loadMemories();
  } catch {
    alert('저장에 실패했어요. 서버를 확인해주세요.');
  }
}

async function filterMemory() {
  const tag = document.getElementById('memTagFilter').value.trim();
  if (!tag) return loadMemories();

  try {
    const res  = await fetch(API.memory + '/tag/' + encodeURIComponent(tag));
    const list = await res.json();
    renderMemories(list);
  } catch {
    loadMemories();
  }
}

async function loadMemories() {
  try {
    const res  = await fetch(API.memory);
    const list = await res.json();
    renderMemories(list);
  } catch {
    /* 서버 미연결 시 조용히 처리 */
  }
}

function renderMemories(list) {
  const container = document.getElementById('memoryList');
  if (list.length === 0) {
    container.innerHTML = `
      <div class="memory-empty">
        <div class="memory-empty-icon">🔮</div>
        <div>아직 저장된 기억이 없어요.<br>첫 번째 기억을 남겨봐요!</div>
      </div>`;
    return;
  }
  container.innerHTML = list.map(m => `
    <div class="memory-card">
      <div class="memory-card-header">
        <div class="memory-card-title">${escape(m.title)}</div>
        ${m.tag ? `<span class="memory-card-tag">${escape(m.tag)}</span>` : ''}
        <button class="memory-card-del" onclick="deleteMemory(${m.id})" title="삭제">×</button>
      </div>
      <div class="memory-card-content">${escape(m.content)}</div>
      <div class="memory-card-time">${formatDateTime(m.createdAt)}</div>
    </div>
  `).join('');
}

async function deleteMemory(id) {
  if (!confirm('이 기억을 삭제할까요?')) return;
  try {
    await fetch(API.memory + '/' + id, { method: 'DELETE' });
    loadMemories();
  } catch {
    alert('삭제에 실패했어요.');
  }
}

/* ===== 유틸리티 ===== */
function el(tag, cls) {
  const e = document.createElement(tag);
  if (cls) e.className = cls;
  return e;
}
function show(id) { document.getElementById(id).style.display = 'block'; }
function hide(id) { document.getElementById(id).style.display = 'none'; }
function sleep(ms) { return new Promise(r => setTimeout(r, ms)); }
function now() {
  const d = new Date();
  return d.getHours().toString().padStart(2,'0') + ':' + d.getMinutes().toString().padStart(2,'0');
}
function escape(s) {
  return String(s)
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;')
    .replace(/\n/g, '<br>');
}
function formatDateTime(iso) {
  if (!iso) return '';
  const d = new Date(iso);
  return d.getFullYear() + '.' +
    String(d.getMonth()+1).padStart(2,'0') + '.' +
    String(d.getDate()).padStart(2,'0') + ' ' +
    String(d.getHours()).padStart(2,'0') + ':' +
    String(d.getMinutes()).padStart(2,'0');
}

/* 간단 마크다운 렌더러 (## h2, ### h3, **bold**, *italic*, `code`, --- hr, > blockquote, - list) */
function renderMarkdown(text) {
  if (!text) return '';
  let html = text
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;');

  // 코드 블록
  html = html.replace(/```[\w]*\n([\s\S]*?)```/g, '<pre><code>$1</code></pre>');
  // 헤딩
  html = html.replace(/^## (.+)$/gm,  '<h2>$1</h2>');
  html = html.replace(/^### (.+)$/gm, '<h3>$1</h3>');
  // 구분선
  html = html.replace(/^---$/gm, '<hr>');
  // 인용
  html = html.replace(/^&gt; (.+)$/gm, '<blockquote>$1</blockquote>');
  // 볼드
  html = html.replace(/\*\*(.+?)\*\*/g, '<strong>$1</strong>');
  // 이탤릭
  html = html.replace(/\*(.+?)\*/g, '<em>$1</em>');
  // 인라인 코드
  html = html.replace(/`(.+?)`/g, '<code>$1</code>');
  // 리스트
  html = html.replace(/^(\d+)\. (.+)$/gm, '<li>$2</li>');
  html = html.replace(/^- (.+)$/gm,        '<li>$1</li>');
  // 체크박스
  html = html.replace(/<li>\[ \] /g, '<li>☐ ');
  html = html.replace(/<li>\[x\] /gi, '<li>☑ ');
  // 줄바꿈
  html = html.replace(/\n/g, '<br>');

  return html;
}
