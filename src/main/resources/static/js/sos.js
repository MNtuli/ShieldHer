/* SOS screen. Every message below comes from the server's response, never an assumed success. */
const STATUS_LABEL = { PENDING: 'Sending', SENT: 'Sent', FAILED: 'Failed', NO_CONTACTS: 'No contacts' };
const SHARE_KEY = 'shieldher_share_location';
const LOCATION_TIMEOUT_MS = 8000;

if (requireLogin()) init();

function init() {
  const btn = document.getElementById('sos-btn');
  const share = document.getElementById('share-location');
  try { share.checked = localStorage.getItem(SHARE_KEY) === 'true'; } catch (e) { /* ignore */ }
  share.addEventListener('change', () => { try { localStorage.setItem(SHARE_KEY, String(share.checked)); } catch (e) { /* ignore */ } });
  document.getElementById('logout-btn').addEventListener('click', logout);
  btn.addEventListener('click', sendSos);
  loadAlerts();
}

function showResult(kind, text, note) {
  const box = document.getElementById('result');
  box.textContent = '';
  const div = document.createElement('div');
  div.className = 'msg ' + kind;
  div.textContent = text;
  (Array.isArray(note) ? note : note ? [note] : []).forEach(n => {
    const small = document.createElement('small');
    small.textContent = n;
    div.appendChild(small);
  });
  box.appendChild(div);
}

function getLocation() {
  // Resolves { coords } or { reason }. Never rejects: the alert must go out even without location.
  return new Promise(resolve => {
    if (!navigator.geolocation) return resolve({ reason: 'Location is not available on this device.' });
    navigator.geolocation.getCurrentPosition(
      pos => resolve({ coords: { latitude: pos.coords.latitude, longitude: pos.coords.longitude } }),
      err => resolve({ reason: err.code === 1 ? 'Location permission was denied.' : 'Could not get your location.' }),
      { enableHighAccuracy: true, timeout: LOCATION_TIMEOUT_MS, maximumAge: 60000 }
    );
  });
}

async function sendSos() {
  const btn = document.getElementById('sos-btn');
  if (btn.disabled) return;
  btn.disabled = true;
  showResult('info', 'Sending...');
  const body = {};
  const message = document.getElementById('message').value.trim();
  if (message) body.message = message;

  let locationNote = null;
  if (document.getElementById('share-location').checked) {
    const loc = await getLocation();
    if (loc.coords) Object.assign(body, loc.coords);
    else locationNote = loc.reason + ' Alert sent without your location.';
  }

  try {
    const res = await apiFetch('/api/alerts', { method: 'POST', body });
    if (res.ok) renderAlertResult(res.data, locationNote);
    else if (res.status >= 500) showResult('bad', 'Could not send. Call emergency services.', errorText(res.data, 'Server error.') + ' Your alert may not have been sent.');
    else if (res.status !== 401) showResult('bad', 'Alert not sent: ' + errorText(res.data, 'Please check your details.'));
    await loadAlerts();
  } catch (e) {
    showResult('bad', 'Could not reach the server. Your alert was NOT sent. Call emergency services.');
  } finally {
    btn.disabled = false;
  }
}

function renderAlertResult(a, locationNote) {
  const notes = [];
  if (a.deliveryMode === 'SIMULATED') notes.push('Demo mode: no real message was sent.');
  if (locationNote) notes.push(locationNote);
  switch (a.status) {
    case 'SENT':
      return showResult('ok', 'Alert sent to ' + a.contactsNotified + ' of ' + a.contactsTotal + ' contacts', notes);
    case 'FAILED':
      return showResult('bad', 'Could not send. Call emergency services.', notes);
    case 'NO_CONTACTS':
      return showResult('warn', 'No contacts to notify. Add a contact.', notes);
    case 'PENDING':
      return showResult('info', 'Sending...', notes);
    default:
      return showResult('warn', 'Alert saved, but its status is unknown. Call emergency services if you are in danger.', notes);
  }
}

async function loadAlerts() {
  const list = document.getElementById('alerts');
  const empty = document.getElementById('alerts-empty');
  const err = document.getElementById('alerts-error');
  err.hidden = true;
  try {
    const res = await apiFetch('/api/alerts');
    if (!res.ok) { if (res.status !== 401) { err.textContent = 'Could not load recent alerts.'; err.hidden = false; } return; }
    list.textContent = '';
    empty.hidden = res.data.length > 0;
    res.data.slice(0, 10).forEach(a => {
      const li = document.createElement('li');
      const badge = document.createElement('span');
      badge.className = 'badge ' + a.status;
      badge.textContent = STATUS_LABEL[a.status] || a.status;
      const when = document.createElement('span');
      when.className = 'muted';
      when.textContent = ' ' + new Date(a.createdAt).toLocaleString();
      const detail = document.createElement('div');
      detail.className = 'muted';
      detail.textContent = a.contactsNotified + ' of ' + a.contactsTotal + ' contacts' + (a.deliveryMode === 'SIMULATED' ? ' (demo mode)' : '');
      li.append(badge, when, detail);
      list.appendChild(li);
    });
  } catch (e) {
    err.textContent = 'Could not load recent alerts.';
    err.hidden = false;
  }
}
