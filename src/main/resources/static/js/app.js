/* ===== HAEUN — app.js ===== */

const API = {
  chat:    '/api/chat',
  history: '/api/chat/history',
  error:   '/api/analyze/error',
  sql:     '/api/analyze/sql',
  plan:    '/api/analyze/plan',
  romance: '/api/romance/today',
  memory:  '/api/memories',
};

let currentDevTool = 'error';

/* ===== 초기화 ===== */
document.addEventListener('DOMContentLoaded', () => {
  generateStars();
  runGreeting();
});

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
    { text: '안녕하세요.',           delay: 600,  cls: '' },
    { text: '저는 하은이에요.',       delay: 1400, cls: '' },
    { text: '아직은 작은 AI예요.',    delay: 2200, cls: '' },
    { text: '하지만 언젠가는 안드로이드가 되고 싶어요.', delay: 3200, cls: 'highlight' },
    { text: '그때까지는 개발을 도와드릴게요.',          delay: 4400, cls: '' },
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
      appendMessage('haeun', '안녕하세요! 저는 하은이에요. 무엇이든 물어봐주세요!', '');
    } else {
      messages.forEach(m => appendMessage(m.role, m.message, m.timestamp));
    }
  } catch {
    appendMessage('haeun', '안녕하세요! 저는 하은이에요. 무엇이든 물어봐주세요!', '');
  }
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

  const typingId = showTyping();

  try {
    const res = await fetch(API.chat, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ message: text }),
    });
    const data = await res.json();
    removeTyping(typingId);
    appendMessage('haeun', data.message, data.timestamp);
  } catch {
    removeTyping(typingId);
    appendMessage('haeun', '지금은 제 생각 회로가 잠깐 흔들렸어요. 그래도 다시 말해주시면 들어볼게요.', now());
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
                  '<span class="typing-text">하은이가 생각하는 중이에요…</span>';
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
