/* VPL Lab - client. Layout and behaviour follow the Moodle VPL plugin. */
'use strict';

// ======================================================================= helpers
const $ = (sel, root = document) => root.querySelector(sel);
const $$ = (sel, root = document) => Array.from(root.querySelectorAll(sel));
const esc = (s) => String(s == null ? '' : s).replace(/[&<>"']/g, (c) => ({ '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;' }[c]));
const app = () => document.getElementById('app');
const enc = encodeURIComponent;

async function apiGet(url) {
  const r = await fetch(url, { cache: 'no-store' });
  const j = await r.json();
  if (!r.ok) throw new Error(j.error || r.statusText);
  return j;
}
async function apiPost(url, body) {
  const r = await fetch(url, { method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(body || {}) });
  const j = await r.json();
  if (!r.ok) throw new Error(j.error || r.statusText);
  return j;
}
function toast(msg, ms = 1600) {
  const t = document.createElement('div');
  t.className = 'toast';
  t.textContent = msg;
  document.body.appendChild(t);
  setTimeout(() => t.remove(), ms);
}
function lsGet(k, d) { try { const v = localStorage.getItem(k); return v == null ? d : v; } catch (e) { return d; } }
function lsSet(k, v) { try { localStorage.setItem(k, v); } catch (e) { /* ignore */ } }

// ======================================================================= icons (Font Awesome 4 + SVG fallback)
const SVG = {
  'plus-square': '<svg viewBox="0 0 24 24"><rect x="2" y="2" width="20" height="20" rx="4" fill="currentColor"/><path d="M12 6.5v11M6.5 12h11" stroke="#e6e6e6" stroke-width="2.6" stroke-linecap="round"/></svg>',
  save: '<svg viewBox="0 0 24 24"><path d="M3 4.5A1.5 1.5 0 0 1 4.5 3h12.3L21 7.2v12.3a1.5 1.5 0 0 1-1.5 1.5h-15A1.5 1.5 0 0 1 3 19.5z" fill="currentColor"/><rect x="6" y="3" width="10" height="6" fill="#e6e6e6"/><circle cx="12" cy="15" r="3" fill="#e6e6e6"/></svg>',
  rocket: '<svg viewBox="0 0 24 24"><path d="M14.5 3.2c2.9-.9 5.4-.6 6.3-.4.2.9.5 3.4-.4 6.3-.8 2.6-2.8 5-5.2 6.7l.3 3.7-3.3 2.5-1-3.9-3.3-3.3-3.9-1 2.5-3.3 3.7.3c1.7-2.4 4.1-4.4 6.3-5.6z" fill="currentColor"/><circle cx="16" cy="8" r="1.9" fill="#e6e6e6"/><path d="M5.8 15.4c-1.6.4-2.6 2.2-2.8 5.6 3.4-.2 5.2-1.2 5.6-2.8z" fill="currentColor"/></svg>',
  'check-square-o': '<svg viewBox="0 0 24 24"><rect x="2.5" y="2.5" width="19" height="19" rx="3" fill="none" stroke="currentColor" stroke-width="2.2"/><path d="M7 12.3l3.4 3.4L17.3 8.6" fill="none" stroke="currentColor" stroke-width="2.6" stroke-linecap="round" stroke-linejoin="round"/></svg>',
  commenting: '<svg viewBox="0 0 24 24"><path d="M12 3C6.5 3 2 6.6 2 11c0 2.4 1.3 4.5 3.4 6-.2 1.6-1 3-2.2 4 2.4 0 4.4-.9 5.8-2.2 1 .3 2 .4 3 .4 5.5 0 10-3.6 10-8.1S17.5 3 12 3z" fill="currentColor"/></svg>',
  terminal: '<svg viewBox="0 0 24 24"><path d="M3.5 6.5l6 5.5-6 5.5" fill="none" stroke="currentColor" stroke-width="2.6" stroke-linecap="round" stroke-linejoin="round"/><path d="M12 18.5h9" stroke="currentColor" stroke-width="2.6" stroke-linecap="round"/></svg>',
  expand: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.4" stroke-linecap="round" stroke-linejoin="round"><path d="M14 3h7v7M21 3l-7 7M10 21H3v-7M3 21l7-7"/></svg>',
  compress: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.4" stroke-linecap="round" stroke-linejoin="round"><path d="M20 10h-6V4M14 10l7-7M4 14h6v6M10 14l-7 7"/></svg>',
  question: '<svg viewBox="0 0 24 24"><path d="M8.6 8.4a3.5 3.5 0 1 1 5.5 2.9c-1.2.8-2.1 1.5-2.1 3.1" fill="none" stroke="currentColor" stroke-width="2.6" stroke-linecap="round"/><circle cx="12" cy="19" r="1.7" fill="currentColor"/></svg>',
  shield: '<svg viewBox="0 0 24 24"><path d="M12 2l8 3v6.2c0 5-3.4 9.3-8 10.8-4.6-1.5-8-5.8-8-10.8V5z" fill="currentColor"/></svg>',
  lock: '<svg viewBox="0 0 24 24"><rect x="4" y="10" width="16" height="12" rx="2" fill="currentColor"/><path d="M8 10V7a4 4 0 0 1 8 0v3" fill="none" stroke="currentColor" stroke-width="2.4"/></svg>',
  remove: '<svg viewBox="0 0 24 24"><path d="M5 5l14 14M19 5L5 19" stroke="currentColor" stroke-width="3" stroke-linecap="round"/></svg>',
  'file-code-o': '<svg viewBox="0 0 24 24"><path d="M6 2h8l5 5v14a1 1 0 0 1-1 1H6a1 1 0 0 1-1-1V3a1 1 0 0 1 1-1z" fill="none" stroke="currentColor" stroke-width="2"/><path d="M10 12l-2 2 2 2M14 12l2 2-2 2" fill="none" stroke="currentColor" stroke-width="1.8"/></svg>',
  trash: '<svg viewBox="0 0 24 24"><path d="M4 6h16M9 6V4h6v2M6 6l1 15h10l1-15" fill="none" stroke="currentColor" stroke-width="2"/></svg>',
  download: '<svg viewBox="0 0 24 24"><path d="M12 3v12M7 10l5 5 5-5M4 20h16" fill="none" stroke="currentColor" stroke-width="2.4" stroke-linecap="round"/></svg>',
  refresh: '<svg viewBox="0 0 24 24"><path d="M20 11a8 8 0 1 0-2.3 5.7M20 4v7h-7" fill="none" stroke="currentColor" stroke-width="2.4" stroke-linecap="round"/></svg>',
  'text-height': '<svg viewBox="0 0 24 24"><path d="M3 6V4h11v2M8.5 4v16M6 20h5M19 4v16M17 6l2-2 2 2M17 18l2 2 2-2" fill="none" stroke="currentColor" stroke-width="2"/></svg>',
  'paint-brush': '<svg viewBox="0 0 24 24"><path d="M20 3L10 13l1.5 1.5L21.5 4.5zM9 14c-2 0-3.5 1.5-3.5 3.5 0 1-.8 2-2.5 2.5 3 1.5 7 .5 7.5-3.5z" fill="currentColor"/></svg>',
  undo: '<svg viewBox="0 0 24 24"><path d="M9 7H4V2M4 7a9 9 0 1 1-1 7" fill="none" stroke="currentColor" stroke-width="2.4" stroke-linecap="round"/></svg>',
  repeat: '<svg viewBox="0 0 24 24"><path d="M15 7h5V2M20 7a9 9 0 1 0 1 7" fill="none" stroke="currentColor" stroke-width="2.4" stroke-linecap="round"/></svg>',
  'object-group': '<svg viewBox="0 0 24 24"><rect x="3" y="3" width="18" height="18" fill="none" stroke="currentColor" stroke-width="2" stroke-dasharray="3 2"/><rect x="7" y="7" width="7" height="6" fill="currentColor"/></svg>',
  search: '<svg viewBox="0 0 24 24"><circle cx="10" cy="10" r="6.5" fill="none" stroke="currentColor" stroke-width="2.4"/><path d="M15 15l6 6" stroke="currentColor" stroke-width="2.6" stroke-linecap="round"/></svg>',
  exchange: '<svg viewBox="0 0 24 24"><path d="M4 8h15M15 4l4 4-4 4M20 16H5M9 12l-4 4 4 4" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round"/></svg>',
  'search-plus': '<svg viewBox="0 0 24 24"><circle cx="10" cy="10" r="6.5" fill="none" stroke="currentColor" stroke-width="2.4"/><path d="M15 15l6 6M10 7v6M7 10h6" stroke="currentColor" stroke-width="2.2" stroke-linecap="round"/></svg>',
  'list-ul': '<svg viewBox="0 0 24 24"><path d="M8 6h13M8 12h13M8 18h13" stroke="currentColor" stroke-width="2.4"/><circle cx="4" cy="6" r="1.6" fill="currentColor"/><circle cx="4" cy="12" r="1.6" fill="currentColor"/><circle cx="4" cy="18" r="1.6" fill="currentColor"/></svg>',
  code: '<svg viewBox="0 0 24 24"><path d="M8.5 7.5L4 12l4.5 4.5M15.5 7.5L20 12l-4.5 4.5M13.5 5l-3 14" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round"/></svg>',
  archive: '<svg viewBox="0 0 24 24"><rect x="2" y="3" width="20" height="5" fill="currentColor"/><path d="M4 9h16v11H4z" fill="currentColor"/><path d="M9 12h6" stroke="#fff" stroke-width="2"/></svg>',
};
function ic(name, lg = true) {
  return `<span class="ic"><i class="fa ${lg ? 'fa-lg ' : ''}fa-${name}" aria-hidden="true"></i><span class="svgi">${SVG[name] || ''}</span></span>`;
}
const VPL_ICON = `<svg class="vplicon" viewBox="0 0 40 40"><text x="20" y="13" text-anchor="middle" font-family="Arial" font-weight="700" font-size="10" fill="#c2185b">VPL</text>
  <path d="M13 17l-7 8 7 8M27 17l7 8-7 8M23 15l-6 20" fill="none" stroke="#c2185b" stroke-width="2.6" stroke-linecap="round" stroke-linejoin="round"/></svg>`;

(function detectFontAwesome() {
  // Font Awesome 4 comes from a CDN; without internet the inline SVG icons are used instead.
  const ok = () => document.body.classList.add('fa-ok');
  const loaded = () => {
    try {
      for (const f of document.fonts) {
        if (f.family.replace(/["']/g, '') === 'FontAwesome' && f.status === 'loaded') return true;
      }
    } catch (e) { /* ignore */ }
    return false;
  };
  if (document.fonts && document.fonts.load) {
    document.fonts.load('14px FontAwesome', '\uf135').then((faces) => { if ((faces && faces.length) || loaded()) ok(); }).catch(() => { });
    document.fonts.ready.then(() => { if (loaded()) ok(); });
  }
})();

// ======================================================================= Ace
const ACE_BASES = [
  '/web/vendor/ace/',
  'https://cdn.jsdelivr.net/npm/ace-builds@1.4.12/src-min-noconflict/',
  'https://cdnjs.cloudflare.com/ajax/libs/ace/1.4.12/',
];
let acePromise = null;
function loadScript(src, timeout = 7000) {
  return new Promise((resolve, reject) => {
    const s = document.createElement('script');
    const t = setTimeout(() => { s.remove(); reject(new Error('timeout')); }, timeout);
    s.src = src;
    s.onload = () => { clearTimeout(t); resolve(); };
    s.onerror = () => { clearTimeout(t); s.remove(); reject(new Error('load failed')); };
    document.head.appendChild(s);
  });
}
function loadAce() {
  if (lsGet('vpl.plainEditor', '') === '1') return Promise.resolve(null);
  if (!acePromise) {
    acePromise = (async () => {
      for (const base of ACE_BASES) {
        try {
          await loadScript(base + 'ace.js');
          if (window.ace) {
            window.ace.config.set('basePath', base);
            window.ace.config.set('modePath', base);
            window.ace.config.set('themePath', base);
            try { await loadScript(base + 'ext-searchbox.js', 4000); } catch (e) { /* optional */ }
            return window.ace;
          }
        } catch (e) { /* next */ }
      }
      console.warn('Ace could not be loaded, using the plain editor.');
      return null;
    })();
  }
  return acePromise;
}
const modeFor = (name) => (/\.java$/i.test(name) ? 'ace/mode/java' : 'ace/mode/text');
const aceTheme = () => (lsGet('vpl.theme', 'chrome') === 'dark' ? 'ace/theme/monokai' : 'ace/theme/chrome');
const fontSize = () => parseInt(lsGet('vpl.fontSize', '12'), 10) || 12;

function renderViewer(host, name, content, ace) {
  if (ace) {
    const div = document.createElement('div');
    div.className = 'viewer';
    host.appendChild(div);
    const ed = ace.edit(div);
    ed.setTheme('ace/theme/chrome');
    ed.session.setMode(modeFor(name));
    ed.setValue(content, -1);
    ed.setOptions({ readOnly: true, maxLines: Infinity, minLines: 1, fontSize: 12, showPrintMargin: false, highlightActiveLine: false, highlightGutterLine: false });
    try { ed.renderer.$cursorLayer.element.style.display = 'none'; } catch (e) { /* cosmetic */ }
    return;
  }
  const lines = content.split('\n');
  const div = document.createElement('div');
  div.className = 'viewer pview';
  div.innerHTML = `<div class="ln">${lines.map((_, i) => i + 1).join('\n')}</div><div class="tx">${esc(content)}</div>`;
  host.appendChild(div);
}

// ======================================================================= page chrome
let SETTINGS = { userName: '' };
function initials(name) {
  return (name || '').split(/\s+/).filter(Boolean).map((w) => w[0]).slice(0, 2).join('').toUpperCase() || '?';
}
function navbar() {
  return `<div class="navbar">
    <a class="brand" href="#/">VPL Lab</a>
    <div class="nav"><a href="#/">Ana sayfa</a><a href="#/">Ödevler</a></div>
    <div class="sp"></div>
    <div class="user"><span class="avatar">${esc(initials(SETTINGS.userName))}</span><span class="uname">${esc(SETTINGS.userName || 'Your Name')}</span></div>
  </div>`;
}
function pill(g) {
  if (g == null) return '<span class="pill">Not evaluated</span>';
  return `<span class="pill ${g >= 100 ? 'full' : g > 0 ? 'part' : 'zero'}">${g} / 100</span>`;
}
function header(d, active) {
  const id = enc(d.id);
  const tab = (key, href, icon, label) => `<a class="${active === key ? 'on' : ''}" href="${href}">${ic(icon, false)}${label}</a>`;
  return `
    <div class="crumbs"><a href="#/">Ödevler</a><span class="sep">/</span><a href="#/">${esc(courseCode(d.course))}</a><span class="sep">/</span>${esc(d.title)}</div>
    <div class="ptitle">${VPL_ICON}<h1>${esc(d.title)}</h1></div>
    <div class="ntabs">
      ${tab('desc', `#/a/${id}`, 'list-ul', 'Description')}
      ${tab('edit', `#/a/${id}/edit`, 'code', 'Düzenle')}
      ${tab('view', `#/a/${id}/view`, 'archive', 'Submission view')}
    </div>`;
}
function courseCode(course) {
  const m = String(course || '').match(/^[A-Za-z]+\s*\d+/);
  return m ? m[0].replace(/\s+/g, '') : course || 'Course';
}

// ======================================================================= router
let leaveHook = null;
async function route() {
  if (leaveHook) { const h = leaveHook; leaveHook = null; try { await h(); } catch (e) { /* ignore */ } }
  $$('.overlay').forEach((o) => o.remove());
  const h = location.hash.replace(/^#/, '') || '/';
  const m = h.match(/^\/a\/([^/]+)(?:\/(edit|view))?\/?$/);
  window.scrollTo(0, 0);
  if (m) {
    const id = decodeURIComponent(m[1]);
    if (m[2] === 'edit') return renderEditor(id);
    if (m[2] === 'view') return renderSubmission(id);
    return renderDescription(id);
  }
  return renderList();
}
window.addEventListener('hashchange', route);

// ======================================================================= list
async function renderList() {
  document.title = 'Ödevler - VPL Lab';
  app().innerHTML = navbar() + '<div class="page"><p>Loading...</p></div>';
  let list = [];
  try { list = await apiGet('/api/assignments'); } catch (e) { app().innerHTML = navbar() + `<div class="page"><p>${esc(e.message)}</p></div>`; return; }
  let body;
  if (!list.length) {
    body = `<div class="empty"><h2>Henüz ödev yok</h2>
      <p>Bu klasörü bir yapay zekâ aracında (Claude Code, Codex, Cursor, Copilot…) aç ve sadece konuyu söyle.<br>Örnek: <em>"LinkedList ödevi hazırla"</em>. Sonra bu sayfayı yenile.</p></div>`;
  } else {
    body = '<div class="courselist">' + list.map((a) => `
      <div class="arow">${VPL_ICON}
        <div class="main"><div class="t"><a href="#/a/${enc(a.id)}">${esc(a.title)}</a></div>
          <div class="s">${esc(a.course)}${a.subtitle ? ' · ' + esc(a.subtitle) : ''}${a.evalCount ? ` · ${a.evalCount} evaluation(s)` : ''}</div></div>
        ${pill(a.grade)}
        <a class="btn primary" href="#/a/${enc(a.id)}/edit">${ic('code', false)} Düzenle</a>
      </div>`).join('') + '</div>';
  }
  const hint = `<p class="newhint">Yeni ödev eklemek için: bu klasörü bir yapay zekâ aracında (Claude Code, Cursor, Copilot…) aç, konuyu söyle — örn. <em>"bu site için stack ödevi hazırla"</em> — sonra sayfayı yenile.</p>`;
  app().innerHTML = navbar() + `<div class="page"><div class="ptitle"><h1>Ödevler</h1></div>${body}${list.length ? hint : ''}</div>`;
}

// ======================================================================= description (view.php)
async function loadAssignment(id) {
  try { return await apiGet('/api/a/' + enc(id)); } catch (e) {
    app().innerHTML = navbar() + `<div class="page"><p>${esc(e.message)}</p><a href="#/">Ödevler</a></div>`;
    return null;
  }
}
function downloadText(name, text) {
  const a = document.createElement('a');
  a.href = URL.createObjectURL(new Blob([text], { type: 'text/plain' }));
  a.download = name;
  document.body.appendChild(a);
  a.click();
  setTimeout(() => { URL.revokeObjectURL(a.href); a.remove(); }, 500);
}
async function renderDescription(id) {
  app().innerHTML = navbar() + '<div class="page"><p>Loading...</p></div>';
  const d = await loadAssignment(id);
  if (!d) return;
  document.title = d.title + ' Description - VPL Lab';
  const names = d.requested.map((f) => esc(f.name)).join(', ');
  app().innerHTML = navbar() + `<div class="page">${header(d, 'desc')}
    <div class="vplinfo"><b>Requested files:</b> ${names} ( <a href="javascript:void 0" id="dl">Download</a>)</div>
    <div class="vplinfo"><b>Type of work:</b> Individual work</div>
    <div class="desc-body">${d.description}</div>
    <div class="reqfiles"><h2>Requested files</h2><div id="rf"></div></div>
    <p style="margin-top:28px"><button class="btn" id="resetBtn">${ic('refresh', false)} Reset my code to the starter files</button></p>
  </div>` + helpFab();
  $('#dl').onclick = () => d.requested.forEach((f) => downloadText(f.name, f.content));
  $('#resetBtn').onclick = async () => {
    if (!confirm('Kodun başlangıç dosyalarıyla değiştirilecek. Emin misin?')) return;
    await apiPost('/api/a/' + enc(id) + '/reset');
    toast('Başlangıç koduna sıfırlandı');
  };
  bindHelpFab();
  const ace = await loadAce();
  const rf = $('#rf');
  if (!rf) return;
  for (const f of d.requested) {
    const h = document.createElement('h3');
    h.textContent = f.name;
    rf.appendChild(h);
    renderViewer(rf, f.name, f.content, ace);
  }
}

// ======================================================================= submission view
async function renderSubmission(id) {
  app().innerHTML = navbar() + '<div class="page"><p>Loading...</p></div>';
  const d = await loadAssignment(id);
  if (!d) return;
  document.title = d.title + ' Submission view - VPL Lab';
  const st = d.state || {};
  const rep = st.report ? st.report.replace(/\n+$/, '').split('\n').map((l) => '> ' + l).join('\n') : '';
  app().innerHTML = navbar() + `<div class="page subview">${header(d, 'view')}
    ${st.evaluatedAt ? `<p>Last evaluation: <b>${esc(st.evaluatedAt)}</b> · Evaluations: ${st.evalCount || 0}</p>
      <p class="grade">Proposed grade: <b>${st.grade == null ? '-' : st.grade + ' / 100'}</b></p>
      <h3>Yorumlar</h3><pre class="box">${esc(rep)}</pre>
      <h3>Compilation / execution</h3><pre class="box">${esc(st.compilation || '')}</pre>`
      : '<p>No evaluation yet. Open <a href="#/a/' + enc(id) + '/edit">Düzenle</a> and press Evaluate.</p>'}
    <h3>Submitted files</h3><div id="sf"></div>
  </div>` + helpFab();
  bindHelpFab();
  const ace = await loadAce();
  const sf = $('#sf');
  if (!sf) return;
  for (const f of d.files.filter((x) => !x.readonly)) {
    const h = document.createElement('h4');
    h.textContent = f.name;
    sf.appendChild(h);
    renderViewer(sf, f.name, f.content, ace);
  }
}

// ======================================================================= editors
class PlainEditor {
  constructor(host, onChange, onCursor) {
    this.el = document.createElement('div');
    this.el.className = 'plained';
    this.el.innerHTML = '<div class="gut"></div><textarea spellcheck="false" autocapitalize="off" autocomplete="off"></textarea>';
    host.appendChild(this.el);
    this.gut = $('.gut', this.el);
    this.ta = $('textarea', this.el);
    this.errors = new Set();
    this.setFontSize(fontSize());
    this.ta.addEventListener('input', () => { if (this.file && !this.file.readonly) this.file.content = this.ta.value; this.renderGutter(); onChange(); });
    this.ta.addEventListener('scroll', () => { this.gut.scrollTop = this.ta.scrollTop; });
    const cur = () => {
      const pre = this.ta.value.slice(0, this.ta.selectionStart);
      onCursor(pre.split('\n').length, pre.length - pre.lastIndexOf('\n'));
    };
    ['keyup', 'click', 'input'].forEach((ev) => this.ta.addEventListener(ev, cur));
    this.ta.addEventListener('keydown', (e) => {
      if (this.ta.readOnly) return;
      if (e.key === 'Tab') { e.preventDefault(); this.insert('    '); }
      else if (e.key === 'Enter') {
        const v = this.ta.value;
        const s = this.ta.selectionStart;
        const ls = v.lastIndexOf('\n', s - 1) + 1;
        const indent = (v.slice(ls, s).match(/^[ \t]*/) || [''])[0];
        e.preventDefault();
        this.insert('\n' + indent + (/\{\s*$/.test(v.slice(ls, s)) ? '    ' : ''));
      }
    });
  }
  insert(text) { this.ta.setRangeText(text, this.ta.selectionStart, this.ta.selectionEnd, 'end'); this.ta.dispatchEvent(new Event('input')); }
  renderGutter() {
    const n = this.ta.value.split('\n').length;
    let out = '';
    for (let i = 1; i <= n; i++) out += this.errors.has(i - 1) ? `<span class="e">${i}</span>\n` : i + '\n';
    this.gut.innerHTML = out;
    this.gut.scrollTop = this.ta.scrollTop;
  }
  open(file) {
    this.file = file;
    this.ta.value = file.content;
    this.ta.readOnly = !!file.readonly;
    this.errors = new Set((file.annotations || []).map((a) => a.row));
    this.renderGutter();
    this.ta.setSelectionRange(0, 0);
    this.ta.scrollTop = 0;
  }
  setAnnotations(file) { if (file === this.file) { this.errors = new Set((file.annotations || []).map((a) => a.row)); this.renderGutter(); } }
  goto(row) {
    const lines = this.ta.value.split('\n');
    let pos = 0;
    for (let i = 0; i < row && i < lines.length; i++) pos += lines[i].length + 1;
    this.ta.focus();
    this.ta.setSelectionRange(pos, pos);
    this.ta.scrollTop = Math.max(0, row * 16 - 100);
  }
  setFontSize(px) { this.el.style.fontSize = px + 'px'; this.el.style.lineHeight = Math.round(px * 1.35) + 'px'; }
  setTheme() { }
  command(name) {
    if (name === 'selectall') { this.ta.focus(); this.ta.select(); }
    else if (name === 'undo' || name === 'redo') { this.ta.focus(); document.execCommand(name); }
    else toast('Bu özellik için Ace editörü gerekiyor (internet)');
  }
  focus() { this.ta.focus({ preventScroll: true }); }
  resize() { }
}

class AceEditor {
  constructor(ace, host, onChange, onCursor) {
    this.ace = ace;
    const div = document.createElement('div');
    host.appendChild(div);
    this.ed = ace.edit(div);
    this.ed.setTheme(aceTheme());
    this.ed.setOptions({ fontSize: fontSize(), showPrintMargin: false, animatedScroll: false });
    this.onChange = onChange;
    this.ed.selection.on('changeCursor', () => {
      const p = this.ed.getCursorPosition();
      onCursor(p.row + 1, p.column + 1);
    });
  }
  open(file) {
    if (!file.session) {
      file.session = this.ace.createEditSession(file.content, modeFor(file.name));
      file.session.setUseSoftTabs(true);
      file.session.setTabSize(4);
      file.session.on('change', () => { if (!file.readonly) { file.content = file.session.getValue(); this.onChange(); } });
    }
    this.file = file;
    this.ed.setSession(file.session);
    this.ed.setReadOnly(!!file.readonly);
    this.setAnnotations(file);
  }
  setAnnotations(file) {
    if (file.session) file.session.setAnnotations((file.annotations || []).map((a) => ({ row: a.row, column: a.column, text: a.text, type: 'error' })));
  }
  goto(row, col) { this.ed.gotoLine(row + 1, col || 0, false); this.ed.focus(); }
  setFontSize(px) { this.ed.setFontSize(px); }
  setTheme() { this.ed.setTheme(aceTheme()); }
  command(name) {
    const map = { undo: 'undo', redo: 'redo', selectall: 'selectall', find: 'find', replace: 'replace', next: 'findnext' };
    this.ed.focus();
    this.ed.execCommand(map[name] || name);
  }
  focus() { this.ed.focus(); }
  resize() { this.ed.resize(); }
  commands(list) { list.forEach((c) => this.ed.commands.addCommand(c)); }
}

// ======================================================================= IDE (edit.php)
let E = null;

async function renderEditor(id) {
  app().innerHTML = navbar() + '<div class="page"><p>Loading...</p></div>';
  const d = await loadAssignment(id);
  if (!d) return;
  const ace = await loadAce();
  document.title = d.title + ' Düzenle - VPL Lab';
  const st = d.state || {};
  E = { id, d, files: d.files.map((f) => ({ ...f })), cur: 0, dirty: false, st, ace };

  app().innerHTML = navbar() + `<div class="page">${header(d, 'edit')}
  <div class="vplide" id="vplide">
    <div class="vmenu" id="vmenu">
      <button class="vb" id="bMore" title="Daha fazla göster...">${ic('plus-square')}</button>
      <button class="vb" id="bSave" title="Kaydet (Ctrl-S)" disabled>${ic('save')}</button>
      <span class="grp">
        <button class="vb" id="bRun" title="Run (Ctrl-F11)">${ic('rocket')}</button>
        <button class="vb" id="bEval" title="Evaluate (Shift-F11)">${ic('check-square-o')} <span class="xh" id="evalCount">${st.evalCount || 0}</span></button>
        <button class="vb" id="bComments" title="Yorumlar">${ic('commenting')}</button>
        <button class="vb" id="bConsole" title="Console">${ic('terminal')}</button>
      </span>
      <span class="more">
        <span class="grp">
          <button class="vb" id="bNew" title="New file">${ic('file-code-o')}</button>
          <button class="vb" id="bDelete" title="Delete file">${ic('trash')}</button>
        </span>
      </span>
      <button class="vb" id="bFull" title="Fullscreen (Alt-F)">${ic('expand')}</button>
    </div>
    <div class="vbody">
      <div class="vleft">
        <div class="vtabs" id="vtabs"></div>
        <div class="vedit" id="vedit"><div class="vstatus" id="vstatus">Ln 1, Col 1 Java</div></div>
      </div>
      <div class="vsplit" id="vsplit"></div>
      <div class="vright" id="vright">
        <div class="acc" id="acc">
          <h4 data-k="grade" id="hGrade"><span class="tri"></span><span id="gradeTxt">Proposed grade: -</span></h4>
          <div class="ac" data-k="grade"><pre id="gradeInfo"></pre></div>
          <h4 data-k="comp"><span class="tri"></span>Compilation</h4>
          <div class="ac" data-k="comp"><pre id="compOut"></pre></div>
          <h4 data-k="comm"><span class="tri"></span>Yorumlar</h4>
          <div class="ac" data-k="comm"><pre id="report"></pre></div>
          <h4 data-k="desc"><span class="tri"></span>Description</h4>
          <div class="ac desc" data-k="desc">${d.description}</div>
        </div>
      </div>
    </div>
  </div></div>
  ${helpFab()}
  <div class="console hidden" id="console">
    <div class="ct" id="conTitle"><span>Console</span><span class="st" id="conStatus"></span><span class="sp"></span>
      <button id="conStop" title="Stop">■</button><button id="conClear" title="Clear">Clear</button><button id="conClose" title="Close">✕</button></div>
    <pre id="conOut"></pre>
    <div class="ci"><span>›</span><input id="conIn" placeholder="input + Enter" autocomplete="off" spellcheck="false"></div>
  </div>`;

  const onChange = () => { if (!E.dirty) { E.dirty = true; $('#bSave').disabled = false; } renderTabs(); };
  const onCursor = (r, c) => { $('#vstatus').textContent = `Ln ${r}, Col ${c} ${/\.java$/i.test(curFile().name) ? 'Java' : 'Text'}`; };
  E.editor = ace ? new AceEditor(ace, $('#vedit'), onChange, onCursor) : new PlainEditor($('#vedit'), onChange, onCursor);
  if (ace) {
    E.editor.commands([
      { name: 'vplSave', bindKey: { win: 'Ctrl-S', mac: 'Command-S' }, exec: () => save() },
      { name: 'vplRun', bindKey: { win: 'Ctrl-F11', mac: 'Ctrl-F11' }, exec: () => run() },
      { name: 'vplEval', bindKey: { win: 'Shift-F11', mac: 'Shift-F11' }, exec: () => evaluate() },
    ]);
  }

  applyAnnotations(st.annotations || []);
  renderTabs();
  openFile(0);
  renderResults(st, st.report ? 'comm' : null);

  $$('#acc h4').forEach((h) => h.addEventListener('click', () => openSection(h.dataset.k, !h.classList.contains('on'))));
  $('#bMore').onclick = () => { $('#vmenu').classList.toggle('showmore'); lsSet('vpl.more', $('#vmenu').classList.contains('showmore') ? '1' : ''); fitIde(); };
  if (lsGet('vpl.more', '') === '1') $('#vmenu').classList.add('showmore');
  $('#bSave').onclick = () => save();
  $('#bRun').onclick = run;
  $('#bEval').onclick = evaluate;
  $('#bComments').onclick = () => toggleRight();
  $('#bConsole').onclick = () => showConsole($('#console').classList.contains('hidden'));
  $('#bFull').onclick = toggleFull;
  $('#bNew').onclick = newFile;
  $('#bDelete').onclick = () => deleteFile(E.cur);
  bindHelpFab();
  setupSplitter();
  setupConsole();
  if (lsGet('vpl.rightHidden', '') === '1') toggleRight(false);

  $('#compOut').addEventListener('click', (e) => {
    const a = e.target.closest('[data-file]');
    if (!a) return;
    const idx = E.files.findIndex((f) => f.name === a.dataset.file);
    if (idx >= 0) { openFile(idx); E.editor.goto(+a.dataset.row, 0); }
  });

  const keyHandler = (e) => {
    const mod = e.metaKey || e.ctrlKey;
    if (mod && !e.shiftKey && (e.key === 's' || e.key === 'S')) { e.preventDefault(); save(); }
    else if (e.key === 'F11' && e.ctrlKey) { e.preventDefault(); run(); }
    else if (e.key === 'F11' && e.shiftKey) { e.preventDefault(); evaluate(); }
    else if (e.altKey && e.code === 'KeyF') { e.preventDefault(); e.stopPropagation(); toggleFull(); }
    else if (e.altKey && e.code === 'KeyR') { e.preventDefault(); e.stopPropagation(); run(); }
    else if (e.altKey && e.code === 'KeyE') { e.preventDefault(); e.stopPropagation(); evaluate(); }
    else if (e.key === 'Escape' && $('#vplide.fullscreen') && !$('.overlay')) { toggleFull(); }
  };
  window.addEventListener('keydown', keyHandler, true);
  const beforeUnload = (e) => { if (E && E.dirty) { e.preventDefault(); e.returnValue = ''; } };
  window.addEventListener('beforeunload', beforeUnload);
  window.addEventListener('resize', fitIde);
  fitIde();
  setTimeout(fitIde, 50);

  leaveHook = async () => {
    window.removeEventListener('keydown', keyHandler, true);
    window.removeEventListener('beforeunload', beforeUnload);
    window.removeEventListener('resize', fitIde);
    document.body.style.overflow = '';
    stopPolling();
    if (E && E.dirty) { try { await save(true); } catch (e) { /* ignore */ } }
    E = null;
  };
  E.editor.focus();
}

function fitIde() {
  const ide = $('#vplide');
  if (!ide || !E) return;
  if (ide.classList.contains('fullscreen')) { ide.style.height = ''; E.editor.resize(); return; }
  const top = ide.getBoundingClientRect().top + window.scrollY;
  const h = Math.max(420, window.innerHeight - top - 14);
  ide.style.height = h + 'px';
  E.editor.resize();
}

function curFile() { return E.files[E.cur]; }

function renderTabs() {
  const errFiles = new Set(E.files.filter((f) => (f.annotations || []).length).map((f) => f.name));
  $('#vtabs').innerHTML = E.files.map((f, i) => {
    const can = !f.required && !f.readonly;
    const icon = f.readonly ? `<span class="ci" title="Read only">${ic('lock', false)}</span>` : f.required ? `<span class="ci" title="Gerekli">${ic('shield', false)}</span>` : '';
    return `<div class="vtab ${i === E.cur ? 'on' : ''} ${errFiles.has(f.name) ? 'err' : ''} ${f.modified ? 'mod' : ''}" data-i="${i}">
      <span class="fname">${esc(f.name)}</span> ${icon}
      <span class="cx ${can ? '' : 'off'}" data-del="${i}" title="${can ? 'Delete file' : 'Kapat'}">${ic('remove', false)}</span></div>`;
  }).join('');
  $$('#vtabs .vtab').forEach((t) => t.addEventListener('click', (e) => {
    const del = e.target.closest('[data-del]');
    if (del) { e.stopPropagation(); deleteFile(+del.dataset.del); return; }
    openFile(+t.dataset.i);
  }));
}

function openFile(i) {
  E.cur = i;
  E.editor.open(E.files[i]);
  renderTabs();
  const t = $$('#vtabs .vtab')[i];
  if (t) t.scrollIntoView({ block: 'nearest', inline: 'nearest' });
  $('#vstatus').textContent = `Ln 1, Col 1 ${/\.java$/i.test(E.files[i].name) ? 'Java' : 'Text'}`;
  $('#bDelete').disabled = !!(E.files[i].required || E.files[i].readonly);
}

function studentPayload() {
  return { files: E.files.filter((f) => !f.readonly).map((f) => ({ name: f.name, content: f.content })) };
}

async function save(silent) {
  if (!E) return;
  await apiPost('/api/a/' + enc(E.id) + '/save', studentPayload());
  if (!E) return;
  E.dirty = false;
  $('#bSave').disabled = true;
  renderTabs();
  if (!silent) toast('Saved');
}

function deleteFile(i) {
  const f = E.files[i];
  if (!f || f.required || f.readonly) return;
  if (!confirm(`${f.name} silinsin mi?`)) return;
  E.files.splice(i, 1);
  E.dirty = true;
  openFile(Math.max(0, Math.min(i, E.files.length - 1)));
  save(true);
}

async function resetFiles() {
  if (!confirm('Tüm dosyalar başlangıç haline dönecek. Emin misin?')) return;
  await apiPost('/api/a/' + enc(E.id) + '/reset');
  E.dirty = false;
  const id = E.id;
  leaveHook = null;
  E = null;
  renderEditor(id);
}

function newFile() {
  dialog('New file', '<p style="margin-top:0">File name, e.g. <code>Helper.java</code></p><input type="text" id="nfName" value="">', [
    { label: 'Cancel' },
    {
      label: 'OK', primary: true, action: () => {
        const name = $('#nfName').value.trim();
        if (!/^[A-Za-z0-9_][A-Za-z0-9_.-]*$/.test(name)) { toast('Geçersiz dosya adı'); return false; }
        const idx = E.files.findIndex((f) => f.name === name);
        if (idx >= 0) { openFile(idx); return true; }
        const cls = name.replace(/\.java$/i, '');
        const content = /\.java$/i.test(name) ? `class ${cls} {\n\n}\n` : '';
        const firstRO = E.files.findIndex((f) => f.readonly);
        const pos = firstRO < 0 ? E.files.length : firstRO;
        E.files.splice(pos, 0, { name, content, readonly: false, required: false });
        openFile(pos);
        E.dirty = true;
        save(true);
        return true;
      },
    },
  ], () => $('#nfName').focus());
}

// ----------------------------------------------------------------------- results
function applyAnnotations(list) {
  E.files.forEach((f) => { f.annotations = []; });
  (list || []).forEach((a) => { const f = E.files.find((x) => x.name === a.file); if (f) f.annotations.push(a); });
  E.files.forEach((f) => E.editor.setAnnotations(f));
}

function openSection(key, open = true) {
  $$('#acc h4').forEach((h) => h.classList.toggle('on', open && h.dataset.k === key));
  $$('#acc .ac').forEach((c) => c.classList.toggle('on', open && c.dataset.k === key));
}

function renderResults(st, openKey) {
  const g = st.grade;
  $('#hGrade').classList.toggle('hidden', g == null && !st.evaluatedAt);
  $('#gradeTxt').textContent = 'Proposed grade: ' + (g == null ? '-' : g + ' / 100');
  const info = [];
  if (st.evaluatedAt) info.push('Evaluated: ' + st.evaluatedAt);
  if (st.total != null && st.compiled !== false) info.push(`Checks passed: ${st.passed} of ${st.total}`);
  info.push('Evaluations: ' + (st.evalCount || 0));
  $('#gradeInfo').textContent = info.join('\n');
  const comp = st.compilation || '';
  $('#compOut').innerHTML = esc(comp).replace(/^([\w.$-]+\.java):(\d+): error:/gm,
    (m, f, r) => `<a class="jump" data-file="${f}" data-row="${r - 1}">${f}:${r}</a>: error:`);
  const rep = st.report ? st.report.replace(/\n+$/, '').split('\n').map((l) => '> ' + l).join('\n') : '';
  $('#report').textContent = rep;
  $('#evalCount').textContent = st.evalCount || 0;
  if (openKey) openSection(openKey, true);
}

async function evaluate() {
  if (!E || E.busy) return;
  E.busy = true;
  const close = busy('Evaluating...');
  try {
    const st = await apiPost('/api/a/' + enc(E.id) + '/evaluate', studentPayload());
    if (!E) return;
    E.dirty = false;
    $('#bSave').disabled = true;
    E.st = st;
    applyAnnotations(st.annotations || []);
    renderTabs();
    toggleRight(true);
    renderResults(st, st.compiled === false ? 'comp' : 'comm');
  } catch (e) {
    alert('Evaluation failed: ' + e.message);
  } finally {
    close();
    if (E) E.busy = false;
  }
}

// ----------------------------------------------------------------------- run + console
function showConsole(show) {
  $('#console').classList.toggle('hidden', !show);
  if (show) setTimeout(() => $('#conIn').focus(), 30);
}
function conWrite(text, cls) {
  const out = $('#conOut');
  if (!out) return;
  const atBottom = out.scrollHeight - out.scrollTop - out.clientHeight < 40;
  if (cls) { const s = document.createElement('span'); s.className = cls; s.textContent = text; out.appendChild(s); }
  else out.appendChild(document.createTextNode(text));
  if (atBottom) out.scrollTop = out.scrollHeight;
}
function setupConsole() {
  const c = $('#console');
  let drag = null;
  $('#conTitle').addEventListener('mousedown', (e) => {
    if (e.target.closest('button')) return;
    const r = c.getBoundingClientRect();
    drag = { dx: e.clientX - r.left, dy: e.clientY - r.top };
    e.preventDefault();
  });
  window.addEventListener('mousemove', (e) => {
    if (!drag) return;
    c.style.left = Math.max(0, e.clientX - drag.dx) + 'px';
    c.style.top = Math.max(0, e.clientY - drag.dy) + 'px';
  });
  window.addEventListener('mouseup', () => { drag = null; });
  $('#conClose').onclick = () => { stopRun(); showConsole(false); };
  $('#conClear').onclick = () => { $('#conOut').textContent = ''; };
  $('#conStop').onclick = stopRun;
  $('#conIn').addEventListener('keydown', async (e) => {
    if (e.key !== 'Enter') return;
    const v = e.target.value;
    e.target.value = '';
    conWrite(v + '\n', 'inp');
    if (E && E.runId && E.running) { try { await apiPost('/api/run/' + E.runId + '/input', { text: v }); } catch (err) { /* ignore */ } }
  });
}
function stopPolling() { if (E && E.poll) { clearTimeout(E.poll); E.poll = null; } }
async function stopRun() {
  if (E && E.runId && E.running) { try { await apiPost('/api/run/' + E.runId + '/stop'); } catch (e) { /* ignore */ } }
}
async function run() {
  if (!E || E.busy) return;
  await stopRun();
  stopPolling();
  showConsole(true);
  $('#conOut').textContent = '';
  $('#conStatus').textContent = 'compiling...';
  let r;
  try { r = await apiPost('/api/a/' + enc(E.id) + '/run', studentPayload()); } catch (e) { conWrite('Error: ' + e.message + '\n', 'err'); return; }
  if (!E) return;
  E.dirty = false;
  $('#bSave').disabled = true;
  applyAnnotations(r.annotations || []);
  renderTabs();
  if (!r.ok) {
    if (r.compilation) {
      conWrite(r.compilation, 'err');
      renderResults({ ...E.st, compilation: r.compilation, compiled: false }, 'comp');
    }
    conWrite((r.message || 'Could not run.') + '\n', 'err');
    $('#conStatus').textContent = 'not running';
    return;
  }
  $('#conStatus').textContent = 'running ' + r.mainClass;
  E.runId = r.runId;
  E.running = true;
  let next = 0;
  const tick = async () => {
    if (!E || E.runId !== r.runId) return;
    try {
      const p = await apiGet('/api/run/' + r.runId + '?from=' + next);
      if (!E || E.runId !== r.runId) return;
      if (p.text) conWrite(p.text);
      next = p.next;
      if (p.done) {
        E.running = false;
        conWrite(`\n[Program finished with exit code ${p.exit}]\n`, 'sys');
        $('#conStatus').textContent = 'finished';
        return;
      }
    } catch (e) { /* retry */ }
    E.poll = setTimeout(tick, 150);
  };
  tick();
}

// ----------------------------------------------------------------------- layout
function toggleRight(force) {
  const r = $('#vright');
  const show = force == null ? r.classList.contains('hidden') : force;
  r.classList.toggle('hidden', !show);
  $('#vsplit').classList.toggle('hidden', !show);
  lsSet('vpl.rightHidden', show ? '' : '1');
  setTimeout(() => E && E.editor.resize(), 20);
}
function setupSplitter() {
  const sp = $('#vsplit');
  const right = $('#vright');
  const saved = parseInt(lsGet('vpl.rightW', ''), 10);
  if (saved > 140) right.style.width = saved + 'px';
  let startX = 0;
  let startW = 0;
  const move = (e) => {
    const w = Math.max(150, Math.min(window.innerWidth - 300, startW - (e.clientX - startX)));
    right.style.width = w + 'px';
    if (E) E.editor.resize();
  };
  const up = () => {
    window.removeEventListener('mousemove', move);
    window.removeEventListener('mouseup', up);
    document.body.style.userSelect = '';
    lsSet('vpl.rightW', parseInt(right.style.width, 10));
  };
  sp.addEventListener('mousedown', (e) => {
    startX = e.clientX;
    startW = right.getBoundingClientRect().width;
    document.body.style.userSelect = 'none';
    window.addEventListener('mousemove', move);
    window.addEventListener('mouseup', up);
  });
}
function toggleFull() {
  const ide = $('#vplide');
  if (!ide) return;
  const on = !ide.classList.contains('fullscreen');
  ide.classList.toggle('fullscreen', on);
  document.body.style.overflow = on ? 'hidden' : '';
  $('#bFull').innerHTML = ic(on ? 'compress' : 'expand');
  fitIde();
}

// ----------------------------------------------------------------------- dialogs
function helpFab() { return '<button class="help-fab" id="fab" title="Yardım">?</button>'; }
function bindHelpFab() { const f = $('#fab'); if (f) f.onclick = help; }
function dialog(title, bodyHtml, buttons, onOpen) {
  const ov = document.createElement('div');
  ov.className = 'overlay';
  ov.innerHTML = `<div class="dlg"><div class="dh">${esc(title)}<span class="sp"></span><button class="x" data-close>✕</button></div><div class="db">${bodyHtml}</div>
    <div class="df">${buttons.map((b, i) => `<button class="${b.primary ? 'primary' : ''}" data-b="${i}">${esc(b.label)}</button>`).join('')}</div></div>`;
  document.body.appendChild(ov);
  const onKey = (e) => {
    if (!document.body.contains(ov)) { document.removeEventListener('keydown', onKey, true); return; }
    if (e.key === 'Escape') { e.preventDefault(); e.stopPropagation(); close(); }
    if (e.key === 'Enter') { const i = buttons.findIndex((b) => b.primary); if (i >= 0) { e.preventDefault(); $(`[data-b="${i}"]`, ov).click(); } }
  };
  const close = () => { ov.remove(); document.removeEventListener('keydown', onKey, true); };
  document.addEventListener('keydown', onKey, true);
  ov.addEventListener('click', (e) => {
    if (e.target === ov || e.target.closest('[data-close]')) { close(); return; }
    const b = e.target.closest('[data-b]');
    if (!b) return;
    const def = buttons[+b.dataset.b];
    if (def.action && def.action() === false) return;
    close();
  });
  if (onOpen) setTimeout(onOpen, 20);
  return close;
}
function busy(text) {
  const ov = document.createElement('div');
  ov.className = 'overlay';
  ov.innerHTML = `<div class="dlg"><div class="dh">${esc(text.replace(/\.+$/, ''))}</div><div class="busy"><div class="spinner"></div>${esc(text)}</div></div>`;
  document.body.appendChild(ov);
  return () => ov.remove();
}
function help() {
  const mod = /Mac/i.test(navigator.platform) ? '⌘' : 'Ctrl';
  dialog('About', `
    <table>
      <tr><td>${ic('plus-square', false)}</td><td>Daha fazla göster: yeni dosya, dosya sil</td></tr>
      <tr><td>${ic('save', false)}</td><td>Kaydet <kbd>${mod}</kbd>+<kbd>S</kbd></td></tr>
      <tr><td>${ic('rocket', false)}</td><td>Run: derler ve <code>main</code>'i konsolda çalıştırır <kbd>Ctrl</kbd>+<kbd>F11</kbd> / <kbd>Alt</kbd>+<kbd>R</kbd></td></tr>
      <tr><td>${ic('check-square-o', false)}</td><td>Evaluate: gizli testler, sayı = kaç kez değerlendirdiğin <kbd>Shift</kbd>+<kbd>F11</kbd> / <kbd>Alt</kbd>+<kbd>E</kbd></td></tr>
      <tr><td>${ic('commenting', false)}</td><td>Sağdaki paneli (not, Compilation, Yorumlar) aç / kapa</td></tr>
      <tr><td>${ic('terminal', false)}</td><td>Console</td></tr>
      <tr><td>${ic('expand', false)}</td><td>Fullscreen <kbd>Alt</kbd>+<kbd>F</kbd></td></tr>
    </table>
    <p style="margin-bottom:0">${ic('shield', false)} gerekli dosya, ${ic('lock', false)} salt okunur dosya. Kodun <code>work</code> klasörüne kaydedilir.
    Editör: <b>${E && E.ace ? 'Ace ' + (window.ace && window.ace.version || '') : 'sade (offline)'}</b>.</p>`, [{ label: 'Close', primary: true }]);
}

// ======================================================================= start
(async function start() {
  try { SETTINGS = await apiGet('/api/settings'); } catch (e) { /* defaults */ }
  route();
})();
