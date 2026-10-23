/* Shared helpers. The UI only ever shows what the server really returned. */
const TOKEN_KEY = 'shieldher_token';
const NAME_KEY = 'shieldher_name';

function getToken() { try { return localStorage.getItem(TOKEN_KEY); } catch (e) { return null; } }
function saveSession(auth) {
  try { localStorage.setItem(TOKEN_KEY, auth.token); localStorage.setItem(NAME_KEY, auth.fullName || ''); } catch (e) { /* storage blocked */ }
}
function clearSession() {
  try { localStorage.removeItem(TOKEN_KEY); localStorage.removeItem(NAME_KEY); } catch (e) { /* ignore */ }
}
function requireLogin() {
  if (!getToken()) { window.location.replace('login.html'); return false; }
  return true;
}
function logout() { clearSession(); window.location.replace('login.html'); }

/**
 * Calls the API. Resolves { ok, status, data } for any HTTP response.
 * Rejects only when the server could not be reached (callers must treat that as "not sent").
 * options.auth === false: public endpoint (no token, no redirect on 401).
 */
async function apiFetch(path, options = {}) {
  const useAuth = options.auth !== false;
  const headers = { 'Content-Type': 'application/json' };
  if (useAuth && getToken()) headers['Authorization'] = 'Bearer ' + getToken();
  const res = await fetch(path, {
    method: options.method || 'GET',
    headers,
    body: options.body !== undefined ? JSON.stringify(options.body) : undefined
  });
  if (useAuth && res.status === 401) { logout(); }
  let data = null;
  try { data = await res.json(); } catch (e) { /* empty or non-JSON body */ }
  return { ok: res.ok, status: res.status, data };
}

/** Turns an error response into text: the server's message plus any field errors. */
function errorText(data, fallback) {
  if (!data) return fallback;
  const parts = [data.message || fallback];
  if (data.fieldErrors) Object.values(data.fieldErrors).forEach(m => parts.push(m));
  return parts.join(' ');
}
